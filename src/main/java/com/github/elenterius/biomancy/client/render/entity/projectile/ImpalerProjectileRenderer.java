package com.github.elenterius.biomancy.client.render.entity.projectile;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.ImpalerProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ImpalerProjectileRenderer extends GeoEntityRenderer<ImpalerProjectile> {

	public ImpalerProjectileRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new DefaultedEntityGeoModel<>(BiomancyMod.createRL("projectile/impaler_projectile")));
	}

	@Override
	public void actuallyRender(PoseStack poseStack, ImpalerProjectile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
		poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));

		super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

}
