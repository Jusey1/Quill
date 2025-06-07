package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.salju.quill.init.QuillTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
	public void getMax(CallbackInfoReturnable<Integer> ci) {
		if (QuillConfig.POTS.get()) {
			ItemStack stack = (ItemStack) (Object) this;
			if (stack.getItem() instanceof PotionItem) {
				ci.setReturnValue(16);
			}
		}
	}

	@Inject(method = "isEnchantable", at = @At("RETURN"), cancellable = true)
	public void isEnchantable(CallbackInfoReturnable<Boolean> ci) {
        ItemStack stack = (ItemStack) (Object) this;
		if (stack.is(QuillTags.ENCHS) && !stack.isEnchanted()) {
			if (!stack.has(DataComponents.ENCHANTABLE)) {
				stack.set(DataComponents.ENCHANTABLE, new Enchantable(1));
			}
			ci.setReturnValue(true);
		}
	}

	@Inject(method = "isDamageableItem", at = @At("RETURN"), cancellable = true)
	public void isUnbreakable(CallbackInfoReturnable<Boolean> ci) {
		ItemStack stack = (ItemStack) (Object) this;
		if (stack.isEnchanted()) {
			if (QuillConfig.UNBREAKABLE.get()) {
				ci.setReturnValue(false);
			} else if (QuillConfig.UNBREAKING.get()) {
				ItemEnchantments.Mutable map = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack.copy()));
				for (Holder<Enchantment> e : map.keySet()) {
					if (e.is(Enchantments.UNBREAKING)) {
						ci.setReturnValue(false);
						break;
					}
				}
			}
		}
	}
}