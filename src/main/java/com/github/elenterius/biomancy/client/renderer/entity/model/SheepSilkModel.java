package com.github.elenterius.biomancy.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SheepSilkModel<T extends SheepEntity> extends QuadrupedModel<T> {

	private float headRotationAngleX;

	public SheepSilkModel() {
		super(12, 0f, false, 8f, 4f, 2f, 2f, 24);

		headModel = new ModelRenderer(this);
		headModel.setRotationPoint(0.0F, 6.0F, -8.0F);

		ModelRenderer headHair0 = new ModelRenderer(this);
		headHair0.setRotationPoint(3.0F, -3.0F, 0.0F);
		headModel.addChild(headHair0);
		setRotationAngle(headHair0, 0.0F, 0.0F, 2.9671F);
		headHair0.setTextureOffset(0, 0).addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

		ModelRenderer headHair1 = new ModelRenderer(this);
		headHair1.setRotationPoint(-3.0F, -3.0F, 0.0F);
		headModel.addChild(headHair1);
		setRotationAngle(headHair1, 0.0F, 0.0F, -2.9671F);
		headHair1.setTextureOffset(0, 4).addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 5.0F, 2.0F);

		ModelRenderer backHair = new ModelRenderer(this);
		backHair.setRotationPoint(0.0F, 0.0F, -1.0F);
		body.addChild(backHair);
		setRotationAngle(backHair, -3.1416F, 0.0F, 3.1416F);
		backHair.setTextureOffset(15, 4).addBox(0.0F, -8.0F, -3.0F, 0.0F, 14.0F, 3.0F, 0.0F, false);

		ModelRenderer hair0 = new ModelRenderer(this);
		hair0.setRotationPoint(-4.0F, 0.0F, -2.0F);
		body.addChild(hair0);
		setRotationAngle(hair0, -3.1416F, -0.1745F, 3.1416F);
		hair0.setTextureOffset(21, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 14.0F, 7.0F, 0.0F, false);

		ModelRenderer hair1 = new ModelRenderer(this);
		hair1.setRotationPoint(4.0F, 0.0F, -2.0F);
		body.addChild(hair1);
		setRotationAngle(hair1, -3.1416F, 0.1745F, 3.1416F);
		hair1.setTextureOffset(35, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 14.0F, 7.0F, 0.0F, false);
	}

	@Override
	public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
		headModel.rotationPointY = 6f + entityIn.getHeadRotationPointY(partialTick) * 9f;
		headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		headModel.rotateAngleX = headRotationAngleX;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}