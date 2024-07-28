package com.github.elenterius.biomancy.client.render.entity.mob;

import com.github.elenterius.biomancy.entity.mob.FleshPig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FleshPigRenderer<T extends FleshPig> extends GeoEntityRenderer<T> {

	public FleshPigRenderer(EntityRendererProvider.Context context) {
		super(context, new FleshPigModel<>());
		shadowRadius = 0.7f;
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, T animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (animatable.isBaby()) {
			widthScale = 0.5f;
			heightScale = 0.5f;
		}

		super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}

}
