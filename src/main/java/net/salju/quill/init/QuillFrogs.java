package net.salju.quill.init;

import net.salju.quill.Quill;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

public class QuillFrogs {
	public static final DeferredRegister<FrogVariant> REGISTRY = DeferredRegister.create(Registries.FROG_VARIANT, Quill.MODID);
	public static final DeferredHolder<FrogVariant, FrogVariant> WITCH = REGISTRY.register("witch", () -> new FrogVariant(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "textures/entity/witch_frog.png")));
}