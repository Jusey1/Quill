package net.salju.quill.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.Lists;
import java.util.List;

public record BundleHoldingContents(List<ItemStack> items) {
	public static final BundleHoldingContents EMPTY = new BundleHoldingContents(List.of());
	public static final Codec<BundleHoldingContents> CODEC = ItemStack.CODEC.listOf().xmap(BundleHoldingContents::new, b -> b.items);
	public static final StreamCodec<RegistryFriendlyByteBuf, BundleHoldingContents> STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(BundleHoldingContents::new, b -> b.items);

	public BundleHoldingContents(List<ItemStack> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "BundleHoldingContents" + this.getItems();
	}

	public ItemStack getSpecificItem(int index) {
		return this.getItems().get(index);
	}

	public Iterable<ItemStack> getItemsAsCopyIterable() {
		return Lists.transform(this.items, ItemStack::copy);
	}

	public List<ItemStack> getItems() {
		return this.items;
	}

	public boolean isEmpty() {
		return this.getItems().isEmpty();
	}

	public int size() {
		return this.getItems().size();
	}

	public int getWeight() {
		int b = 0;
		if (!this.isEmpty()) {
			for (int i = 0; i < this.size(); i++) {
				b = (b + this.getSpecificItem(i).getCount());
			}
		}
		return b;
	}
}