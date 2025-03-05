package net.salju.quill.init;

import net.salju.quill.Quill;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class QuillTags {
	public static final TagKey<EntityType<?>> PEACEFUL = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID, "peaceful"));
	public static final TagKey<EntityType<?>> NO = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"no"));
	public static final TagKey<EntityType<?>> DOUBLE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"double_riders"));
	public static final TagKey<EntityType<?>> BOOTS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"force_feet_armor"));
	public static final TagKey<Block> SMELT = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"autosmelt"));
	public static final TagKey<Item> AXES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"axes"));
	public static final TagKey<Item> BOWS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"bows"));
	public static final TagKey<Item> SHIELDS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"shields"));
	public static final TagKey<Item> SPECIALS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"unique_weapons"));
	public static final TagKey<Item> NETHER = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"netherite_axe_tier"));
	public static final TagKey<Item> DIAMOND = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"diamond_axe_tier"));
	public static final TagKey<Item> IRON = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"iron_axe_tier"));
	public static final TagKey<Item> STONE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"stone_axe_tier"));
	public static final TagKey<Item> ENCHS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"enchantables"));
	public static final TagKey<Item> DOUBENCHS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"double_enchantments"));
	public static final TagKey<Item> PROTECTED = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"protected"));
}