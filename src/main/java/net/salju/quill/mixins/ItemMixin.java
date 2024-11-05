package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.salju.quill.init.QuillTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Item.class)
public abstract class ItemMixin {
	@Inject(method = "getEnchantmentValue", at = @At("RETURN"), cancellable = true)
	public void isEnchantable(CallbackInfoReturnable<Integer> ci) {
		if (QuillConfig.ENCHS.get()) {
			ItemStack stack = new ItemStack((Item) (Object) this);
			if (stack.is(QuillTags.ENCHS) && ci.getReturnValue() <= 0) {
				ci.setReturnValue(1);
			}
		}
	}
}