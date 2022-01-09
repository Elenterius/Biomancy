package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.model.entity.FleshBlobModel;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FleshBlobRenderer extends MobRenderer<FleshBlob, FleshBlobModel<FleshBlob>> {

	public static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob.png");
	public static final ResourceLocation AGGRESSIVE_TEXTURE = BiomancyMod.createRL("textures/entity/aggressive_flesh_blob.png");

	public FleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new FleshBlobModel<>(context.bakeLayer(FleshBlobModel.MODEL_LAYER)), 0.65f);
	}

	@Override
	public void render(FleshBlob entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		shadowRadius = 0.65f * (0.5f + entity.getBlobSize() * 0.5f);
		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}

	@Override
	protected void scale(FleshBlob fleshBlob, PoseStack matrixStack, float partialTickTime) {
		matrixStack.scale(0.999f, 0.999f, 0.999f);
		matrixStack.translate(0, 0.001f, 0);
		float blobSize = fleshBlob.getBlobSize();
		float v = 0.5f + blobSize * 0.5f;
		float squish = 0.5f / (Mth.lerp(partialTickTime, fleshBlob.squishTracker.prevSquish, fleshBlob.squishTracker.squish) / v + 0.5f);
		matrixStack.scale(blobSize * squish, blobSize * squish, blobSize * squish);
	}

	@Override
	public ResourceLocation getTextureLocation(FleshBlob entity) {
		return entity.isHangry() ? AGGRESSIVE_TEXTURE : TEXTURE;
	}

}
