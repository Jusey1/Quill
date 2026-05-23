package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.Quill;
import net.salju.quill.events.QuillManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.LivingEntity;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
	@Inject(method = "createProjectile", at = @At("RETURN"))
	private void onGetArrow(Level level, LivingEntity shooter, ItemStack crossbow, ItemStack ammo, boolean critical, CallbackInfoReturnable<AbstractArrow> ci) {
		if (ci.getReturnValue() instanceof AbstractArrow arrow) {
			int s = QuillManager.getEnchantmentLevel(crossbow, shooter.level(), Quill.MODID, "sharpshooter");
			arrow.getPersistentData().putDouble("Sharpshooter", s);
		}
	}
}