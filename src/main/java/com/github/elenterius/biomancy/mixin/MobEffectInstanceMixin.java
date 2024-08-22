package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.mixin.accessor.MobEffectInstanceAccessor;
import com.github.elenterius.biomancy.statuseffect.StackingStatusEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

	@Shadow
	public abstract int getAmplifier();

	@Shadow
	@Final
	private MobEffect effect;

	@Inject(method = "update", at = @At(value = "HEAD"))
	private void onUpdate(MobEffectInstance other, CallbackInfoReturnable<Boolean> cir) {
		if (other.getEffect() != effect) return;

		if (other.getEffect() instanceof StackingStatusEffect stackingStatusEffect) {
			int modifiedAmplifier = StackingStatusEffect.computeAmplifierFrom(stackingStatusEffect, other.getAmplifier(), getAmplifier());
			((MobEffectInstanceAccessor) other).biomancy$setAmplifier(modifiedAmplifier);
		}
	}

}
