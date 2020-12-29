package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@Inject(method = "func_234570_el_", at = @At(value = "RETURN"))
	private static void onRegisterAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
		BlightlingsMod.LOGGER.debug(MarkerManager.getMarker("ATTRIBUTE INJECTION"), "adding attack distance attribute to player...");
		cir.getReturnValue().createMutableAttribute(ModAttributes.getAttackReachDistance());
	}
}
