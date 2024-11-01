package net.salju.quill.events;

import net.salju.quill.Quill;
import net.salju.quill.init.QuillTags;
import net.salju.quill.init.QuillItems;
import net.salju.quill.init.QuillFrogs;
import net.salju.quill.init.QuillConfig;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
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
				for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(8.0 * i))) {
					if (item.isAlive() && player.isCrouching() && QuillManager.isValidMagneticItem(player, item.getItem())) {
						item.setNoGravity(true);
						Vec3 v = player.getEyePosition().subtract(item.position());
						if (item.level().isClientSide) {
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
	public static void onHurt(LivingDamageEvent.Pre event) {
		if (event.getSource().getDirectEntity() != null) {
			Entity direct = event.getSource().getDirectEntity();
			LivingEntity target = event.getEntity();
			if (direct instanceof Projectile proj) {
				if (proj.getPersistentData().getDouble("Sharpshooter") > 0) {
					double x = direct.getPersistentData().getDouble("Sharpshooter");
					event.setNewDamage((float) (Math.round(Mth.nextInt(RandomSource.create(), 7, 11)) + (2.5 * x)));
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
	public static void onDeath(LivingDeathEvent event) {
		if (event.getEntity().getType() == EntityType.MAGMA_CUBE && event.getSource().getEntity() instanceof Frog frog) {
			if (frog.getVariant() == QuillFrogs.WITCH && event.getEntity().level() instanceof ServerLevel lvl) {
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
		if (event.getEntity() != null && event.getDamageSource().getDirectEntity() != null) {
			ItemStack stack = event.getEntity().getUseItem();
			if (event.getDamageSource().getDirectEntity() instanceof LivingEntity target) {
				int i = QuillManager.getEnchantmentLevel(stack, target.level(), Quill.MODID, "spikes");
				int e = QuillManager.getEnchantmentLevel(stack, target.level(), Quill.MODID, "blazing");
				int u = QuillManager.getEnchantmentLevel(stack, target.level(), Quill.MODID, "curse_of_zombie");
				if (i > 0) {
					if (event.getEntity() instanceof Player player) {
						float fd = (target instanceof Phantom ? (float) (i * 2.5F) : (float) i);
						target.hurt(player.damageSources().thorns(player), fd);
						if (player.level() instanceof ServerLevel lvl && fd > 2.0F) {
							lvl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5), target.getZ(), (int) (fd * 0.5F), 0.15, 0.0, 0.15, 0.25);
						}
					} else {
						float fd = (target instanceof Phantom ? (float) (i * 2.5F) : (float) i);
						target.hurt(event.getEntity().damageSources().thorns(event.getEntity()), fd);
					}
				}
				if (u > 0) {
					if (target.level() instanceof ServerLevel lvl) {
						lvl.playSound(null, target.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.25F, Mth.nextFloat(lvl.getRandom(), 0.12F, 0.21F));
						lvl.playSound(null, target.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.25F, Mth.nextFloat(lvl.getRandom(), 0.12F, 0.21F));
					}
					event.getEntity().stopUsingItem();
					if (event.getEntity() instanceof Player player) {
						player.getCooldowns().addCooldown(stack, 600);
					}
					for (Zombie billy : event.getEntity().level().getEntitiesOfClass(Zombie.class, event.getEntity().getBoundingBox().inflate(64.0D))) {
						if (billy.level() instanceof ServerLevel lvl) {
							lvl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, billy.getX(), billy.getY() + 1.05, billy.getZ(), 12, 0.45, 0.25, 0.45, 0);
						}
						if (target.isAlive() && !(target instanceof Zombie) && Math.random() >= 0.95) {
							billy.setTarget(target);
						} else {
							billy.setTarget(event.getEntity());
						}
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
		if (QuillConfig.CAMPFIRE.get() && event.getEntity() instanceof Enemy && event.getSpawnType() == EntitySpawnReason.NATURAL && QuillManager.getCampfire(event.getLevel(), event.getEntity().blockPosition(), 64)) {
			event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
		}
	}

	@SubscribeEvent
	public static void onTick(EntityTickEvent.Post event) {
		if (event.getEntity() instanceof Villager target) {
			if (QuillConfig.TAXI.get()) {
				Player player = target.level().getNearestPlayer(target, 2);
				if (player != null && player.isPassenger()) {
					if (player.getVehicle().getType().is(QuillTags.DOUBLE) && player.getVehicle().getPassengers().size() < 2) {
						target.startRiding(player.getVehicle());
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
			} else if (target.getType().is(QuillTags.PETS) && QuillConfig.PETS.get() && target instanceof TamableAnimal animal) {
				if (animal.isOwnedBy(player)) {
					if (animal.getPersistentData().getBoolean("isWandering")) {
						animal.getPersistentData().remove("isWandering");
						player.swing(InteractionHand.MAIN_HAND, true);
						event.setCanceled(true);
					} else if (animal.isOrderedToSit()) {
						animal.getPersistentData().putBoolean("isWandering", true);
					}
				}
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
		if (state.is(BlockTags.CLIMBABLE) && state.getBlock().asItem() == stack.getItem()) {
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
			Block target = state.getBlock();
			if (target instanceof CropBlock crops && crops.isMaxAge(state)) {
				player.swing(event.getHand());
				if (world instanceof ServerLevel lvl) {
					lvl.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
					stack.hurtAndBreak(1, player, stack.getEquipmentSlot());
					List<ItemStack> drops = target.getDrops(state, lvl, pos, null, player, stack);
					ItemStack base = crops.getCloneItemStack(lvl, pos, state);
					lvl.setBlock(pos, crops.getStateForAge(0), 2);
					double x = (pos.getX() + 0.5);
					double y = (pos.getY() + 0.5);
					double z = (pos.getZ() + 0.5);
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
	public static void onGrindstone(GrindstoneEvent.OnPlaceItem event) {
		if (QuillConfig.ENCHS.get() && QuillConfig.GRIND.get() && event.getTopItem().isEnchanted() && event.getBottomItem().is(Items.ENCHANTED_BOOK)) {
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
		if (QuillConfig.ENCHS.get()) {
			if (QuillConfig.ANBOOK.get() && (event.getLeft().isEnchantable() || event.getLeft().is(Items.ENCHANTED_BOOK) || event.getLeft().isEnchanted()) && (event.getRight().is(Items.ENCHANTED_BOOK) || (event.getRight().is(event.getLeft().getItem()) && event.getRight().isEnchanted()))) {
				ItemEnchantments.Mutable map = event.getLeft().isEnchanted() || event.getLeft().is(Items.ENCHANTED_BOOK) ? new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(event.getLeft().copy())) : new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
				ItemEnchantments.Mutable book = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(event.getRight()));
				int i = 0;
				int m = event.getLeft().is(QuillTags.DOUBENCHS) ? QuillConfig.MAXENCH.get() * 2 : QuillConfig.MAXENCH.get();
				for (Holder<Enchantment> e : book.keySet()) {
					int p = Math.min(map.getLevel(e) == book.getLevel(e) ? book.getLevel(e) + 1 : Math.max(book.getLevel(e), map.getLevel(e)), e.value().getMaxLevel());
					int t = 0;
					boolean check = event.getLeft().is(Items.ENCHANTED_BOOK) ? true : event.getLeft().supportsEnchantment(e);
					for (Holder<Enchantment> target : map.keySet()) {
						if (!target.is(EnchantmentTags.CURSE)) {
							t++;
						}
						if ((e != target && !EnchantmentHelper.isEnchantmentCompatible(map.keySet(), e)) || (e == target && book.getLevel(e) == p)) {
							check = false;
						}
					}
					boolean always = (e.is(EnchantmentTags.CURSE) || p > book.getLevel(e));
					if (check && (always || t < m)) {
						map.set(e, book.getLevel(e));
						int c = 4;
						i = (i + ((p * c) / 2));
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
					event.setCost(Math.min(i, QuillConfig.MAXANBOOKCOST.get()));
					event.setMaterialCost(1);
					event.setOutput(stack);
				} else {
					event.setCanceled(true);
				}
			} else if (QuillConfig.RENAME.get() && !event.getLeft().isEmpty() && event.getRight().isEmpty() && event.getName() != null && !StringUtil.isBlank(event.getName()) && !event.getName().equals(event.getLeft().getHoverName().getString())) {
				ItemStack stack = event.getLeft().copy();
				stack.set(DataComponents.CUSTOM_NAME, Component.literal(event.getName()));
				event.setCost(1);
				event.setMaterialCost(1);
				event.setOutput(stack);
			}
		}
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = player.getMainHandItem();
		BlockPos pos = event.getPos();
		BlockState state = event.getState();
		LevelAccessor world = event.getLevel();
		double x = (pos.getX() + 0.5);
		double y = (pos.getY() + 0.5);
		double z = (pos.getZ() + 0.5);
		int i = QuillManager.getEnchantmentLevel(stack, player.level(), Quill.MODID, "auto_smelt");
		if (state.is(Tags.Blocks.ORES) && world instanceof ServerLevel lvl && i > 0) {
			Block target = state.getBlock();
			List<ItemStack> drops = target.getDrops(state, lvl, pos, null, player, stack);
			for (ItemStack item : drops) {
				Optional<RecipeHolder<SmeltingRecipe>> recipe = lvl.getServer().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(item), lvl);
				if (recipe.isPresent()) {
					ItemStack smelt = recipe.get().value().assemble(new SingleRecipeInput(item), lvl.registryAccess());
					smelt.setCount(item.getCount());
					lvl.addFreshEntity(new ItemEntity(lvl, x, y, z, smelt));
					if (!event.isCanceled()) {
						world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
						stack.hurtAndBreak(1, player, stack.getEquipmentSlot());
						lvl.sendParticles(ParticleTypes.FLAME, x, y, z, 4, 0.35, 0.35, 0.35, 0);
						lvl.addFreshEntity(new ExperienceOrb(lvl, x, y, z, 2));
						event.setCanceled(true);
					}
				}
			}
		}
	}
}