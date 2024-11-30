package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.item.MagicMirrorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Inject(method = "renderArmWithItem", at = @At(value = "HEAD"))
	private void renderSwordBlock(AbstractClientPlayer player, float ticks, float p, InteractionHand hand, float sP, ItemStack stack, float f, PoseStack pose, MultiBufferSource buffer, int i, CallbackInfo ci) {
		if (player.isUsingItem() && stack == player.getUseItem()) {
			if (stack.getItem() instanceof MagicMirrorItem) {
				boolean check = hand.equals(InteractionHand.MAIN_HAND);
				pose.translate(check ? 0.245F : -0.245F, 0.035F, 0.14F);
				pose.mulPose(Axis.YP.rotationDegrees(check ? 32.0F : -32.0F));
				pose.mulPose(Axis.ZP.rotationDegrees(check ? 4.0F : -4.0F));
			}
		}
	}
}