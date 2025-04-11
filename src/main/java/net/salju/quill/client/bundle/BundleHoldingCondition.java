package net.salju.quill.client.bundle;

import net.salju.quill.item.BundleHoldingItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

public record BundleHoldingCondition() implements ConditionalItemModelProperty {
	public static final MapCodec<BundleHoldingCondition> MAP_CODEC = MapCodec.unit(new BundleHoldingCondition());

	@Override
	public boolean get(ItemStack stack, @Nullable ClientLevel lvl, @Nullable LivingEntity target, int i, ItemDisplayContext context) {
		return BundleHoldingItem.hasSelectedItem(stack);
	}

	@Override
	public MapCodec<BundleHoldingCondition> type() {
		return MAP_CODEC;
	}
}
