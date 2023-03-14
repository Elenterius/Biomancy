package com.github.elenterius.biomancy.client.render.entity;

import com.github.elenterius.biomancy.world.entity.ownable.Boomling;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

public class BoomlingRenderer extends GeoEntityRenderer<Boomling> {

	public BoomlingRenderer(EntityRendererProvider.Context context) {
		super(context, new BoomlingModel<>());
		shadowRadius = 0.2f;
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
	protected float getDeathMaxRotation(Boomling entityLivingBaseIn) {
		return 180f;
	}

}
