package net.salju.quill.client.bundle;

import net.salju.quill.item.BundleHoldingItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

public class BundleHoldingRenderer implements ItemModel {
	private static final ItemModel INSTANCE = new BundleHoldingRenderer();

	@Override
	public void update(ItemStackRenderState render, ItemStack bundle, ItemModelResolver resolver, ItemDisplayContext context, @Nullable ClientLevel lvl, @Nullable LivingEntity target, int i) {
		ItemStack stack = BundleHoldingItem.getSelectedItemStack(bundle);
		if (!stack.isEmpty()) {
			resolver.appendItemLayers(render, stack, context, lvl, target, i);
		}
	}

	public record Unbaked() implements ItemModel.Unbaked {
		public static final MapCodec<BundleHoldingRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new BundleHoldingRenderer.Unbaked());

		@Override
		public MapCodec<BundleHoldingRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public ItemModel bake(ItemModel.BakingContext context) {
			return BundleHoldingRenderer.INSTANCE;
		}

		@Override
		public void resolveDependencies(ResolvableModel.Resolver resolver) {
			//
		}
	}
}