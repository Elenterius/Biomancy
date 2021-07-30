package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.client.renderer.entity.model.NutrientSlurryCowModel;
import com.github.elenterius.biomancy.entity.mutation.NutrientSlurryCowEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class NutrientSlurryCowRenderer extends MobRenderer<NutrientSlurryCowEntity, NutrientSlurryCowModel> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/cow/cow.png");

	public NutrientSlurryCowRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new NutrientSlurryCowModel(), 0.7f);
	}

	@Override
	public void render(NutrientSlurryCowEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getEntityTexture(NutrientSlurryCowEntity entity) {
		return TEXTURE;
	}

}
