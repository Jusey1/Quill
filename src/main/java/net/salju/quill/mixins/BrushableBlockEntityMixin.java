package net.salju.quill.mixins;

import net.salju.quill.events.QuillManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {
	@Inject(method = "brush", at = @At("HEAD"))
	public void speed(long time, Player player, Direction dir, CallbackInfoReturnable<Boolean> ci) {
		if (QuillConfig.ENCHS.get()) {
			int e = QuillManager.getEnchantmentLevel(player.getUseItem(), player.level(), "minecraft", "efficiency");
			if (e > 0) {
				if (this.brushCount <= e * 2) {
					++this.brushCount;
					if (e >= 4) {
						++this.brushCount;
					}
				}
			}
		}
	}

	@Shadow
	private int brushCount;
}
