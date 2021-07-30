package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.handler.event.ItemDecayHandler;
import com.github.elenterius.biomancy.init.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

	@Unique
	private static double getSphericalDistSqBetween(Entity entityA, Entity entityB) {
		//point distances
		double distX = entityA.getPosX() - entityB.getPosX();
		double distY = (entityA.getPosY() + entityA.getHeight() * 0.5d) - (entityB.getPosY() + entityB.getHeight() * 0.5d); //y position is centered
		double distZ = entityA.getPosZ() - entityB.getPosZ();

		//inscribed circle radius distances
		double icrWSum = entityA.getWidth() * 0.5d + entityB.getWidth() * 0.5d;
		double icrHSum = entityA.getHeight() * 0.5d + entityB.getHeight() * 0.5d;
		double x = Math.max(Math.abs(distX) - icrWSum, 0);
		double y = Math.max(Math.abs(distY) - icrHSum, 0);
		double z = Math.max(Math.abs(distZ) - icrWSum, 0);

		return Math.min(x * x + y * y + z * z, distX * distX + distY * distY + distZ * distZ);
	}

	@Inject(method = "openContainer", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, remap = false, target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"))
	protected void biomancy_onContainerOpen(INamedContainerProvider containerProvider, CallbackInfoReturnable<OptionalInt> cir) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		ItemDecayHandler.decayItemsInContainer(player, player.openContainer, containerProvider);
	}

	/**
	 * Injects a max attack distance check. When the players attack distance is reduced and can't reach the target anymore this mixin cancels the attacks.
	 */
	@Deprecated //TODO: remove once forge merges the attack distance pull request
	@Inject(method = "attackTargetEntityWithCurrentItem", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/player/PlayerEntity;attackTargetEntityWithCurrentItem(Lnet/minecraft/entity/Entity;)V"))
	protected void biomancy_onAttackTargetEntityWithCurrentItem(Entity targetEntity, CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		double maxDist = ModAttributes.getCombinedReachDistance(player); // the max attack distance can be smaller than the default value of 3
		if (!player.isCreative() && getSphericalDistSqBetween(player, targetEntity) > maxDist * maxDist) {
			ci.cancel(); //prevent attack if distance is larger than max attack distance
		}
	}

}
