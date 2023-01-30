package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.github.elenterius.biomancy.world.entity.fleshblob.AbstractFleshBlob;
import com.github.elenterius.biomancy.world.entity.fleshblob.TumorFlag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;
import java.util.Locale;

public class FleshBlobRenderer extends GeoEntityRenderer<AbstractFleshBlob> {

	public FleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new FleshBlobModel<>());
		shadowRadius = 0.65f;
	}

	@Override
	public void renderEarly(AbstractFleshBlob entity, PoseStack poseStack, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		float scale = entity.getBlobScale();
		shadowRadius = 0.65f * scale;

		FleshBlobModel<AbstractFleshBlob> fleshBlobModel = (FleshBlobModel<AbstractFleshBlob>) modelProvider;
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

	@Override
	public RenderType getRenderType(AbstractFleshBlob fleshBlob, float partialTicks, PoseStack stack, @Nullable MultiBufferSource buffer, @Nullable VertexConsumer vertexBuilder, int packedLight, ResourceLocation textureLocation) {
		if (fleshBlob.hasCustomName()) {
			Component customName = fleshBlob.getCustomName();
			if (customName != null) {
				String name = customName.getContents().toLowerCase(Locale.ENGLISH);
				if (name.contains("party_blob")) {
					return ModRenderTypes.getCutoutPartyTime(textureLocation);
				}
			}
		}
		return RenderType.entityCutout(textureLocation);
	}

}
