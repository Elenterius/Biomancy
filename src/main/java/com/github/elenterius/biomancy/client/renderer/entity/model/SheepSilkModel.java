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

		head = new ModelRenderer(this);
		head.setPos(0.0F, 6.0F, -8.0F);

		ModelRenderer headHair0 = new ModelRenderer(this);
		headHair0.setPos(3.0F, -3.0F, 0.0F);
		head.addChild(headHair0);
		setRotationAngle(headHair0, 0.0F, 0.0F, 2.9671F);
		headHair0.texOffs(0, 0).addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

		ModelRenderer headHair1 = new ModelRenderer(this);
		headHair1.setPos(-3.0F, -3.0F, 0.0F);
		head.addChild(headHair1);
		setRotationAngle(headHair1, 0.0F, 0.0F, -2.9671F);
		headHair1.texOffs(0, 4).addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setPos(0.0F, 5.0F, 2.0F);

		ModelRenderer backHair = new ModelRenderer(this);
		backHair.setPos(0.0F, 0.0F, -1.0F);
		body.addChild(backHair);
		setRotationAngle(backHair, -3.1416F, 0.0F, 3.1416F);
		backHair.texOffs(15, 4).addBox(0.0F, -8.0F, -3.0F, 0.0F, 14.0F, 3.0F, 0.0F, false);

		ModelRenderer hair0 = new ModelRenderer(this);
		hair0.setPos(-4.0F, 0.0F, -2.0F);
		body.addChild(hair0);
		setRotationAngle(hair0, -3.1416F, -0.1745F, 3.1416F);
		hair0.texOffs(21, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 14.0F, 7.0F, 0.0F, false);

		ModelRenderer hair1 = new ModelRenderer(this);
		hair1.setPos(4.0F, 0.0F, -2.0F);
		body.addChild(hair1);
		setRotationAngle(hair1, -3.1416F, 0.1745F, 3.1416F);
		hair1.texOffs(35, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 14.0F, 7.0F, 0.0F, false);
	}

	@Override
	public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
		head.y = 6f + entityIn.getHeadEatPositionScale(partialTick) * 9f;
		headRotationAngleX = entityIn.getHeadEatAngleScale(partialTick);
	}

	@Override
	public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		head.xRot = headRotationAngleX;
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

}