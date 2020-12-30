package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@Inject(method = "func_234570_el_", at = @At(value = "RETURN"))
	private static void onRegisterAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
		BlightlingsMod.LOGGER.debug(MarkerManager.getMarker("ATTRIBUTE INJECTION"), "adding attack distance attribute to player...");
		cir.getReturnValue().createMutableAttribute(ModAttributes.getAttackReachDistance());
	}

	@Redirect(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getDistanceSq(Lnet/minecraft/entity/Entity;)D"))
	protected double transformSweepDistSq(PlayerEntity playerEntity, Entity entityIn) {
		double distSq = playerEntity.getDistanceSq(entityIn);
		double maxDist = ModAttributes.getAttackReachDistance(playerEntity);
		if (distSq < maxDist * maxDist) {
			return distSq < 9d ? distSq : 8.99d; //hack to allow sweep attacks with attack distance greater than 3
		}
		return distSq;
	}
}
