package net.salju.quill.effect;

import net.salju.quill.Quill;
import net.salju.quill.init.QuillTags;
import net.salju.quill.mixins.FrogAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.phys.Vec3;

public class FrogEffect extends MobEffect {
	public FrogEffect(MobEffectCategory cate, int i) {
		super(cate, i);
	}

	@Override
	public String getDescriptionId() {
		return "effect.quill.froggo";
	}

	@Override
	public void applyInstantenousEffect(ServerLevel lvl, Entity direct, Entity owner, LivingEntity target, int i, double d) {
		if (target.getType().is(QuillTags.FROGGO)) {
			Holder<FrogVariant> witch = lvl.registryAccess().lookupOrThrow(Registries.FROG_VARIANT).getOrThrow(ResourceKey.create(Registries.FROG_VARIANT, ResourceLocation.fromNamespaceAndPath(Quill.MODID, "witch")));
			lvl.playSound(null, target.blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.HOSTILE, 1.0F, 1.0F);
			Frog froggo = EntityType.FROG.create(lvl, EntitySpawnReason.CONVERSION);
			froggo.snapTo(Vec3.atBottomCenterOf(target.blockPosition()));
			froggo.addEffect(new MobEffectInstance(MobEffects.LUCK, 3600, 0, false, true));
			froggo.setPersistenceRequired();
			froggo.getEntityData().set(FrogAccessor.getFrogData(), witch);
			lvl.addFreshEntity(froggo);
			target.discard();
		}
	}

	@Override
	public boolean isInstantenous() {
		return true;
	}
}