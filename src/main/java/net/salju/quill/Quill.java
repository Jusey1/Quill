package net.salju.quill;

import net.salju.quill.init.*;
import net.salju.quill.network.NetworkSetup;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.IEventBus;

@Mod("quill")
public class Quill {
	public static final String MODID = "quill";

	public Quill(ModContainer mod, IEventBus bus) {
		bus.addListener(NetworkSetup::registerNetworking);
		QuillData.REGISTRY.register(bus);
		QuillBlocks.REGISTRY.register(bus);
		QuillItems.REGISTRY.register(bus);
		QuillSounds.REGISTRY.register(bus);
		QuillTabs.REGISTRY.register(bus);
		QuillWitch.EFFECTS.register(bus);
		QuillWitch.POTIONS.register(bus);
		mod.registerConfig(ModConfig.Type.COMMON, QuillConfig.CONFIG, "quill-common.toml");
	}
}