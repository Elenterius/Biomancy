package com.github.elenterius.biomancy.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BeetlingModel<T extends Entity> extends EntityModel<T> {
	private final ModelRenderer body;
	private final ModelRenderer armor;
	private final ModelRenderer tail;
	private final ModelRenderer head;
	private final ModelRenderer antlers;
	private final ModelRenderer mouth_r1;
	private final ModelRenderer mouth_r2;
	private final ModelRenderer leftLeg0;
	private final ModelRenderer leftLeg2;
	private final ModelRenderer leftLeg3;
	private final ModelRenderer rightLeg0;
	private final ModelRenderer rightLeg1;
	private final ModelRenderer rightLeg2;

	public BeetlingModel() {
		textureWidth = 32;
		textureHeight = 32;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 23.0F, 0.0F);
		body.setTextureOffset(0, 7).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 1.0F, 4.0F, 0.0F, false);

		armor = new ModelRenderer(this);
		armor.setRotationPoint(0.5F, -1.0F, -0.5F);
		body.addChild(armor);
		setRotationAngle(armor, -0.0873F, 0.0F, 0.0F);
		armor.setTextureOffset(0, 0).addBox(-2.5F, -1.8F, -1.05F, 4.0F, 2.0F, 5.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, -0.25F, 2.5F);
		body.addChild(tail);
		setRotationAngle(tail, -0.0873F, 0.0F, 0.0F);
		tail.setTextureOffset(7, 7).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 3.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -1.25F, -1.25F);
		body.addChild(head);
		head.setTextureOffset(12, 12).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(0, 0).addBox(-1.2F, -0.25F, -1.75F, 0.5F, 0.5F, 0.5F, 0.0F, false);
		head.setTextureOffset(0, 0).addBox(0.7F, -0.25F, -1.75F, 0.5F, 0.5F, 0.5F, 0.0F, false);

		antlers = new ModelRenderer(this);
		antlers.setRotationPoint(0.0F, -0.5F, -1.5F);
		head.addChild(antlers);
		antlers.setTextureOffset(0, 12).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 3.0F, 0.0F, 0.0F, false);

		mouth_r1 = new ModelRenderer(this);
		mouth_r1.setRotationPoint(-0.4625F, 1.075F, -1.9F);
		head.addChild(mouth_r1);
		setRotationAngle(mouth_r1, -0.2618F, 0.0F, -0.1745F);
		mouth_r1.setTextureOffset(0, 2).addBox(-0.3125F, -0.3125F, -0.3125F, 0.625F, 0.625F, 0.625F, 0.0F, false);

		mouth_r2 = new ModelRenderer(this);
		mouth_r2.setRotationPoint(0.475F, 1.075F, -1.9F);
		head.addChild(mouth_r2);
		setRotationAngle(mouth_r2, -0.2618F, 0.0F, 0.1745F);
		mouth_r2.setTextureOffset(0, 2).addBox(-0.3125F, -0.3125F, -0.3125F, 0.625F, 0.625F, 0.625F, 0.0F, true);

		leftLeg0 = new ModelRenderer(this);
		leftLeg0.setRotationPoint(1.5F, 0.0F, -1.0F);
		body.addChild(leftLeg0);
		setRotationAngle(leftLeg0, -0.4363F, 0.0F, -0.7854F);
		leftLeg0.setTextureOffset(8, 15).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		leftLeg2 = new ModelRenderer(this);
		leftLeg2.setRotationPoint(1.5F, 0.0F, 0.25F);
		body.addChild(leftLeg2);
		setRotationAngle(leftLeg2, 0.0F, 0.0F, -0.7854F);
		leftLeg2.setTextureOffset(4, 15).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		leftLeg3 = new ModelRenderer(this);
		leftLeg3.setRotationPoint(1.5F, 0.0F, 1.5F);
		body.addChild(leftLeg3);
		setRotationAngle(leftLeg3, 0.4363F, 0.0F, -0.7854F);
		leftLeg3.setTextureOffset(0, 15).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		rightLeg0 = new ModelRenderer(this);
		rightLeg0.setRotationPoint(-1.5F, 0.0F, -1.0F);
		body.addChild(rightLeg0);
		setRotationAngle(rightLeg0, -0.4363F, 0.0F, 0.7854F);
		rightLeg0.setTextureOffset(8, 15).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, true);

		rightLeg1 = new ModelRenderer(this);
		rightLeg1.setRotationPoint(-1.5F, 0.0F, 0.25F);
		body.addChild(rightLeg1);
		setRotationAngle(rightLeg1, 0.0F, 0.0F, 0.7854F);
		rightLeg1.setTextureOffset(4, 15).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, true);

		rightLeg2 = new ModelRenderer(this);
		rightLeg2.setRotationPoint(-1.5F, 0.0F, 1.5F);
		body.addChild(rightLeg2);
		setRotationAngle(rightLeg2, 0.4363F, 0.0F, 0.7854F);
		rightLeg2.setTextureOffset(0, 15).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, true);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
		head.rotateAngleX = headPitch * ((float) Math.PI / 180F);

		tail.rotateAngleX = -0.0873F + MathHelper.cos(0.25f * ageInTicks) * 0.1f;

		float f = 0.7854F;
		leftLeg0.rotateAngleZ = leftLeg2.rotateAngleZ = leftLeg3.rotateAngleZ = -f;
		rightLeg0.rotateAngleZ = rightLeg1.rotateAngleZ = rightLeg2.rotateAngleZ = f;
		leftLeg0.rotateAngleX = rightLeg0.rotateAngleX = -0.4363F;
		leftLeg2.rotateAngleX = rightLeg1.rotateAngleX = 0;
		leftLeg3.rotateAngleX = rightLeg2.rotateAngleX = 0.4363F;
		float m = 1.6f;
		float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + 0F) * m) * limbSwingAmount;
		float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + (float) Math.PI) * m) * limbSwingAmount;
		float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + ((float) Math.PI * 0.5F)) * m) * limbSwingAmount;
		float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + ((float) Math.PI * 1.5F)) * m) * limbSwingAmount;
		float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0F) * m) * limbSwingAmount;
		float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float) Math.PI) * m) * limbSwingAmount;
		float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 0.5F)) * m) * limbSwingAmount;
		float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 1.5F)) * m) * limbSwingAmount;
		leftLeg0.rotateAngleX += f3;
		leftLeg2.rotateAngleX += -f3;
		leftLeg3.rotateAngleX += f4;
		rightLeg0.rotateAngleX += -f5;
		rightLeg1.rotateAngleX += f6;
		rightLeg2.rotateAngleX += -f6;
		leftLeg0.rotateAngleZ += f7;
		leftLeg2.rotateAngleZ += -f7;
		leftLeg3.rotateAngleZ += f8;
		rightLeg0.rotateAngleZ += -f9;
		rightLeg1.rotateAngleZ += f10;
		rightLeg2.rotateAngleZ += -f10;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (isChild) {
			antlers.showModel = false;
			matrixStackIn.push();
			matrixStackIn.scale(0.5f, 0.5f, 0.5f);
			matrixStackIn.translate(0.0D, 1.5f, 0.0D);
			body.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			matrixStackIn.pop();
		} else {
			antlers.showModel = true;
			body.render(matrixStackIn, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}