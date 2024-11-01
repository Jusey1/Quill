package net.salju.quill.init;

import net.salju.quill.client.tooltip.*;
import net.salju.quill.item.component.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class QuillTooltips {
	@SubscribeEvent
	public static void registerTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(BundleHoldingTooltip.class, ClientBundleHoldingTooltip::new);
	}
}