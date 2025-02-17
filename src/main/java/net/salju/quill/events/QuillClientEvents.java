package net.salju.quill.events;

import net.salju.quill.init.QuillData;
import net.salju.quill.item.BundleHoldingItem;
import net.salju.quill.item.MagicMirrorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import com.mojang.math.Axis;
import net.salju.quill.item.component.BundleHoldingContents;
import org.joml.Vector2i;

@EventBusSubscriber(value = Dist.CLIENT)
public class QuillClientEvents {
	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event) {
		Player player = Minecraft.getInstance().player;
		if (player.isUsingItem() && event.getItemStack().is(player.getUseItem().getItem())) {
			if (event.getItemStack().getItem() instanceof MagicMirrorItem) {
				boolean check = event.getHand().equals(InteractionHand.MAIN_HAND);
				event.getPoseStack().translate(check ? 0.245F : -0.245F, 0.035F, 0.14F);
				event.getPoseStack().mulPose(Axis.YP.rotationDegrees(check ? 32.0F : -32.0F));
				event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(check ? 4.0F : -4.0F));
			}
		}
	}

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
				if (slot.hasItem() && slot != screen.getSlotUnderMouse() && stack.getItem() instanceof BundleHoldingItem target) {
					if (target.getSelectedItem(stack) != -1) {
						stack.set(QuillData.BUNDLE, new BundleHoldingContents(target.getContents(stack), -1));
					}
				}
			}
		}
	}
}