package net.salju.quill.item.component;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record BundleHoldingTooltip(BundleHoldingContents contents) implements TooltipComponent {
    public BundleHoldingContents getContents() {
        return this.contents;
    }
}