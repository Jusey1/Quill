package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.salju.quill.Quill;
import net.salju.quill.item.BundleHoldingItem;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.Nullable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Final
	@Shadow
	private ItemModelShaper itemModelShaper;

	@Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
	private void renderBundle(ItemStack stack, ItemDisplayContext context, boolean check, PoseStack pose, MultiBufferSource buffer, int c, int e, BakedModel model, CallbackInfo ci) {
		if (stack.getItem() instanceof BundleHoldingItem target) {
			if (target.getSelectedItem(stack) != -1 && context == ItemDisplayContext.GUI) {
				this.renderItemModelRaw(stack, context, check, pose, buffer, c, e, this.resolveModelOverride(this.itemModelShaper.getItemModel(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "bundle_open_back")), stack, null, null, 0), true, -1.5F);
				this.renderSimpleItemModel(target.getContents(stack).get(target.getSelectedItem(stack)), context, check, pose, buffer, c, e, this.getModel(target.getContents(stack).get(target.getSelectedItem(stack)), null, null, 0), true);
				this.renderItemModelRaw(stack, context, check, pose, buffer, c, e, this.resolveModelOverride(this.itemModelShaper.getItemModel(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "bundle_open_front")), stack, null, null, 0), true, 0.5F);
				ci.cancel();
			}
		}
	}

	@Shadow
	protected abstract void renderSimpleItemModel(ItemStack stack, ItemDisplayContext context, boolean check, PoseStack pose, MultiBufferSource buffer, int c, int e, BakedModel model, boolean bool);

	@Shadow
	protected abstract void renderItemModelRaw(ItemStack stack, ItemDisplayContext context, boolean check, PoseStack pose, MultiBufferSource buffer, int c, int e, BakedModel model, boolean bool, float f);

	@Shadow
	protected abstract BakedModel resolveModelOverride(BakedModel model, ItemStack stack, Level world, LivingEntity owner, int i);

	@Shadow
	public abstract BakedModel getModel(ItemStack stack, @Nullable Level world, @Nullable LivingEntity owner, int i);
}