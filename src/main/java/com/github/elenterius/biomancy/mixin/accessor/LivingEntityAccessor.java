package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

	@Nullable
	@Invoker("getHurtSound")
	SoundEvent biomancy$getHurtSound(DamageSource damageSource);

	@Nullable
	@Invoker("getDeathSound")
	SoundEvent biomancy$getDeathSound();

	@Invoker("getSoundVolume")
	float biomancy$getSoundVolume();

	@Invoker("getVoicePitch")
	float biomancy$getVoicePitch();

}
