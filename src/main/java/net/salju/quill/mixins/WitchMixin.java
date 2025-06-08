package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.salju.quill.init.QuillConfig;
import net.salju.quill.init.QuillWitch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownSplashPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

@Mixin(Witch.class)
public abstract class WitchMixin {
	@Inject(method = "aiStep", at = @At("HEAD"))
	private void step(CallbackInfo ci) {
		if (QuillConfig.FROGGO.get()) {
			Witch hag = (Witch) (Object) this;
			if (hag.level() instanceof ServerLevel lvl && hag.isAlive()) {
				Villager trader = lvl.getNearestEntity(Villager.class, TargetingConditions.DEFAULT, hag, hag.getX(), hag.getY(), hag.getZ(), hag.getBoundingBox().inflate(12.85D));
				if (trader != null && trader.getVillagerData().profession().is(VillagerProfession.NITWIT)) {
					hag.setTarget(trader);
				}
			}
		}
	}

	@Inject(method = {"performRangedAttack"}, at = {@At("HEAD")}, cancellable = true)
	private void potion(LivingEntity target, float f1, CallbackInfo ci) {
		if (QuillConfig.FROGGO.get()) {
			Witch hag = (Witch) (Object) this;
			if (target instanceof Villager trader) {
				if (trader.getVillagerData().profession().is(VillagerProfession.NITWIT) && !hag.isDrinkingPotion()) {
					double d0 = trader.getX() + (trader.getDeltaMovement()).x - hag.getX();
					double d1 = trader.getEyeY() - 1.1F - hag.getY();
					double d2 = trader.getZ() + (trader.getDeltaMovement()).z - hag.getZ();
					double d3 = Math.sqrt(d0 * d0 + d2 * d2);
					if (hag.level() instanceof ServerLevel lvl) {
						Projectile.spawnProjectileUsingShoot(ThrownSplashPotion::new, lvl, PotionContents.createItemStack(Items.SPLASH_POTION, QuillWitch.FROG), hag, d0, d1 + d3 * 0.2, d2, 0.75F, 8.0F);
					}
					if (!hag.isSilent()) {
						hag.level().playSound(null, hag.getX(), hag.getY(), hag.getZ(), SoundEvents.WITCH_THROW, hag.getSoundSource(), 1.0F, 0.8F + hag.getRandom().nextFloat() * 0.4F);
					}
					hag.setTarget(null);
					ci.cancel();
				}
			}
		}
	}
}