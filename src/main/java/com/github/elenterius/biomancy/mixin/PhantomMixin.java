package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PhantomMixin {

	@Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;)Z", at = @At(value = "HEAD"), cancellable = true)
	private void onCanAttack(LivingEntity target, TargetingConditions condition, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity attacker = (LivingEntity) (Object) this;
		if (attacker instanceof Phantom && target.hasEffect(ModMobEffects.DROWSY.get())) {
			cir.setReturnValue(false);
		}
	}

}
