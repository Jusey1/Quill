package net.salju.quill.init;

import net.salju.quill.effect.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class QuillWitch {
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "quill");
	public static final DeferredHolder<MobEffect, MobEffect> FROGGO = EFFECTS.register("froggo", () -> new FrogEffect(MobEffectCategory.HARMFUL, -6750055));

	public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, "quill");
	public static final DeferredHolder<Potion, Potion> FROG = POTIONS.register("froggo", () -> new Potion("froggo", new MobEffectInstance(FROGGO, 0, 0, false, true)));
}