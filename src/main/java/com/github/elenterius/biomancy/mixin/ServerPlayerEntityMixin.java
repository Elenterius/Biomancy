package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.handler.event.ItemDecayHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

	@Inject(method = "openMenu", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, remap = false, target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"))
	protected void biomancy_onContainerOpen(INamedContainerProvider containerProvider, CallbackInfoReturnable<OptionalInt> cir) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		ItemDecayHandler.decayItemsInContainer(player, player.containerMenu, containerProvider);
	}

}
