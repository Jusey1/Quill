package net.salju.quill.item;

import net.minecraft.world.InteractionResult;
import net.salju.quill.init.QuillData;
import net.salju.quill.item.component.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.stats.Stats;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class BundleHoldingItem extends Item {
	public BundleHoldingItem(Item.Properties props) {
		super(props);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		list.add(Component.translatable("item.quill.bundle.fullness", getContentWeight(stack), 256).withStyle(ChatFormatting.GRAY));
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
	public boolean canFitInsideContainerItems() {
		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity target, int e, boolean check) {
		super.inventoryTick(stack, world, target, e, check);
		BundleHoldingContents data = stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY);
		if (EnchantmentHelper.hasTag(stack, EnchantmentTags.CURSE)) {
			if (!data.isEmpty()) {
				data.getItems().clear();
			}
		}
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
				ItemStack newstack = removeOne(stack, 0);
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
				ItemStack newstack = removeOne(stack, 0);
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

	public boolean add(ItemStack stack, ItemStack item) {
		if (!item.isEmpty() && item.getItem().canFitInsideContainerItems()) {
			List<ItemStack> data = new ArrayList<>(getContents(stack));
			if (getContentWeight(stack) >= 256) {
				return false;
			} else {
				int i = getMatchingItem(item, data);
				if (i != -1) {
					if (data.get(i).getCount() >= data.get(i).getMaxStackSize()) {
						data.addFirst(item.copyWithCount(item.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : item.getCount()));
					} else {
						int c = (data.get(i).getCount() + item.getCount());
						if (c > data.get(i).getMaxStackSize()) {
							ItemStack copy = data.remove(i).copyWithCount(item.getMaxStackSize());
							data.addFirst(copy.copyWithCount(copy.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : copy.getCount()));
							data.addFirst(item.copyWithCount(item.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : c - item.getMaxStackSize()));
						} else {
							ItemStack copy = data.remove(i);
							copy.grow(item.getCount());
							data.addFirst(copy);
						}
					}
				} else {
					data.addFirst(item.copyWithCount(item.getCount() + getContentWeight(stack) > 256 ? 256 - getContentWeight(stack) : item.getCount()));
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

	public Iterable<ItemStack> getContentsIterable(ItemStack stack) {
		return stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY).getItemsAsCopyIterable();
	}

	public int getContentWeight(ItemStack stack) {
		return stack.getOrDefault(QuillData.BUNDLE, BundleHoldingContents.EMPTY).getWeight();
	}

	public void playSound(Entity target, SoundEvent sound) {
		if (target.level() instanceof ServerLevel lvl) {
			lvl.playSound(null, target.blockPosition(), sound, SoundSource.AMBIENT, 1.0F, 0.8F + lvl.getRandom().nextFloat() * 0.4F);
		} else {
			target.playSound(sound, 0.8F, 0.8F + target.level().getRandom().nextFloat() * 0.4F);
		}
	}
}