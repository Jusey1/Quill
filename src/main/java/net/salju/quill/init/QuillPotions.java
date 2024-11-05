package net.salju.quill.init;

import net.salju.quill.Quill;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.effect.MobEffectInstance;

public class QuillPotions {
	public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(Registries.POTION, Quill.MODID);
	public static final DeferredHolder<Potion, Potion> FROGGO = REGISTRY.register("froggo", () -> new Potion(new MobEffectInstance(QuillEffects.FROGGO, 0, 0, false, true)));
}