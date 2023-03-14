package com.github.elenterius.biomancy.client.render.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.projectile.WitherProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WitherProjectileRenderer extends EntityRenderer<WitherProjectile> {

	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(BiomancyMod.createRL("wither_skull"), "main");
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/wither/wither.png");

	private final SkullModel skullModel;

	public WitherProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		skullModel = new SkullModel(context.bakeLayer(MODEL_LAYER));
		skullModel.setupAnim(0, -90f, 0);
	}

	@Override
	protected int getBlockLightLevel(WitherProjectile entityIn, BlockPos pos) {
		return 15;
	}

	@Override
	public void render(WitherProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0, 0.5F, 0);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90f));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

		VertexConsumer vertexConsumer = buffer.getBuffer(skullModel.renderType(getTextureLocation(entity)));
		skullModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
		poseStack.popPose();

		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(WitherProjectile entity) {
		return TEXTURE;
	}

}
