package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.PanicGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PanicGoal.class)
public abstract class PanicGoalMixin {
	@Shadow
	@Final
	protected CreatureEntity creature;

	@Inject(method = "shouldExecute", at = @At("HEAD"), cancellable = true)
	protected void biomancy_onShouldExecute(CallbackInfoReturnable<Boolean> cir) {
		if (creature.isPotionActive(ModEffects.RAVENOUS_HUNGER.get())) {
			cir.setReturnValue(false);
		}
	}
}
