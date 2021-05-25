package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.entity.model.FleshBlobModel;
import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FleshBlobRenderer extends MobRenderer<FleshBlobEntity, FleshBlobModel<FleshBlobEntity>> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/flesh_blob.png");
	private static final ResourceLocation AGGRESSIVE_TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/aggressive_flesh_blob.png");

	public FleshBlobRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new FleshBlobModel<>(), 0.65f);
	}

	@Override
	public void render(FleshBlobEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		shadowSize = 0.65f * (0.5f + entityIn.getBlobSize() * 0.5f);
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	protected void preRenderCallback(FleshBlobEntity entity, MatrixStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(0.999f, 0.999f, 0.999f);
		matrixStackIn.translate(0, 0.001f, 0);
		float x = 0.5f + entity.getBlobSize() * 0.5f;
		matrixStackIn.scale(x, x, x);
	}

	@Override
	@Nonnull
	public ResourceLocation getEntityTexture(FleshBlobEntity entity) {
		return entity.getFleshBlobData() == 1 ? AGGRESSIVE_TEXTURE : TEXTURE;
	}

}