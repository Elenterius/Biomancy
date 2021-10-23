package com.github.elenterius.biomancy.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;


@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

	@Nullable
	@Invoker("getHurtSound")
	SoundEvent biomancy_getHurtSound(DamageSource damageSource);

	@Nullable
	@Invoker("getDeathSound")
	SoundEvent biomancy_getDeathSound();

	@Invoker("getSoundVolume")
	float biomancy_getSoundVolume();

	@Invoker("getVoicePitch")
	float biomancy_getVoicePitch();

}
