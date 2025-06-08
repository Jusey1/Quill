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
	public static final TagKey<EntityType<?>> PEACEFUL = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID, "forceful_peaceful"));
	public static final TagKey<EntityType<?>> NO = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"disable_griefing"));
	public static final TagKey<EntityType<?>> RIDERS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"riders"));
	public static final TagKey<EntityType<?>> DOUBLE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"vehs/double"));
	public static final TagKey<EntityType<?>> TRIPLE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"vehs/triple"));
	public static final TagKey<EntityType<?>> QUAD = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"vehs/quad"));
	public static final TagKey<EntityType<?>> PENTA = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"vehs/penta"));
	public static final TagKey<EntityType<?>> FROGGO = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"can_be_transformed_into_frog"));
	public static final TagKey<Block> SMELT = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"autosmelt"));
	public static final TagKey<Block> LADDERS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"ladders"));
	public static final TagKey<Item> AXES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"axes"));
	public static final TagKey<Item> BOWS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"bows"));
	public static final TagKey<Item> SHIELDS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"shields"));
	public static final TagKey<Item> SWORDS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"swords"));
	public static final TagKey<Item> SPECIALS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"unique_weapons"));
	public static final TagKey<Item> NETHER = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"netherite_axe_tier"));
	public static final TagKey<Item> DIAMOND = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"diamond_axe_tier"));
	public static final TagKey<Item> IRON = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"iron_axe_tier"));
	public static final TagKey<Item> STONE = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"stone_axe_tier"));
	public static final TagKey<Item> ENCHS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"enchantables"));
	public static final TagKey<Item> PROTECTED = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"protected"));
}