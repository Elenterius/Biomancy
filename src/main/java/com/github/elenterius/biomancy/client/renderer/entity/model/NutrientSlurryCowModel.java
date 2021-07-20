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
	public void setLivingAnimations(NutrientSlurryCowEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
		headModel.rotationPointY = 4f + entityIn.getHeadRotationPointY(partialTick) * 9f;
		headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);
	}

	@Override
	public void setRotationAngles(NutrientSlurryCowEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		headModel.rotateAngleX = headRotationAngleX;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		red = (float) (color >> 16 & 255) / 255f;
		green = (float) (color >> 8 & 255) / 255f;
		blue = (float) (color & 255) / 255f;
		super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

}
