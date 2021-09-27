package com.github.elenterius.biomancy.client.renderer.entity.layers;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class FleshkinHeadLayer<T extends LivingEntity, M extends EntityModel<T> & IHasHead> extends LayerRenderer<T, M> {

	private final ItemStack blockToRender = new ItemStack(ModBlocks.FLESH_TENTACLE.get());

	public FleshkinHeadLayer(IEntityRenderer<T, M> entityRendererIn) {
		super(entityRendererIn);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		matrixStackIn.pushPose();
//			matrixStackIn.scale(1f, 1f, 1f);
		if (entity.isBaby()) {
			matrixStackIn.translate(0d, 0.03125d, 0d);
			matrixStackIn.scale(0.7f, 0.7f, 0.7f);
			matrixStackIn.translate(0d, 1d, 0d);
		}
		getParentModel().getHead().translateAndRotate(matrixStackIn);
		matrixStackIn.translate(0d, -0.25d, 0d);
		matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
		matrixStackIn.scale(0.625f, -0.625f, -0.625f);
		Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, blockToRender, ItemCameraTransforms.TransformType.HEAD, false, matrixStackIn, bufferIn, packedLightIn);
		matrixStackIn.popPose();
	}

}
