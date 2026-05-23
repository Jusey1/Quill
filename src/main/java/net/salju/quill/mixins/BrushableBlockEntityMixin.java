package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.events.QuillManager;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {
	@Inject(method = "brush", at = @At("HEAD"))
	public void speed(long time, ServerLevel lvl, LivingEntity target, Direction dir, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		int e = QuillManager.getEnchantmentLevel(stack, lvl, "minecraft", "efficiency");
		if (e > 0) {
			if (this.brushCount <= e * 2) {
				++this.brushCount;
				if (e >= 4) {
					++this.brushCount;
				}
			}
		}
	}

	@Shadow
	private int brushCount;
}
