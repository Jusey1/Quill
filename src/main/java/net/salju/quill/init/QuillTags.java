package net.salju.quill.init;

import net.salju.quill.Quill;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class QuillTags {
	public static final TagKey<EntityType<?>> PEACEFUL = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID, "peaceful"));
	public static final TagKey<EntityType<?>> PETS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID, "pets"));
	public static final TagKey<EntityType<?>> NO = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"no"));
	public static final TagKey<EntityType<?>> DOUBLE = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"double_riders"));
	public static final TagKey<EntityType<?>> BOOTS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Quill.MODID,"force_feet_armor"));
	public static final TagKey<Item> BOWS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"bows"));
	public static final TagKey<Item> ENCHS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"enchantables"));
	public static final TagKey<Item> DOUBENCHS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"double_enchantments"));
	public static final TagKey<Item> AXES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Quill.MODID,"axes"));
}