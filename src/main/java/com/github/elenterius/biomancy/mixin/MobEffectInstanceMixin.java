package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.world.statuseffect.ArmorShredEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

	@Shadow
	public abstract int getAmplifier();

	@Inject(method = "update", at = @At(value = "HEAD"))
	protected void onUpdate(MobEffectInstance other, CallbackInfoReturnable<Boolean> cir) {
		if (other.getEffect() instanceof ArmorShredEffect) {
			int modifiedAmplifier = Math.max(other.getAmplifier(), getAmplifier()) + 1;
			((MobEffectInstanceAccessor) other).setAmplifier(modifiedAmplifier);
		}
	}

}
