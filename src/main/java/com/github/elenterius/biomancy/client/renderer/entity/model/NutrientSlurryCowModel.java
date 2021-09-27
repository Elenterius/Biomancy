package com.github.elenterius.biomancy.client.renderer.entity.model;

import com.github.elenterius.biomancy.entity.mutation.NutrientSlurryCowEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NutrientSlurryCowModel extends CowModel<NutrientSlurryCowEntity> {

	private float headRotationAngleX;
	private static final int color = 0xCCD65B;

	@Override
	public void prepareMobModel(NutrientSlurryCowEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
		head.y = 4f + entityIn.getHeadRotationPointY(partialTick) * 9f;
		headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);
	}

	@Override
	public void setupAnim(NutrientSlurryCowEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		head.xRot = headRotationAngleX;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		red = (float) (color >> 16 & 255) / 255f;
		green = (float) (color >> 8 & 255) / 255f;
		blue = (float) (color & 255) / 255f;
		super.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

}
