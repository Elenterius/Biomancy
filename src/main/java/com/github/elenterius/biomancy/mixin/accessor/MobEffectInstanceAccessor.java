package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

	@Accessor("amplifier")
	void biomancy$setAmplifier(int amplifier);

	@Accessor("duration")
	void biomancy$setDuration(int ticks);

	@Accessor("factorData")
	Optional<MobEffectInstance.FactorData> biomancy$getFactorData();

}
