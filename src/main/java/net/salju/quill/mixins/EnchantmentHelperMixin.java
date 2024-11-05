package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillTags;
import net.salju.quill.init.QuillConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.core.Holder;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	@Inject(method = "runIterationOnEquipment", at = @At("HEAD"))
	private static void runEquipment(LivingEntity target, EnchantmentHelper.EnchantmentInSlotVisitor v, CallbackInfo ci) {
		if (QuillConfig.ENCHS.get()) {
			if (target instanceof Animal animal) {
				ItemStack stack = animal.getBodyArmorItem();
				if (!stack.isEmpty() && stack.isEnchanted() && stack.getItem() instanceof ArmorItem armor) {
					EnchantmentHelper.runIterationOnItem(stack, animal.getType().is(QuillTags.BOOTS) ? EquipmentSlot.FEET : armor.getEquipmentSlot(), target, v);
				}
			}
		}
	}

	@Inject(method = "setEnchantments", at = @At("HEAD"), cancellable = true)
	private static void setEnchantments(ItemStack stack, ItemEnchantments map, CallbackInfo ci) {
		if (QuillConfig.ENCHS.get()) {
			ci.cancel();
			int i = 0;
			int m = QuillConfig.MAXENCH.get() * (stack.is(QuillTags.DOUBENCHS) ? 2 : 1);
			ItemEnchantments.Mutable book = new ItemEnchantments.Mutable(map);
			for (Holder<Enchantment> e : book.keySet()) {
				if (!e.is(EnchantmentTags.CURSE)) {
					if (i < m) {
						i++;
					} else {
						book.keySet().remove(e);
					}
				}
			}
			stack.set(EnchantmentHelper.getComponentType(stack), book.toImmutable());
		}
	}
}