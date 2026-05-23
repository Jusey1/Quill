package net.salju.quill.init;

import net.salju.quill.network.Fireworks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class QuillClient {
    @SubscribeEvent
    public static void registerNetwork(RegisterClientPayloadHandlersEvent event) {
        event.register(Fireworks.CREEPER_FIREWORKS, Fireworks::handleData);
    }
}