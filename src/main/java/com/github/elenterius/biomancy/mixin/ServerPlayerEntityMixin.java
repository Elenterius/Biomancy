package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.handler.ItemDecayHandler;
import com.github.elenterius.biomancy.init.ModAttributes;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	public ServerPlayerEntityMixin(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
		super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
	}

	@Inject(method = "openContainer", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, remap = false, target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"))
	protected void biomancy_onContainerOpen(INamedContainerProvider p_213829_1_, CallbackInfoReturnable<OptionalInt> cir) {
		ItemDecayHandler.decayItemsInContainer((ServerPlayerEntity) (Object) this, openContainer, p_213829_1_);
	}

	/**
	 * Injects an max attack distance check. When the players attack distance is reduced and can't reach the target anymore this mixin cancels the attacks.
	 */
	@Inject(method = "attackTargetEntityWithCurrentItem", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/entity/player/PlayerEntity;attackTargetEntityWithCurrentItem(Lnet/minecraft/entity/Entity;)V"))
	protected void biomancy_onAttackTargetEntityWithCurrentItem(Entity targetEntity, CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		double maxDist = ModAttributes.getAttackReachDistance(player); // the max attack distance can be smaller than the default value of 3
		if (!player.isCreative() && player.getDistanceSq(targetEntity) > maxDist * maxDist) {
			ci.cancel(); //prevent attack if distance is larger than max attack distance
		}
	}
}
