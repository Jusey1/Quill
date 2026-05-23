package net.salju.quill.init;

import net.salju.quill.Quill;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class QuillItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.createItems(Quill.MODID);
	public static final DeferredHolder<Item, Item> AZURE = block(QuillBlocks.AZURE, "azure_froglight");

	private static DeferredHolder<Item, Item> block(DeferredHolder<Block, Block> block, String name) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), createBaseProps(name)));
	}

	public static Item.Properties createBaseProps(String name) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Quill.MODID, name)));
	}
}