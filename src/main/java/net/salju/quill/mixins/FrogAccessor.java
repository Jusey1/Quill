package net.salju.quill.mixins;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;

@Mixin(Frog.class)
public interface FrogAccessor {
	@Accessor("DATA_VARIANT_ID")
	static EntityDataAccessor<Holder<FrogVariant>> getFrogData() {
		throw new AssertionError();
	}
}