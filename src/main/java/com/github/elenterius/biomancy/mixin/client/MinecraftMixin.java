package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.client.renderer.ClientRenderHandler;
import com.github.elenterius.biomancy.item.IEntityUnveilerHeadSlotItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Inject(method = "isEntityGlowing", at = @At("HEAD"), cancellable = true)
	protected void onIsEntityGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity == ClientRenderHandler.HIGHLIGHTED_ENTITY || (entity != player && IEntityUnveilerHeadSlotItem.canUnveilEntity(player, entity))) {
			cir.setReturnValue(true);
		}
	}
}
