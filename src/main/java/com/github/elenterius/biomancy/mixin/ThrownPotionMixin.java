package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionMixin {

	@WrapWithCondition(method = "applySplash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"))
	private boolean onlyApplyInstantEffectIfAllowed(MobEffect effect, Entity source, Entity indirectSource, LivingEntity livingEntity, int amplifier, double distanceMultiplier) {
		return StatusEffectHandler.canApplySplashEffectIfAllowed(effect, livingEntity);
	}

	@WrapWithCondition(method = "applySplash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean onlyApplyEffectIfAllowed(LivingEntity livingEntity, MobEffectInstance effectInstance, Entity source) {
		return StatusEffectHandler.canApplySplashEffectIfAllowed(effectInstance.getEffect(), livingEntity);
	}

}
