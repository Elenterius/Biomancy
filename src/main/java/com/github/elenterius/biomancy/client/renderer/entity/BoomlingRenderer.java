package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.client.model.entity.BoomlingModel;
import com.github.elenterius.biomancy.world.entity.ownable.Boomling;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

import javax.annotation.Nullable;

public class BoomlingRenderer extends GeoEntityRenderer<Boomling> {

	public BoomlingRenderer(EntityRendererProvider.Context context) {
		super(context, new BoomlingModel<>());
		shadowRadius = 0.65f;
		addLayer(new GeoLayerRenderer<>(this) {
			@Override
			public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Boomling boomling, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				int rgb = boomling.getColor();
				float r = (rgb >> 16 & 255) / 255f;
				float g = (rgb >> 8 & 255) / 255f;
				float b = (rgb & 255) / 255f;
				renderModel(getGeoModelProvider(), BoomlingModel.OVERLAY_TEXTURE, poseStack, buffer, packedLight, boomling, partialTicks, r, g, b);
			}

			@Override
			public RenderType getRenderType(ResourceLocation textureLocation) {
				return RenderType.eyes(textureLocation);
			}
		});
	}

	@Override
	public void renderEarly(Boomling boomling, PoseStack poseStack, float ticks, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		float v = boomling.getFuseFlashIntensity(partialTicks);
		float w = 1f + Mth.sin(v * 100f) * v * 0.01f;
		v = Mth.clamp(v, 0f, 1f);
		v = (v * v) * (v * v);
		float xz = (1f + v * 0.4f) * w;
		poseStack.scale(xz, (1f + v * 0.1f) / w, xz);

		super.renderEarly(boomling, poseStack, ticks, renderTypeBuffer, vertexBuilder, packedLight, packedOverlay, red, green, blue, partialTicks);
	}

	@Override
	protected float getDeathMaxRotation(Boomling entityLivingBaseIn) {
		return 180f;
	}

}
