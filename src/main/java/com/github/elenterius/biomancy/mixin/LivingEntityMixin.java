package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.handler.event.StatusEffectHandler;
import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.statuseffect.AdrenalineEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> pType, World pLevel) {
		super(pType, pLevel);
	}

	@Shadow
	public abstract AttributeModifierManager getAttributes();

	@Shadow
	public abstract boolean hasEffect(Effect potionIn);

	@Shadow
	public abstract boolean addEffect(EffectInstance pEffectInstance);

	@Shadow
	@Final
	private Map<Effect, EffectInstance> activeEffects;

	@Inject(method = "getAttributeValue", at = @At("HEAD"), cancellable = true)
	protected void biomancy_onGetAttributeValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
		if (attribute == Attributes.ATTACK_DAMAGE && !getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
			cir.setReturnValue(hasEffect(ModEffects.RAVENOUS_HUNGER.get()) ? 0.25d : 0d);
		}
	}

	@Unique
	private EffectInstance adrenalFatigueEffectInstance;

	@Inject(method = "removeAllEffects", at = @At("HEAD"))
	protected void biomancy_onPreRemoveAllEffects(CallbackInfoReturnable<Boolean> cir) {
		if (!level.isClientSide) {
			AdrenalineEffect effect = ModEffects.ADRENALINE_RUSH.get();
			EffectInstance effectInstance = activeEffects.get(effect);
			if (effectInstance != null) {
				adrenalFatigueEffectInstance = StatusEffectHandler.createAdrenalFatigueEffectFrom(effectInstance);
			}
		}
	}

	@Inject(method = "removeAllEffects", at = @At("TAIL"))
	protected void biomancy_onPostRemoveAllEffects(CallbackInfoReturnable<Boolean> cir) {
		if (!level.isClientSide && adrenalFatigueEffectInstance != null) {
			if (!activeEffects.containsKey(ModEffects.ADRENALINE_RUSH.get())) { //check if the effect was actually removed
				addEffect(adrenalFatigueEffectInstance);
			}
			adrenalFatigueEffectInstance = null;
		}
	}

}
