package net.salju.quill.init;

import net.salju.quill.Quill;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Block;

public class QuillBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, Quill.MODID);
	public static final DeferredHolder<Block, Block> AZURE = REGISTRY.register("azure_froglight", () -> new RotatedPillarBlock(createBaseProps("azure_froglight").mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.3F).lightLevel((state) -> 15).sound(SoundType.FROGLIGHT)));

	public static BlockBehaviour.Properties createBaseProps(String name) {
		return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Quill.MODID, name)));
	}
}