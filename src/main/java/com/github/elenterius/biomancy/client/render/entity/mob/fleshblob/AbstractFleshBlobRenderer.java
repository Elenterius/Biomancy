package com.github.elenterius.biomancy.client.render.entity.mob.fleshblob;

import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.entity.mob.fleshblob.TumorFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class AbstractFleshBlobRenderer<T extends FleshBlob> extends GeoEntityRenderer<T> {

	protected AbstractFleshBlobRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
		super(context, model);
		shadowRadius = 0.65f;
	}

	@Override
	public GeoEntityRenderer<T> withScale(float scale) {
		shadowRadius = 0.65f * scale;
		return withScale(scale, scale);
	}

	@Override
	public void preRender(PoseStack poseStack, T fleshBlob, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

		withScale(fleshBlob.getBlobScale());

		AnimationProcessor<?> animationProcessor = getGeoModel().getAnimationProcessor();

		int flag = fleshBlob.getTumorFlags();
		for (TumorFlag tumorFlag : TumorFlag.values()) {
			CoreGeoBone tumor = animationProcessor.getBone(tumorFlag.getBoneId());
			tumor.setHidden(tumorFlag.isNotSet(flag));
		}

		super.preRender(poseStack, fleshBlob, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, T animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (!isReRender && (widthScale != 1 || heightScale != 1)) {
			poseStack.scale(0.999f, 0.999f, 0.999f);
			poseStack.translate(0, 0.001f, 0);
			poseStack.scale(widthScale, heightScale, widthScale);
		}
	}

}
