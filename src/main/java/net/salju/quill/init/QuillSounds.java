package net.salju.quill.init;

import net.salju.quill.Quill;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public class QuillSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, Quill.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> CHEERS = REGISTRY.register("cheers", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "cheers")));
	public static final DeferredHolder<SoundEvent, SoundEvent> EMPTY = REGISTRY.register("empty", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Quill.MODID, "empty")));
}