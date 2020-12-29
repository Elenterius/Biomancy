package com.github.elenterius.blightlings.mixin.client;

import com.github.elenterius.blightlings.init.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Shadow
	@Final
	private Minecraft mc;

	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerController;getBlockReachDistance()F"))
	protected float transformD0(PlayerController playerController) {
		return (float) ModAttributes.getAttackReachDistance(mc.player); // replace block read distance with attack distance
	}

	@ModifyArg(method = "getMouseOver", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;pick(DFZ)Lnet/minecraft/util/math/RayTraceResult;"))
	protected double transformPickRayTraceDistance(double d0) {
		//noinspection ConstantConditions
		return mc.playerController.getBlockReachDistance(); // use block reach distance for block pick
	}

	@Redirect(method = "getMouseOver", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/math/vector/Vector3d;squareDistanceTo(Lnet/minecraft/util/math/vector/Vector3d;)D"))
	protected double transformD2(Vector3d vector3d, Vector3d vec) {
		double distSq = vector3d.squareDistanceTo(vec);
		double attackDist = ModAttributes.getAttackReachDistance(mc.player);
		return distSq > attackDist * attackDist ? 9.1d : Math.min(distSq, 9d); //workaround to allow greater attack distance
	}
}
