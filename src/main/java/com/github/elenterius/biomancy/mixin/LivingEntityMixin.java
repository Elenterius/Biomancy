package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Shadow
	public abstract AttributeModifierManager getAttributeManager();

	@Shadow
	public abstract boolean isPotionActive(Effect potionIn);

	@Inject(method = "getAttributeValue", at = @At("HEAD"), cancellable = true)
	protected void biomancy_onGetAttributeValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
		if (attribute == Attributes.ATTACK_DAMAGE && !getAttributeManager().hasAttributeInstance(Attributes.ATTACK_DAMAGE)) {
			cir.setReturnValue(isPotionActive(ModEffects.RAVENOUS_HUNGER.get()) ? 0.25d : 0d);
		}
	}
}
