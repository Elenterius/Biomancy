package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {

	@Inject(method = "causePlayerDamage", at = @At("HEAD"), cancellable = true)
	private static void onCausePlayerDamage(PlayerEntity player, CallbackInfoReturnable<DamageSource> cir) {
		if (player.getHeldItemMainhand().getItem() == ModItems.BIOFLESH_WAR_AXE.get()) {
			cir.setReturnValue(ModDamageSources.createBlightThornDamage("player", player));
		}
	}

	@Inject(method = "causeMobDamage", at = @At("HEAD"), cancellable = true)
	private static void onCauseMobDamage(LivingEntity mob, CallbackInfoReturnable<DamageSource> cir) {
		if (mob.getHeldItemMainhand().getItem() == ModItems.BIOFLESH_WAR_AXE.get()) {
			cir.setReturnValue(ModDamageSources.createBlightThornDamage("mob", mob));
		}
	}

}
