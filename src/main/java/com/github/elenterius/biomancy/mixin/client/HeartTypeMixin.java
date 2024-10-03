package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.HeartType.class)
public abstract class HeartTypeMixin {

	@Inject(method = "forPlayer", at = @At("HEAD"), cancellable = true)
	private static void onForPlayer(Player player, CallbackInfoReturnable<Gui.HeartType> cir) {
		if (player.hasEffect(ModMobEffects.WITHDRAWAL.get())) {
			cir.setReturnValue(Gui.HeartType.POISIONED);
		}
	}

}