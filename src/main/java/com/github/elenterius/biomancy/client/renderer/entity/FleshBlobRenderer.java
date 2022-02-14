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
	public void renderEarly(FleshBlob entity, PoseStack poseStack, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		float scale = FleshBlob.getScaleMultiplier(entity);
		shadowRadius = 0.65f * scale;

		FleshBlobModel<FleshBlob> fleshBlobModel = (FleshBlobModel<FleshBlob>) getGeoModelProvider();
		AnimationProcessor<?> animationProcessor = fleshBlobModel.getAnimationProcessor();

		int flag = entity.getTumorFlags();
		for (TumorFlag tumorFlag : TumorFlag.values()) {
			IBone tumor = animationProcessor.getBone(tumorFlag.getBoneId());
			tumor.setHidden(tumorFlag.isNotSet(flag));
		}

		poseStack.scale(0.999f, 0.999f, 0.999f);
		poseStack.translate(0, 0.001f, 0);
		poseStack.scale(scale, scale, scale);

		super.renderEarly(entity, poseStack, ticks, renderTypeBuffer, vertexBuilder, packedLight, packedOverlay, red, green, blue, partialTicks);
	}

}
