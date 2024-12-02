package net.salju.quill.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Final
	@Shadow
	private ItemModelShaper itemModelShaper;

	@Inject(method = "renderBundleItem", at = @At(value = "HEAD"))
	private void renderBundle(ItemStack stack, ItemDisplayContext context, boolean check, PoseStack pose, MultiBufferSource buffer, int c, int e, BakedModel model, Level world, LivingEntity owner, int i, CallbackInfo ci) {
		if (stack.getItem() instanceof BundleHoldingItem target) {
			if (target.getSelectedItem(stack) != -1) {
				this.renderItemModelRaw(stack, context, check, pose, buffer, c, e, this.resolveModelOverride(this.itemModelShaper.getItemModel(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "bundle_open_back")), stack, world, owner, i), this.quill$shouldRenderItemFlat(context), -1.5F);
				this.renderSimpleItemModel(target.getContents(stack).get(target.getSelectedItem(stack)), context, check, pose, buffer, c, e, this.getModel(target.getContents(stack).get(target.getSelectedItem(stack)), world, owner, i), this.quill$shouldRenderItemFlat(context));
				this.renderItemModelRaw(stack, context, check, pose, buffer, c, e, this.resolveModelOverride(this.itemModelShaper.getItemModel(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "bundle_open_front")), stack, world, owner, i), this.quill$shouldRenderItemFlat(context), 0.5F);
			} else {
				this.render(stack, context,check, pose, buffer, c, e, model);
			}
		}
	}

	@Shadow
    public abstract void render(ItemStack stack, ItemDisplayContext context, boolean check, PoseStack pose, MultiBufferSource buffer, int c, int e, BakedModel model);

	@Shadow
	protected abstract void renderSimpleItemModel(ItemStack stack, ItemDisplayContext context, boolean check, PoseStack pose, MultiBufferSource buffer, int c, int e, BakedModel model, boolean bool);

	@Shadow
    protected abstract void renderItemModelRaw(ItemStack stack, ItemDisplayContext context, boolean check, PoseStack pose, MultiBufferSource buffer, int c, int e, BakedModel model, boolean bool, float f);

	@Shadow
	protected abstract BakedModel resolveModelOverride(BakedModel model, ItemStack stack, Level world, LivingEntity owner, int i);

	@Shadow
	public abstract BakedModel getModel(ItemStack stack, @Nullable Level world, @Nullable LivingEntity owner, int i);

	@Unique
	private boolean quill$shouldRenderItemFlat(ItemDisplayContext context) {
		return context.equals(ItemDisplayContext.GUI) || context.equals(ItemDisplayContext.GROUND) || context.equals(ItemDisplayContext.FIXED);
	}
}