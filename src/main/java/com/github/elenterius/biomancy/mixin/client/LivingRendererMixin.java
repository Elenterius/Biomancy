package com.github.elenterius.biomancy.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingRenderer.class)
public abstract class LivingRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M> {

//	@Shadow
//	protected M entityModel;
//
	protected LivingRendererMixin(EntityRendererManager renderManager) {
		super(renderManager);
	}

	//FIXME: Incompatible with Optifine. Removing this doesn't affect gameplay.
	//TODO: Write more robust & better mixin.
//	@Inject(method = "render", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "FIELD", ordinal = 5, shift = At.Shift.AFTER,
//			target = "Lnet/minecraft/client/renderer/entity/LivingRenderer;entityModel:Lnet/minecraft/client/renderer/entity/model/EntityModel;"))
//	protected void biomancy_onPostRenderModel(LivingEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci,
//											  boolean shouldSit, float f, float f1, float f2, float f6, float f7, float f8, float f5, Minecraft minecraft, boolean flag, boolean flag1, boolean flag2,
//											  RenderType rendertype, IVertexBuilder ivertexbuilder, int i) {
//		// if the entity is invisible but visible to player then render the entity a second time
//		if (flag1) {
//			entityModel.render(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, 0.5F); //
//		}
//	}

}
