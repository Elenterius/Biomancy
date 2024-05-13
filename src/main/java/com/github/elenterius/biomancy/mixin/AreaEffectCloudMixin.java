package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AreaEffectCloud.class)
public class AreaEffectCloudMixin {

	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"))
	private boolean onlyApplyInstantEffectIfAllowed(MobEffect effect, Entity source, Entity indirectSource, LivingEntity livingEntity, int amplifier, double distanceMultiplier) {
		return StatusEffectHandler.canApplySplashEffectIfAllowed(effect, livingEntity);
	}

}
