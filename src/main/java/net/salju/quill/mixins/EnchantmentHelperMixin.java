package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	@Inject(method = "runIterationOnEquipment", at = @At("HEAD"))
	private static void runEquipment(LivingEntity target, EnchantmentHelper.EnchantmentInSlotVisitor v, CallbackInfo ci) {
		if (QuillConfig.ENCHS.get()) {
			if (target instanceof Animal animal) {
				ItemStack stack = animal.getBodyArmorItem();
				if (!stack.isEmpty() && stack.isEnchanted() && stack.getItem() instanceof ArmorItem armor) {
					EnchantmentHelper.runIterationOnItem(stack, armor.getEquipmentSlot(stack), target, v);
				}
			}
		}
	}
}