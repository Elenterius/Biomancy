package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.entity.mob.Boomling;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BoomlingRenderer extends GeoEntityRenderer<Boomling> {

	public BoomlingRenderer(EntityRendererProvider.Context context) {
		super(context, new BoomlingModel<>());
		shadowRadius = 0.2f;

		addRenderLayer(new GeoRenderLayer<Boomling>(this) {

			@Override
			public void render(PoseStack poseStack, Boomling boomling, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
				int rgb = boomling.getColor();
				float r = (rgb >> 16 & 255) / 255f;
				float g = (rgb >> 8 & 255) / 255f;
				float b = (rgb & 255) / 255f;
				RenderType emissiveRenderType = RenderType.eyes(BoomlingModel.OVERLAY_TEXTURE);
				getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, emissiveRenderType, bufferSource.getBuffer(emissiveRenderType), partialTick, 15728640, OverlayTexture.NO_OVERLAY, r, g, b, 1);
			}

		});
	}

	@Override
	protected float getDeathMaxRotation(Boomling entityLivingBaseIn) {
		return 180f;
	}

}
