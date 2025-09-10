package net.salju.quill.item.component;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;

public record BundleHoldingContents(List<ItemStack> items, int target) {
	public static final BundleHoldingContents EMPTY = new BundleHoldingContents(List.of());
	public static final Codec<BundleHoldingContents> CODEC = ItemStack.CODEC.listOf().xmap(BundleHoldingContents::new, b -> b.items);
	public static final StreamCodec<RegistryFriendlyByteBuf, BundleHoldingContents> STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(BundleHoldingContents::new, b -> b.items);

	public BundleHoldingContents(List<ItemStack> items) {
		this(items, -1);
	}

	@Override
	public String toString() {
		return "BundleHoldingContents" + this.getItems();
	}

	public ItemStack getSpecificItem(int i) {
		return this.getItems().get(i);
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

	public int getSelectedItem() {
		return this.target;
	}
}