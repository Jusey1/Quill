package net.salju.quill.init;

import net.salju.quill.Quill;
import net.salju.quill.item.component.*;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.component.DataComponentType;
import java.util.function.UnaryOperator;

public class QuillData {
	public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Quill.MODID);
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<MagicMirrorTeleport>> TELEPORT = register("magic_mirror_teleport", (stuffs) -> {return stuffs.persistent(MagicMirrorTeleport.CODEC).networkSynchronized(MagicMirrorTeleport.STREAM_CODEC).cacheEncoding();});
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<BundleHoldingContents>> BUNDLE = register("bundle_holding_contents", (stuffs) -> {return stuffs.persistent(BundleHoldingContents.CODEC).networkSynchronized(BundleHoldingContents.STREAM_CODEC).cacheEncoding();});

	private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> stuffs) {
		return REGISTRY.register(name, () -> stuffs.apply(DataComponentType.builder()).build());
	}
}