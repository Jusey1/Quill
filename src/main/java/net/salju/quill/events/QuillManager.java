package net.salju.quill.events;

import net.salju.quill.init.QuillTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos;

public class QuillManager {
	public static int getEnchantmentLevel(ItemStack stack, Level world, String id, String name) {
		return stack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(id, name))));
	}

	public static float getBonusDamage(ItemStack stack) {
		if (stack.is(QuillTags.NETHER)) {
			return 4.0F;
		} else if (stack.is(QuillTags.DIAMOND)) {
			return 3.0F;
		} else if (stack.is(QuillTags.IRON)) {
			return 2.0F;
		} else if (stack.is(QuillTags.STONE)) {
			return 1.0F;
		}
		return 0.0F;
	}

	public static float reduceBonusSpeed(ItemStack stack) {
		if (stack.is(QuillTags.NETHER)) {
			return -0.2F;
		} else if (stack.is(QuillTags.DIAMOND)) {
			return -0.1F;
		}
		return 0.0F;
	}

	public static int getMaxRiders(EntityType<?> type) {
		if (type.is(QuillTags.DOUBLE)) {
			return 2;
		} else if (type.is(QuillTags.TRIPLE)) {
			return 3;
		} else if (type.is(QuillTags.QUAD)) {
			return 4;
		} else if (type.is(QuillTags.PENTA)) {
			return 5;
		}
		return 0;
	}

	public static boolean isValidRepairItem(ItemStack stack, ItemStack material) {
		Repairable data = stack.get(DataComponents.REPAIRABLE);
		if (data != null) {
			return data.isValidRepairItem(material);
		} else if (material.is(Items.STICK)) {
			return (stack.is(Items.BOW) || stack.is(Items.FISHING_ROD));
		} else if (material.is(Items.IRON_INGOT)) {
			return (stack.is(Items.SHEARS) || stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.CROSSBOW));
		} else if (material.is(Items.COPPER_INGOT)) {
			return (stack.is(Items.BRUSH));
		} else if (material.is(Items.PRISMARINE_SHARD)) {
			return (stack.is(Items.TRIDENT));
		}
		return false;
	}

	public static boolean isValidMagneticItem(Player player, ItemStack stack) {
		int i = player.getInventory().getSlotWithRemainingSpace(stack);
		if (player.isCreative()) {
			return true;
		} else if (player.isSpectator()) {
			return false;
		}
		return (i < 0 ? (player.getInventory().getFreeSlot() >= 0) : true);
	}

	public static boolean getCampfire(ServerLevelAccessor lvl, BlockPos pos, int radius) {
		int minX = SectionPos.blockToSectionCoord(pos.getX() - radius);
		int minZ = SectionPos.blockToSectionCoord(pos.getZ() - radius);
		int maxX = SectionPos.blockToSectionCoord(pos.getX() + radius);
		int maxZ = SectionPos.blockToSectionCoord(pos.getZ() + radius);
		boolean check = false;
		for (int chunkX = minX; chunkX <= maxX; chunkX++) {
			for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
				LevelChunk chunk = lvl.getChunkSource().getChunk(chunkX, chunkZ, false);
				if (chunk != null) {
					for (BlockPos target : chunk.getBlockEntitiesPos()) {
						BlockState state = chunk.getBlockState(target);
						if (state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) {
							if (state.getValue(CampfireBlock.LIT)) {
								if (pos.closerThan(target, radius)) {
									check = true;
									break;
								}
							}
						}
					}
				}
			}
		}
		return check;
	}
}