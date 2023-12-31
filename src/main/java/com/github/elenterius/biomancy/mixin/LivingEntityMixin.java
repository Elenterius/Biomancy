package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "isSensitiveToWater", at = @At(value = "HEAD"), cancellable = true)
	private void onIsSensitiveToWater(CallbackInfoReturnable<Boolean> cir) {
		LivingEntity livingEntity = (LivingEntity) (Object) this;
		if (livingEntity.hasEffect(ModMobEffects.CORROSIVE.get())) {
			cir.setReturnValue(true);
		}
	}

}
