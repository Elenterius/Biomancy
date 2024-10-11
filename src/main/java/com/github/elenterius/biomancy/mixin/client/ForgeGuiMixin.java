package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin extends Gui {

	private ForgeGuiMixin(Minecraft minecraft, ItemRenderer itemRenderer) {
		super(minecraft, itemRenderer);
	}

	@ModifyExpressionValue(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
	private boolean canRenderFoodHunger(boolean hasHungerEffect) {
		//noinspection DataFlowIssue
		if (!hasHungerEffect && minecraft.player.hasEffect(ModMobEffects.WITHDRAWAL.get())) {
			return true;
		}
		return hasHungerEffect;
	}

}
