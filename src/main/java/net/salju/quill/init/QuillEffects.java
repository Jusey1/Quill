package net.salju.quill.init;

import net.salju.quill.Quill;
import net.salju.quill.effect.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class QuillEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, Quill.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> FROGGO = REGISTRY.register("froggo", () -> new FrogEffect(MobEffectCategory.HARMFUL, -6750055, "effect.quill.froggo"));
}