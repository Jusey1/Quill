package net.salju.quill.client.bundle;

import net.salju.quill.Quill;
import net.salju.quill.item.component.BundleHoldingTooltip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ClientBundleHoldingTooltip(BundleHoldingTooltip tip) implements ClientTooltipComponent {
	private static final ResourceLocation SLOT_HIGHLIGHT_BACK = ResourceLocation.fromNamespaceAndPath(Quill.MODID, "bundle/slot_highlight_back");
	private static final ResourceLocation SLOT_HIGHLIGHT_FRONT = ResourceLocation.fromNamespaceAndPath(Quill.MODID, "bundle/slot_highlight_front");

	@Override
	public int getHeight(Font f) {
		return this.backgroundHeight() + 4;
	}

	@Override
	public int getWidth(Font f) {
		return this.backgroundWidth();
	}

	@Override
	public boolean showTooltipWithItemInHand() {
		return true;
	}

	@Override
	public void renderImage(Font f, int x, int y, int z, int o, GuiGraphics gui) {
		int k = 0;
		if (!tip.getContents().isEmpty()) {
			for (int i = 0; i < this.gridSizeY(); i++) {
				for (int a = 0; a < this.gridSizeX(); a++) {
					int e = x + a * 18 + 1;
					int u = y + i * 20 + 1;
					this.renderSlot(e, u, k++, gui, f);
				}
			}
		}
	}

	private void renderSlot(int x, int y, int i, GuiGraphics gui, Font f) {
		if (i >= tip.getContents().size()) {
			//
		} else {
			if (tip.getContents().getSelectedItem() == i) {
				gui.blitSprite(RenderType::guiTextured, SLOT_HIGHLIGHT_BACK, x, y, 18, 18);
			}
			ItemStack stack = tip.getContents().getSpecificItem(i);
			gui.renderItem(stack, x + 1, y + 1, i);
			gui.renderItemDecorations(f, stack, x + 1, y + 1);
			if (tip.getContents().getSelectedItem() == i) {
				gui.blitSprite(RenderType::guiTextured, SLOT_HIGHLIGHT_FRONT, x, y, 18, 18);
			}
		}
	}

	private int backgroundWidth() {
		return this.gridSizeX() * 18 + 2;
	}

	private int backgroundHeight() {
		return this.gridSizeY() * 20 + 2;
	}

	private int gridSizeX() {
		if (tip.getContents().isEmpty()) {
			return 0;
		}
		return Math.max(2, (int) Math.ceil(Math.sqrt((double) tip.getContents().size() + 1)));
	}

	private int gridSizeY() {
		if (tip.getContents().isEmpty()) {
			return 0;
		}
		return (int) Math.ceil((double) tip.getContents().size() / this.gridSizeX());
	}
}