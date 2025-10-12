package net.salju.quill.events;

import net.salju.quill.Quill;
import net.salju.quill.init.QuillTags;
import net.salju.quill.init.QuillItems;
import net.salju.quill.init.QuillConfig;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.InteractionHand;
import java.util.Optional;
import java.util.List;

@EventBusSubscriber
public class QuillEvents {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && player.getItemBySlot(EquipmentSlot.CHEST).isEnchanted()) {
			int i = QuillManager.getEnchantmentLevel(player.getItemBySlot(EquipmentSlot.CHEST), player.level(), Quill.MODID, "magnetic");
			if (i > 0) {
				for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(QuillConfig.MAGRANGE.get() * i))) {
					if (item.isAlive() && player.isCrouching() && QuillManager.isValidMagneticItem(player, item.getItem())) {
						item.setNoGravity(true);
						Vec3 v = player.getEyePosition().subtract(item.position());
						if (item.level().isClientSide()) {
							item.yOld = item.getY();
						}
						item.setDeltaMovement(item.getDeltaMovement().scale(0.95D).add(v.normalize().scale(0.05D)));
					} else {
						item.setNoGravity(false);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onHurt(LivingIncomingDamageEvent event) {
		if (event.getSource().getDirectEntity() != null) {
			Entity direct = event.getSource().getDirectEntity();
			LivingEntity target = event.getEntity();
			if (direct instanceof Projectile proj && proj.getPersistentData().getDouble("Sharpshooter").isPresent()) {
				if (proj.getPersistentData().getDouble("Sharpshooter").get() > 0) {
					double x = proj.getPersistentData().getDouble("Sharpshooter").get();
					event.setAmount((float) (Math.round(Mth.nextInt(target.getRandom(), 7, 11)) + (2.5 * x)));
				}
			} else if (direct instanceof LivingEntity && target.isUsingItem() && QuillConfig.USER.get()) {
				if (target.getUseItem().getUseDuration(target) <= 64) {
					target.stopUsingItem();
					if (target instanceof Player player) {
						player.getCooldowns().addCooldown(target.getUseItem(), 12);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onDeathDrops(LivingDropsEvent event) {
		if (event.getEntity().getType().equals(EntityType.MAGMA_CUBE) && event.getSource().getEntity() instanceof Frog frog) {
			if (frog.getVariant().is(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "witch")) && event.getEntity().level() instanceof ServerLevel lvl) {
				event.getEntity().spawnAtLocation(lvl, new ItemStack(QuillItems.AZURE.get()));
			}
		}
	}

	@SubscribeEvent
	public static void onProjectile(LivingGetProjectileEvent event) {
		int i = QuillManager.getEnchantmentLevel(event.getProjectileWeaponItemStack(), event.getEntity().level(), "minecraft", "infinity");
		if (i > 0 && event.getProjectileItemStack().isEmpty()) {
			event.setProjectileItemStack(new ItemStack(Items.ARROW));
		}
	}

	@SubscribeEvent
	public static void onBlockAttack(LivingShieldBlockEvent event) {
		if (event.getDamageSource().getDirectEntity() != null && event.getBlocked()) {
			ItemStack stack = event.getEntity().getUseItem();
			if (event.getDamageSource().getDirectEntity() instanceof LivingEntity target) {
				int i = QuillManager.getEnchantmentLevel(stack, target.level(), Quill.MODID, "spikes");
				int e = QuillManager.getEnchantmentLevel(stack, target.level(), Quill.MODID, "blazing");
				if (i > 0) {
					if (event.getEntity() instanceof Player player) {
						float fd = (target instanceof Phantom ? i * 2.5F : (float) i);
						target.hurt(player.damageSources().thorns(player), fd);
						if (player.level() instanceof ServerLevel lvl && fd > 2.0F) {
							lvl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5), target.getZ(), (int) (fd * 0.5F), 0.15, 0.0, 0.15, 0.25);
						}
					} else {
						float fd = (target instanceof Phantom ? i * 2.5F : (float) i);
						target.hurt(event.getEntity().damageSources().thorns(event.getEntity()), fd);
					}
				}
				if (e > 0 && !target.fireImmune()) {
					target.setRemainingFireTicks(e * 80);
				}
			} else if (event.getDamageSource().getDirectEntity() instanceof Projectile target) {
				int d = QuillManager.getEnchantmentLevel(stack, target.level(), Quill.MODID, "deflecting");
				if (d > 0 && target instanceof Arrow) {
					double x = Mth.nextDouble(target.getRandom(), -0.15, 0.15);
					double y = Mth.nextDouble(target.getRandom(), -0.07, 0.07);
					Arrow newbie = new Arrow(event.getEntity().level(), event.getEntity(), new ItemStack(Items.ARROW), new ItemStack(Items.BOW));
					newbie.setCritArrow(true);
					newbie.setBaseDamage(0.75 * d);
					newbie.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
					newbie.shoot(event.getEntity().getViewVector(1.0F).x + x, event.getEntity().getViewVector(1.0F).y + y, event.getEntity().getViewVector(1.0F).z, 0.65F * 3.0F, 1.0F);
					event.getEntity().level().addFreshEntity(newbie);
					target.discard();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onMobSpawned(MobSpawnEvent.PositionCheck event) {
		if (QuillConfig.CAMPFIRE.get() && event.getEntity() instanceof Enemy && event.getSpawnType().equals(EntitySpawnReason.NATURAL) && QuillManager.getCampfire(event.getLevel(), event.getEntity().blockPosition(), QuillConfig.CAMPRANGE.get())) {
			event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
		}
	}

	@SubscribeEvent
	public static void onTick(EntityTickEvent.Post event) {
		if (event.getEntity().getType().is(QuillTags.RIDERS)) {
			if (QuillConfig.TAXI.get()) {
				Player player = event.getEntity().level().getNearestPlayer(event.getEntity(), 6);
				if (player != null && player.isPassenger()) {
					int i = QuillManager.getMaxRiders(player.getVehicle().getType());
					if (player.getVehicle().getPassengers().size() < i && event.getEntity().distanceTo(player.getVehicle()) <= 1.0F * i) {
						event.getEntity().startRiding(player.getVehicle());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onMobAttack(LivingChangeTargetEvent event) {
		if (event.getEntity().getType().is(QuillTags.PEACEFUL) && QuillConfig.PEACEFUL.get() && event.getOriginalAboutToBeSetTarget() instanceof Player) {
			if (event.getEntity().getLastHurtByMob() != event.getOriginalAboutToBeSetTarget()) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onMobGrief(EntityMobGriefingEvent event) {
		if (event.getEntity().getType().is(QuillTags.NO) && QuillConfig.NO.get()) {
			event.setCanGrief(false);
		}
	}

	@SubscribeEvent
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Player player = event.getEntity();
		if (event.getTarget() instanceof LivingEntity target && player.isCrouching()) {
			if (target.isPassenger() && QuillConfig.KICK.get()) {
				player.swing(InteractionHand.MAIN_HAND, true);
				target.stopRiding();
			}
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();
		LevelAccessor world = event.getLevel();
		ItemStack stack = event.getItemStack();
		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		if (QuillConfig.LADDER.get() && state.is(QuillTags.LADDERS) && stack.is(state.getBlock().asItem())) {
			if (world.isEmptyBlock(pos.below())) {
				world.setBlock(pos.below(), state, 3);
				world.playSound(player, pos.below(), state.getSoundType(world, pos.below(), player).getPlaceSound(), SoundSource.BLOCKS);
				player.swing(event.getHand());
				if (!player.isCreative()) {
					stack.shrink(1);
				}
			} else if (world.isEmptyBlock(pos.above())) {
				world.setBlock(pos.above(), state, 3);
				world.playSound(player, pos.above(), state.getSoundType(world, pos.above(), player).getPlaceSound(), SoundSource.BLOCKS);
				player.swing(event.getHand());
				if (!player.isCreative()) {
					stack.shrink(1);
				}
			}
		} else if (stack.getItem() instanceof HoeItem && QuillConfig.FARMER.get()) {
			if (state.getBlock() instanceof CropBlock crops && crops.isMaxAge(state)) {
				player.swing(event.getHand());
				if (world instanceof ServerLevel lvl) {
					lvl.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
					stack.hurtAndBreak(1, player, stack.getEquipmentSlot());
					List<ItemStack> drops = crops.getDrops(state, lvl, pos, null, player, stack);
					ItemStack base = crops.getCloneItemStack(lvl, pos, state, true, player);
					lvl.setBlock(pos, crops.getStateForAge(0), 2);
					double x = pos.getX() + 0.5;
					double y = pos.getY() + 0.5;
					double z = pos.getZ() + 0.5;
					int f = QuillManager.getEnchantmentLevel(stack, player.level(), "minecraft", "fortune");
					for (ItemStack item : drops) {
						if (item.is(Tags.Items.CROPS)) {
							lvl.addFreshEntity(new ItemEntity(lvl, x, y, z, item));
							if (base.get(DataComponents.FOOD) == null && f > 0) {
								if (Math.random() <= 0.56) {
									int i = Mth.nextInt(RandomSource.create(), 0, f);
									if (i > 0) {
										item.setCount(i);
										lvl.addFreshEntity(new ItemEntity(lvl, x, y, z, item));
									}
								}
							}
						} else if (item.is(Tags.Items.SEEDS)) {
							item.setCount(1);
							if (Math.random() <= 0.45) {
								lvl.addFreshEntity(new ItemEntity(lvl, x, y, z, item));
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onAttributes(ItemAttributeModifierEvent event) {
		if (QuillConfig.WEAPONS.get()) {
			if (event.getItemStack().is(QuillTags.SPECIALS)) {
				event.replaceModifier(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.8F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
				event.replaceModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 7.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
			} else if (event.getItemStack().is(QuillTags.AXES)) {
				event.replaceModifier(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
				event.replaceModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 5.0F + QuillManager.getBonusDamage(event.getItemStack()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
			}
		}
	}

	@SubscribeEvent
	public static void onGrindstone(GrindstoneEvent.OnPlaceItem event) {
		if (QuillConfig.GRIND.get() && event.getTopItem().isEnchanted() && event.getBottomItem().is(Items.ENCHANTED_BOOK)) {
			ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
			ItemEnchantments.Mutable enchs = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(event.getTopItem()));
			ItemEnchantments.Mutable map = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(event.getBottomItem()));
			int i = 0;
			for (Holder<Enchantment> e : map.keySet()) {
				if (e.is(EnchantmentTags.CURSE)) {
					enchs.set(e, map.getLevel(e));
				} else {
					i += e.value().getMinCost(map.getLevel(e));
				}
			}
			EnchantmentHelper.setEnchantments(stack, enchs.toImmutable());
			event.setOutput(stack);
			event.setXp(i);
		}
	}

	@SubscribeEvent
	public static void onAnvil(AnvilUpdateEvent event) {
		if (QuillConfig.REPAIR.get() && QuillManager.isValidRepairItem(event.getLeft(), event.getRight()) && event.getLeft().isDamaged()) {
			ItemStack stack = event.getLeft().copy();
			int i = (stack.getDamageValue() > (stack.getMaxDamage() / 2) && event.getRight().getCount() > 1) ? 2 : 1;
			stack.setDamageValue(Math.max(stack.getDamageValue() - ((stack.getMaxDamage() / 2) * i), 0));
			event.setXpCost(i);
			event.setMaterialCost(i);
			event.setOutput(stack);
		} else if (QuillConfig.ANBOOK.get() && (event.getLeft().isEnchantable() || event.getLeft().is(Items.ENCHANTED_BOOK) || event.getLeft().isEnchanted()) && (event.getRight().is(Items.ENCHANTED_BOOK) || (event.getRight().is(event.getLeft().getItem()) && event.getRight().isEnchanted()))) {
			ItemEnchantments.Mutable map = event.getLeft().isEnchanted() || event.getLeft().is(Items.ENCHANTED_BOOK) ? new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(event.getLeft().copy())) : new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
			ItemEnchantments.Mutable book = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(event.getRight()));
			int i = 0;
			for (Holder<Enchantment> e : book.keySet()) {
				int p = Math.min(map.getLevel(e) == book.getLevel(e) ? book.getLevel(e) + 1 : book.getLevel(e), e.value().getMaxLevel());
				boolean check = event.getLeft().is(Items.ENCHANTED_BOOK) || event.getLeft().supportsEnchantment(e);
				for (Holder<Enchantment> target : map.keySet()) {
					if ((e != target && !EnchantmentHelper.isEnchantmentCompatible(map.keySet(), e)) || (e == target && map.getLevel(e) >= p)) {
						check = false;
					} else if  (e == target && map.getLevel(e) < p) {
						check = true;
					}
				}
				if (check && (e.is(EnchantmentTags.CURSE) || p > map.getLevel(e))) {
					map.set(e, p);
					i = (i + ((p * 4) / 2));
				}
			}
			if (i > 0) {
				ItemStack stack = event.getLeft().copy();
				if (event.getName() != null && !StringUtil.isBlank(event.getName()) && !event.getName().equals(stack.getHoverName().getString())) {
					stack.set(DataComponents.CUSTOM_NAME, Component.literal(event.getName()));
				}
				if (stack.isEnchanted()) {
					EnchantmentHelper.setEnchantments(stack, ItemEnchantments.EMPTY);
				}
				EnchantmentHelper.setEnchantments(stack, map.toImmutable());
				event.setXpCost(Math.min(i, QuillConfig.MAXANBOOKCOST.get()));
				event.setMaterialCost(1);
				event.setOutput(stack);
			} else {
				event.setCanceled(true);
			}
		} else if (QuillConfig.RENAME.get() && !event.getLeft().isEmpty() && event.getRight().isEmpty() && event.getName() != null && !StringUtil.isBlank(event.getName()) && !event.getName().equals(event.getLeft().getHoverName().getString())) {
			ItemStack stack = event.getLeft().copy();
			stack.set(DataComponents.CUSTOM_NAME, Component.literal(event.getName()));
			event.setXpCost(1);
			event.setMaterialCost(1);
			event.setOutput(stack);
		}
	}

	@SubscribeEvent
	public static void onBlockAttack(PlayerInteractEvent.LeftClickBlock event) {
		if (QuillConfig.WEAPONS.get() && event.getEntity().isCreative() && event.getItemStack().is(QuillTags.AXES)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockDrops(BlockDropsEvent event) {
		if (event.getBreaker() instanceof Player player && !event.getTool().isEmpty() && event.getLevel() instanceof ServerLevel lvl) {
			ItemStack stack = event.getTool();
			double x = event.getPos().getX() + 0.5;
			double y = event.getPos().getY() + 0.5;
			double z = event.getPos().getZ() + 0.5;
			int i = QuillManager.getEnchantmentLevel(stack, player.level(), Quill.MODID, "auto_smelt");
			if (event.getState().is(QuillTags.SMELT) && !player.isCreative() && i > 0) {
				for (ItemEntity ore : event.getDrops()) {
					Optional<RecipeHolder<SmeltingRecipe>> recipe = lvl.getServer().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(ore.getItem()), lvl);
					if (recipe.isPresent()) {
						ItemStack smelt = recipe.get().value().assemble(new SingleRecipeInput(ore.getItem()), lvl.registryAccess());
						smelt.setCount(ore.getItem().getCount());
						lvl.addFreshEntity(new ItemEntity(lvl, x, y, z, smelt));
						if (!event.isCanceled()) {
							lvl.sendParticles(ParticleTypes.FLAME, x, y, z, 4, 0.35, 0.35, 0.35, 0);
							lvl.addFreshEntity(new ExperienceOrb(lvl, x, y, z, 2));
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
}