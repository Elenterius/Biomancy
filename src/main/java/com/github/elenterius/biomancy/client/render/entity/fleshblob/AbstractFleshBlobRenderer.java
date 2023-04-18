package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.entity.fleshblob.AbstractFleshBlob;
import com.github.elenterius.biomancy.entity.fleshblob.TumorFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public abstract class AbstractFleshBlobRenderer<T extends AbstractFleshBlob> extends GeoEntityRenderer<T> {

	protected AbstractFleshBlobRenderer(EntityRendererProvider.Context context, AnimatedGeoModel<T> modelProvider) {
		super(context, modelProvider);
		shadowRadius = 0.65f;
	}

	@Override
	public void renderEarly(T entity, PoseStack poseStack, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		float scale = entity.getBlobScale();
		shadowRadius = 0.65f * scale;

		AnimationProcessor<?> animationProcessor = modelProvider.getAnimationProcessor();

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
