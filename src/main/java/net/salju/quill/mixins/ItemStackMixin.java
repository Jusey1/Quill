package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.salju.quill.init.QuillTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ItemStack;

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
		if (QuillConfig.ENCHS.get()) {
			ItemStack stack = (ItemStack) (Object) this;
			if (stack.is(QuillTags.ENCHS) && !stack.isEnchanted()) {
				if (!stack.has(DataComponents.ENCHANTABLE)) {
					stack.set(DataComponents.ENCHANTABLE, new Enchantable(1));
				}
				ci.setReturnValue(true);
			}
		}
	}

	@Inject(method = "isDamageableItem", at = @At("RETURN"), cancellable = true)
	public void isUnbreakable(CallbackInfoReturnable<Boolean> ci) {
		if (QuillConfig.ENCHS.get() && QuillConfig.UNBREAKING.get()) {
			ItemStack stack = (ItemStack) (Object) this;
			if (stack.isEnchanted()) {
				ci.setReturnValue(false);
			}
		}
	}
}