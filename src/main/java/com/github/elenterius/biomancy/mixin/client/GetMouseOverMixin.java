package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModAttributes;
import com.github.elenterius.biomancy.mixin.ServerPlayerEntityMixin;
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

/**
 * A Mixin that allows greater attack distance. (hacky solution)
 * <br>
 * The method {@link GameRenderer#pick} updates the raytrace result of minecraft.hitResult.
 * <br>
 * On left-click the client uses minecraft.hitResult to verify an entity was hit and sends the action to attack the entity to the server.
 * The server verifies the player distance to the attack target is smaller than 6 and attacks the entity.
 * <br>
 * Note: {@link ServerPlayerEntityMixin} adds a max attack distance check.
 */
@Deprecated
@Mixin(GameRenderer.class)
public abstract class GetMouseOverMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Redirect(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerController;getPickRange()F"))
	protected float biomancy_transformD0(PlayerController playerController) {
		return (float) ModAttributes.getCombinedReachDistance(minecraft.player); // replace block reach distance with attack distance
	}

	@ModifyArg(method = "pick", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;pick(DFZ)Lnet/minecraft/util/math/RayTraceResult;"))
	protected double biomancy_transformPickRayTraceDistance(double d0) {
		//noinspection ConstantConditions
		return minecraft.gameMode.getPickRange(); // use block reach distance for block pick
	}

	@Redirect(method = "pick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/math/vector/Vector3d;distanceToSqr(Lnet/minecraft/util/math/vector/Vector3d;)D"))
	protected double biomancy_transformD2(Vector3d vector3d, Vector3d vec) {
		double distSq = vector3d.distanceToSqr(vec);
		//noinspection ConstantConditions
		double maxDist = !minecraft.player.isCreative() ? ModAttributes.getCombinedReachDistance(minecraft.player) : 6d;
		return distSq > maxDist * maxDist ? 9.1d : Math.min(distSq, 9d); //workaround to allow greater attack distance
	}

}
