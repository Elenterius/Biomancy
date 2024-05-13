package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Arrow.class)
public class ArrowMixin {

	@WrapWithCondition(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean onlyApplyEffectIfAllowed(LivingEntity target, MobEffectInstance effectInstance, Entity source) {
		return StatusEffectHandler.canApplySplashEffectIfAllowed(effectInstance.getEffect(), target);
	}

}
