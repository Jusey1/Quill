package net.salju.quill.init;

import net.salju.quill.Quill;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class QuillTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Quill.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> QUILL = REGISTRY.register("quill",
			() -> CreativeModeTab.builder().title(Component.translatable("itemGroup.quill")).icon(() -> new ItemStack(QuillItems.SALJU.get())).displayItems((parameters, tabData) -> {
				tabData.accept(QuillItems.AZURE.get());
				tabData.accept(QuillItems.MAGIC_MIRROR.get());
				tabData.accept(QuillItems.BUNDLE.get());
			}).build());
}