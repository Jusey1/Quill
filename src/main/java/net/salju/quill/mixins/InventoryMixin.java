package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.salju.quill.init.QuillTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Mixin(Inventory.class)
public class InventoryMixin {
	@Inject(method = "dropAll", at = @At(value = "HEAD"), cancellable = true)
	private void dontDropGear(CallbackInfo ci) {
		Inventory invy = (Inventory) (Object) this;
		if (QuillConfig.DEATH.get()) {
			ci.cancel();
			for (int i = 0; i < invy.getContainerSize(); i++) {
				ItemStack stack = invy.getItem(i);
				if (i < 9 || i > 35 || stack.is(QuillTags.PROTECTED)) {
					continue;
				}
				if (!stack.isEmpty()) {
					invy.player.drop(stack, true, false);
					invy.setItem(i, ItemStack.EMPTY);
				}
			}
		}
	}
}