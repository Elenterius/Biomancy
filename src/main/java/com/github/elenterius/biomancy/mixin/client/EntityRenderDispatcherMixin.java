package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.entity.misc.HitboxDebugInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

	//	@Inject(method = "renderHitbox", at = @At("HEAD"))
	//	private static void onRenderHitbox(PoseStack poseStack, VertexConsumer lineBuffer, Entity entity, float partialTicks, CallbackInfo ci) {
	//		if (entity instanceof HitboxDebugInfo customHitboxInfo) {
	//			customHitboxInfo.renderHitboxInfo(Minecraft.getInstance().getEntityRenderDispatcher(), poseStack, lineBuffer, partialTicks);
	//		}
	//	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;F)V"))
	private void render(Entity entity, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBuffer, int packedLight, CallbackInfo ci) {
		if (entity instanceof HitboxDebugInfo customHitboxInfo) {
			customHitboxInfo.renderHitboxInfo((EntityRenderDispatcher) (Object) this, poseStack, multiBuffer, packedLight, partialTicks);
		}
	}

}