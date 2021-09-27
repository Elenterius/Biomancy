package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.client.renderer.entity.model.BoomlingModel;
import com.github.elenterius.biomancy.entity.projectile.BoomlingProjectileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoomlingProjectileRenderer<T extends BoomlingProjectileEntity> extends EntityRenderer<T> {

	private final BoomlingModel<T> model = new BoomlingModel<>();

	public BoomlingProjectileRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	protected int getBlockLightLevel(T entityIn, BlockPos pos) {
		return 15;
	}

	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrixStackIn.pushPose();

		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot)));
		matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.xRotO, entityIn.xRot)));
		matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90f)); //rotate, so we see the bottom of the boomling

		matrixStackIn.translate(0, 1.351f, -0.15f); //"center"
		matrixStackIn.scale(-1, -1, 1); //flip

		//render normal model
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(model.renderType(getTextureLocation(entityIn)));
		model.renderToBuffer(matrixStackIn, vertexBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

		//render full-bright colored overlay
		vertexBuffer = bufferIn.getBuffer(BoomlingRenderer.RENDER_TYPE);
		int rgb = entityIn.getColor();
		float r = (rgb >> 16 & 255) / 255f;
		float g = (rgb >> 8 & 255) / 255f;
		float b = (rgb & 255) / 255f;
		model.renderToBuffer(matrixStackIn, vertexBuffer, 0xf00000, OverlayTexture.NO_OVERLAY, r, g, b, 1f);

		matrixStackIn.popPose();

		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return BoomlingRenderer.TEXTURE;
	}

}
