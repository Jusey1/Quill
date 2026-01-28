package net.salju.quill.init;

import net.salju.quill.Quill;
import net.salju.quill.client.bundle.*;
import net.salju.quill.item.component.*;
import net.salju.quill.network.Fireworks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.minecraft.resources.Identifier;

@EventBusSubscriber(value = Dist.CLIENT)
public class QuillClient {
	@SubscribeEvent
	public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(BundleHoldingTooltip.class, ClientBundleHoldingTooltip::new);
	}

	@SubscribeEvent
	public static void registerConditionalProps(RegisterConditionalItemModelPropertyEvent event) {
		event.register(Identifier.fromNamespaceAndPath(Quill.MODID, "bundle/has_selected_item"), BundleHoldingCondition.MAP_CODEC);
	}

	@SubscribeEvent
	public static void registerItemModels(RegisterItemModelsEvent event) {
		event.register(Identifier.fromNamespaceAndPath(Quill.MODID, "bundle/selected_item"), BundleHoldingRenderer.Unbaked.MAP_CODEC);
	}

    @SubscribeEvent
    public static void registerNetwork(RegisterClientPayloadHandlersEvent event) {
        event.register(Fireworks.CREEPER_FIREWORKS, Fireworks::handleData);
    }
}