package com.github.elenterius.biomancy.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

	@Accessor("amplifier")
	void setAmplifier(int amplifier);

	@Accessor("duration")
	void setDuration(int ticks);

	@Accessor("factorData")
	Optional<MobEffectInstance.FactorData> getFactorData();

}
