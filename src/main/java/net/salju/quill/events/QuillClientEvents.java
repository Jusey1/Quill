package net.salju.quill.events;

import net.salju.quill.init.QuillData;
import net.salju.quill.item.BundleHoldingItem;
import net.salju.quill.item.component.BundleHoldingContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

@EventBusSubscriber(value = Dist.CLIENT)
public class QuillClientEvents {
	@SubscribeEvent
	public static void onWheel(ScreenEvent.MouseScrolled.Post event) {
		if (event.getScreen() instanceof AbstractContainerScreen<?> screen && screen.getSlotUnderMouse() != null) {
			ItemStack stack = screen.getSlotUnderMouse().getItem();
			if (screen.getSlotUnderMouse().hasItem() && stack.getItem() instanceof BundleHoldingItem target) {
				if (target.getContentWeight(stack) > 0) {
					Vector2i v = new ScrollWheelHandler().onMouseScroll(event.getScrollDeltaX(), event.getScrollDeltaY());
					int w = v.y == 0 ? -v.x : v.y;
					if (w != 0) {
						int c = target.getSelectedItem(stack);
						int e = ScrollWheelHandler.getNextScrollWheelSelection(w, c, target.getContents(stack).size());
						if (c != e) {
							stack.set(QuillData.BUNDLE, new BundleHoldingContents(target.getContents(stack), e));
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onScreen(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
			for (Slot slot : screen.getMenu().slots) {
				ItemStack stack = slot.getItem();
				if (slot.hasItem() && stack.getItem() instanceof BundleHoldingItem target) {
					if ((slot != screen.getSlotUnderMouse() || screen.isDragging()) && target.getSelectedItem(stack) != -1) {
						stack.set(QuillData.BUNDLE, new BundleHoldingContents(target.getContents(stack), -1));
					}
				}
			}
		}
	}
}