package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin {

	@Inject(method = "canBreed", at = @At(value = "HEAD"), cancellable = true)
	private void onCanBreed(CallbackInfoReturnable<Boolean> cir) {
		Villager thisVillager = (Villager) (Object) this;

		if (thisVillager.hasEffect(ModMobEffects.LIBIDO.get()) && !thisVillager.isSleeping() && thisVillager.getAge() == 0) {
			cir.setReturnValue(true);
		}
	}

}
