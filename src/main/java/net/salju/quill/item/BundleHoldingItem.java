package net.salju.quill.item;

import net.salju.quill.init.QuillData;
import net.salju.quill.item.component.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.*;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class BundleHoldingItem extends Item {
	public BundleHoldingItem(Item.Properties props) {
		super(props);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, list, flag);
		list.accept(Component.translatable("item.quill.bundle.fullness", getContentWeight(stack), 256).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new BundleHoldingTooltip(stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY)));
	}

	@Override
	public ItemStack getDefaultInstance() {
		ItemStack stack = super.getDefaultInstance();
		stack.set(QuillData.BUNDLE, BundleHoldingContents.EMPTY);
		return stack;
	}

    @Override
    public boolean canFitInsideContainerItems() {
        return true;
    }

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getContentWeight(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.min(1 + 12 * getContentWeight(stack) / 256, 13);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 6711039;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel lvl, Entity target, EquipmentSlot slot) {
		super.inventoryTick(stack, lvl, target, slot);
		BundleHoldingContents data = stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY);
        BundleBomb fuse = stack.getOrDefault(QuillData.BOMB, BundleBomb.EMPTY);
        if (!lvl.isClientSide() && fuse.isBomb()) {
            this.explode(stack, lvl, target);
        }
		if (EnchantmentHelper.hasTag(stack, EnchantmentTags.CURSE)) {
			if (!data.isEmpty()) {
				data.getItems().clear();
			}
		}
	}

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity target) {
        BundleBomb fuse = stack.getOrDefault(QuillData.BOMB, BundleBomb.EMPTY);
        if (target.level() instanceof ServerLevel lvl && fuse.isBomb()) {
            this.explode(stack, lvl, target);
        }
        return super.onEntityItemUpdate(stack, target);
    }

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (action != ClickAction.SECONDARY) {
			return false;
		} else {
			ItemStack target = slot.getItem();
			if (!target.isEmpty() && EnchantmentHelper.hasTag(stack, EnchantmentTags.CURSE)) {
				this.playSound(player, SoundEvents.BUNDLE_INSERT);
				target.shrink(target.getCount());
			} else if (target.isEmpty() && getContentWeight(stack) > 0) {
				int i = getSelectedItem(stack);
				ItemStack newstack = removeOne(stack, i != -1 ? i : 0);
				if (!newstack.isEmpty()) {
					this.playSound(player, SoundEvents.BUNDLE_REMOVE_ONE);
					add(stack, slot.safeInsert(newstack));
				}
			} else if (target.getItem().canFitInsideContainerItems()) {
				int i = (256 - getContentWeight(stack));
				if (add(stack, slot.safeTake(target.getCount(), i, player))) {
					this.playSound(player, SoundEvents.BUNDLE_INSERT);
				}
			}
			return true;
		}
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack target, Slot slot, ClickAction action, Player player, SlotAccess acc) {
		if (action != ClickAction.SECONDARY) {
			return false;
		} else if (slot.allowModification(player)) {
			if (!target.isEmpty() && EnchantmentHelper.hasTag(stack, EnchantmentTags.CURSE)) {
				this.playSound(player, SoundEvents.BUNDLE_INSERT);
				target.shrink(target.getCount());
			} else if (target.isEmpty()) {
				int i = getSelectedItem(stack);
				ItemStack newstack = removeOne(stack, i != -1 ? i : 0);
				if (!newstack.isEmpty()) {
					this.playSound(player, SoundEvents.BUNDLE_REMOVE_ONE);
					acc.set(newstack);
				}
			} else if (target.getItem().canFitInsideContainerItems()) {
				int i = (256 - getContentWeight(stack));
				if (add(stack, target)) {
					this.playSound(player, SoundEvents.BUNDLE_INSERT);
					target.shrink(i);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public InteractionResult use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (dropContents(stack, player)) {
			this.playSound(player, SoundEvents.BUNDLE_DROP_CONTENTS);
			player.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.FAIL;
		}
	}

	public int getMatchingItem(ItemStack stack, List<ItemStack> data) {
		if (!stack.isStackable()) {
			return -1;
		} else {
			for (int i = 0; i < data.size(); i++) {
				if (ItemStack.isSameItemSameComponents(data.get(i), stack)) {
					return i;
				}
			}
			return -1;
		}
	}

	public boolean add(ItemStack stack, ItemStack target) {
		if (!target.isEmpty() && target.getItem().canFitInsideContainerItems()) {
			List<ItemStack> data = new ArrayList<>(getContents(stack));
			if (getContentWeight(stack) >= 256) {
				return false;
			} else {
				int i = getMatchingItem(target, data);
				if (i != -1) {
					if (data.get(i).getCount() >= data.get(i).getMaxStackSize()) {
						data.addFirst(target.copyWithCount(target.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : target.getCount()));
					} else {
						int c = (data.get(i).getCount() + target.getCount());
						if (c > data.get(i).getMaxStackSize()) {
							ItemStack copy = data.remove(i).copyWithCount(target.getMaxStackSize());
							data.addFirst(copy.copyWithCount(copy.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : copy.getCount()));
							data.addFirst(target.copyWithCount(target.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : c - target.getMaxStackSize()));
						} else {
							ItemStack copy = data.remove(i);
							copy.grow(target.getCount());
							data.addFirst(copy);
						}
					}
				} else {
					data.addFirst(target.copyWithCount(target.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : target.getCount()));
				}
                if (target.getItem() instanceof BundleHoldingItem) {
                    stack.set(QuillData.BOMB, new BundleBomb(true));
                }
				stack.set(QuillData.BUNDLE, new BundleHoldingContents(data));
				return true;
			}
		}
		return false;
	}

	public boolean dropContents(ItemStack stack, Entity target) {
		BundleHoldingContents data = stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY);
		if (data.isEmpty()) {
			return false;
		} else {
			if (target instanceof ServerPlayer ply) {
				data.getItemsAsCopyIterable().forEach(item -> ply.drop(item, true));
			}
			stack.set(QuillData.BUNDLE, BundleHoldingContents.EMPTY);
			return true;
		}
	}

	public ItemStack removeOne(ItemStack stack, int i) {
		List<ItemStack> data = new ArrayList<>(getContents(stack));
		if (data.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			ItemStack newstack = data.remove(i);
			stack.set(QuillData.BUNDLE, new BundleHoldingContents(data));
			return newstack;
		}
	}

	public List<ItemStack> getContents(ItemStack stack) {
		return stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY).getItems();
	}

	public int getContentWeight(ItemStack stack) {
		return stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY).getWeight();
	}

	public int getSelectedItem(ItemStack stack) {
		return stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY).getSelectedItem();
	}

	public void playSound(Entity target, SoundEvent sound) {
		if (target.level() instanceof ServerLevel lvl) {
			lvl.playSound(null, target.blockPosition(), sound, SoundSource.AMBIENT, 1.0F, 0.8F + lvl.getRandom().nextFloat() * 0.4F);
		} else {
			target.playSound(sound, 0.8F, 0.8F + target.level().getRandom().nextFloat() * 0.4F);
		}
	}

    public void explode(ItemStack stack, ServerLevel lvl , Entity target) {
        stack.shrink(1);
        lvl.explode(null, target.damageSources().badRespawnPointExplosion(target.blockPosition().getCenter()), null, target.blockPosition().getCenter(), 5.0F, true, Level.ExplosionInteraction.BLOCK);
    }

	public static boolean hasSelectedItem(ItemStack stack) {
		BundleHoldingContents data = stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY);
		return data.getSelectedItem() >= 0;
	}

	public static ItemStack getSelectedItemStack(ItemStack stack) {
		BundleHoldingContents data = stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY);
		if (data.getSelectedItem() >= 0) {
			return data.getSpecificItem(data.getSelectedItem());
		}
		return ItemStack.EMPTY;
	}
}