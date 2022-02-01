package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.client.model.entity.FleshBlobModel;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.world.entity.fleshblob.TumorFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class FleshBlobRenderer extends GeoEntityRenderer<FleshBlob> {

	public FleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new FleshBlobModel<>());
		shadowRadius = 0.65f;
	}

	@Override
	public void render(FleshBlob entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		shadowRadius = 0.65f * (0.5f + entity.getBlobSize() * 0.5f);
		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}

	@Override
	public void renderEarly(FleshBlob fleshBlob, PoseStack poseStack, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		FleshBlobModel<FleshBlob> fleshBlobModel = (FleshBlobModel<FleshBlob>) getGeoModelProvider();
		AnimationProcessor<?> animationProcessor = fleshBlobModel.getAnimationProcessor();

		int flag = fleshBlob.getTumorFlags();
		for (TumorFlag tumorFlag : TumorFlag.values()) {
			IBone tumor = animationProcessor.getBone(tumorFlag.getBoneId());
			tumor.setHidden(tumorFlag.isNotSet(flag));
		}

		poseStack.scale(0.999f, 0.999f, 0.999f);
		poseStack.translate(0, 0.001f, 0);
		float v = 0.5f + fleshBlob.getBlobSize() * 0.5f;
		poseStack.scale(v, v, v);

		super.renderEarly(fleshBlob, poseStack, ticks, renderTypeBuffer, vertexBuilder, packedLight, packedOverlay, red, green, blue, partialTicks);
	}

}
