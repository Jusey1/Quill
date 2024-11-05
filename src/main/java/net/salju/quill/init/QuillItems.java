package net.salju.quill.init;

import net.salju.quill.Quill;
import net.salju.quill.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class QuillItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.createItems(Quill.MODID);
	public static final DeferredHolder<Item, Item> AZURE = block(QuillBlocks.AZURE);
	public static final DeferredHolder<Item, Item> SALJU = REGISTRY.register("salju", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
	public static final DeferredHolder<Item, Item> MAGIC_MIRROR = REGISTRY.register("magic_mirror", () -> new MagicMirrorItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> BUNDLE = REGISTRY.register("bundle", () -> new BundleHoldingItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

	private static DeferredHolder<Item, Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}