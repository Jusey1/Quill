package net.salju.quill.init;

import net.salju.quill.Quill;
import net.salju.quill.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class QuillItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.createItems(Quill.MODID);
	public static final DeferredHolder<Item, Item> AZURE = block(QuillBlocks.AZURE, "azure_froglight");
	public static final DeferredHolder<Item, Item> SALJU = REGISTRY.register("salju", () -> new Item(createBaseProps("salju").stacksTo(1).rarity(Rarity.EPIC)));
	public static final DeferredHolder<Item, Item> MAGIC_MIRROR = REGISTRY.register("magic_mirror", () -> new MagicMirrorItem(createBaseProps("magic_mirror").stacksTo(1).rarity(Rarity.UNCOMMON)));
	public static final DeferredHolder<Item, Item> BUNDLE = REGISTRY.register("bundle", () -> new BundleHoldingItem(createBaseProps("bundle").stacksTo(1).rarity(Rarity.UNCOMMON)));

	private static DeferredHolder<Item, Item> block(DeferredHolder<Block, Block> block, String name) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), createBaseProps(name)));
	}

	public static Item.Properties createBaseProps(String name) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Quill.MODID, name)));
	}
}