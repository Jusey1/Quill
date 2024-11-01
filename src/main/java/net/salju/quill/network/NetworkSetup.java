package net.salju.quill.network;

import net.salju.quill.Quill;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NetworkSetup {
	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		Quill.addNetworkMessage(Fireworks.CREEPER_FIREWORKS, Fireworks.STREAM_CODEC, Fireworks::handleData);
	}
}