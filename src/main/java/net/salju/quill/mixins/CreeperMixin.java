package net.salju.quill.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import net.salju.quill.init.QuillSounds;
import net.salju.quill.init.QuillConfig;
import net.salju.quill.network.Fireworks;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;

@Mixin(Creeper.class)
public abstract class CreeperMixin {
	@Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
	private void boom(CallbackInfo ci) {
		if (QuillConfig.CREEPER.get()) {
			Creeper creeper = (Creeper) (Object) this;
			float f = creeper.isPowered() ? 2.0F : 1.0F;
			if (creeper.level() instanceof ServerLevel lvl) {
				lvl.playSound(null, creeper.blockPosition(), SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.HOSTILE, 1.0F, 1.0F);
				if (creeper.isPowered() || (Math.random() <= 0.12)) {
					lvl.playSound(null, creeper.blockPosition(), QuillSounds.CHEERS.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
				}
				PacketDistributor.sendToPlayersNear(lvl, null, creeper.getX(), creeper.getY(), creeper.getZ(), 32, new Fireworks(creeper.blockPosition()));
				lvl.explode(creeper, Explosion.getDefaultDamageSource(lvl, creeper), null, creeper.getX(), creeper.getY(), creeper.getZ(), 3.0F * f, false, Level.ExplosionInteraction.NONE, ParticleTypes.CRIT, ParticleTypes.CRIT, QuillSounds.EMPTY);
				spawnLingeringCloud();
				creeper.discard();
			}
			ci.cancel();
		}
	}

	@Shadow
	abstract void spawnLingeringCloud();
}
