package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import net.salju.quill.init.QuillData;
import net.salju.quill.init.QuillItems;
import net.salju.quill.item.BundleHoldingItem;
import net.salju.quill.item.component.BundleHoldingContents;
import net.salju.quill.network.BundleUpdate;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;


@Mixin(BundleMouseActions.class)
public abstract class BundleMouseActionsMixin {
	@Final
	@Shadow
	private Minecraft minecraft;

	@Final
	@Shadow
	private ScrollWheelHandler scrollWheelHandler;

	@Inject(method = "onMouseScrolled", at = @At(value = "HEAD"), cancellable = true)
	private void mouse(double up, double down, int s, ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if (stack.getItem() instanceof BundleHoldingItem target) {
			ci.cancel();
			if (target.getContentWeight(stack) > 0) {
				Vector2i v = this.scrollWheelHandler.onMouseScroll(up, down);
				int w = v.y == 0 ? -v.x : v.y;
				if (w != 0) {
					int c = target.getSelectedItem(stack);
					int e = ScrollWheelHandler.getNextScrollWheelSelection(w, c, target.getContents(stack).size());
					if (c != e) {
						this.quill$update(stack, s, e);
					}
				}
				ci.setReturnValue(true);
			}
		}
	}

	@Inject(method = "onStopHovering", at = @At(value = "HEAD"), cancellable = true)
	private void stopHover(Slot target, CallbackInfo ci) {
		if (target.getItem().is(QuillItems.BUNDLE)) {
			ci.cancel();
			this.quill$reset(target.getItem(), target.index);
		}
	}

	@Inject(method = "onSlotClicked", at = @At(value = "HEAD"), cancellable = true)
	private void stopHover(Slot target, ClickType type, CallbackInfo ci) {
		if (target.getItem().is(QuillItems.BUNDLE)) {
			ci.cancel();
			if (type.equals(ClickType.QUICK_MOVE) || type.equals(ClickType.SWAP)) {
				this.quill$reset(target.getItem(), target.index);
			}
		}
	}

	@Unique
	public void quill$update(ItemStack stack, int s, int i) {
		if (stack.getItem() instanceof BundleHoldingItem target) {
			if (this.minecraft.getConnection() != null && i < target.getContents(stack).size()) {
				stack.set(QuillData.BUNDLE, new BundleHoldingContents(target.getContents(stack), i));
				PacketDistributor.sendToServer(new BundleUpdate(s, i));
			}
		}
	}

	@Unique
	public void quill$reset(ItemStack stack, int s) {
		this.quill$update(stack, s, -1);
	}
}