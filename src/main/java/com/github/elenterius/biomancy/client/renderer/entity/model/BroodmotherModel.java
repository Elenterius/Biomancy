package com.github.elenterius.biomancy.client.renderer.entity.model;
//Made by Elenterius

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BroodmotherModel<T extends Entity> extends SegmentedModel<T> {
	private final ModelRenderer abdomen;
	private final ModelRenderer body;
	private final ModelRenderer leftLeg0;
	private final ModelRenderer head;
	private final ModelRenderer bottomJaw;
	private final ModelRenderer topJaw;
	private final ModelRenderer leftLeg1;
	private final ModelRenderer leftLeg2;
	private final ModelRenderer leftLeg3;
	private final ModelRenderer rightLeg0;
	private final ModelRenderer rightLeg1;
	private final ModelRenderer rightLeg2;
	private final ModelRenderer rightLeg3;

	public BroodmotherModel() {
		texWidth = 64;
		texHeight = 64;

		abdomen = new ModelRenderer(this);
		abdomen.setPos(0.0F, 19.0F, 3.0F);
		setRotationAngle(abdomen, 0.3491F, 0.0F, 0.0F);
		abdomen.texOffs(0, 14).addBox(-5.0F, -4.0F, 0.0F, 10.0F, 9.0F, 12.0F, 0.0F, true);
		abdomen.texOffs(8, 35).addBox(-3.5F, -3.0F, 12.0F, 7.0F, 7.0F, 2.0F, 0.0F, true);

		body = new ModelRenderer(this);
		body.setPos(0.0F, 20.0F, 0.0F);
		setRotationAngle(body, 0.1396F, 0.0F, 0.0F);
		body.texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 8.0F, 0.0F, true);

		leftLeg0 = new ModelRenderer(this);
		leftLeg0.setPos(4.0F, 20.0F, -1.0F);
		setRotationAngle(leftLeg0, 0.0F, 0.7854F, 0.4363F);
		leftLeg0.texOffs(21, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);

		head = new ModelRenderer(this);
		head.setPos(0.0F, 20.0F, -3.0F);
		setRotationAngle(head, 0.0698F, 0.0F, 0.0F);
		head.texOffs(32, 4).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 6.0F, 6.0F, 0.0F, true);
		head.texOffs(1, 37).addBox(-3.0F, -2.75F, -4.2F, 1.0F, 1.0F, 1.0F, 0.0F, true);
		head.texOffs(1, 37).addBox(2.0F, -2.75F, -4.2F, 1.0F, 1.0F, 1.0F, 0.0F, true);
		head.texOffs(1, 37).addBox(-3.0F, -1.7F, -2.6F, 1.0F, 1.0F, 1.0F, 0.0F, true);
		head.texOffs(1, 37).addBox(2.0F, -1.7F, -2.6F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(1, 37).addBox(-3.0F, -3.35F, -2.65F, 1.0F, 1.0F, 1.0F, 0.0F, true);
		head.texOffs(1, 37).addBox(2.0F, -3.35F, -2.65F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		bottomJaw = new ModelRenderer(this);
		bottomJaw.setPos(0.0F, 0.5F, -5.0F);
		head.addChild(bottomJaw);
		bottomJaw.texOffs(42, 43).addBox(-1.5F, -0.7989F, -6.964F, 3.0F, 2.0F, 8.0F, 0.0F, false);

		topJaw = new ModelRenderer(this);
		topJaw.setPos(0.0F, -2.5F, -5.0F);
		head.addChild(topJaw);
		topJaw.texOffs(32, 20).addBox(-2.0F, -0.55F, -4.0F, 4.0F, 2.0F, 4.0F, 0.0F, true);
		topJaw.texOffs(48, 21).addBox(-1.5F, -0.55F, -7.0F, 3.0F, 2.0F, 3.0F, 0.0F, true);
		topJaw.texOffs(46, 35).addBox(-1.0F, -1.05F, -7.0F, 2.0F, 1.0F, 7.0F, 0.0F, true);
		topJaw.texOffs(46, 35).addBox(-1.0F, -1.55F, -8.0F, 2.0F, 4.0F, 1.0F, 0.0F, true);

		leftLeg1 = new ModelRenderer(this);
		leftLeg1.setPos(4.0F, 20.0F, 0.0F);
		setRotationAngle(leftLeg1, 0.0F, 0.2618F, 0.4363F);
		leftLeg1.texOffs(21, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);

		leftLeg2 = new ModelRenderer(this);
		leftLeg2.setPos(4.0F, 20.0F, 1.0F);
		setRotationAngle(leftLeg2, 0.0F, -0.0873F, 0.2618F);
		leftLeg2.texOffs(21, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);

		leftLeg3 = new ModelRenderer(this);
		leftLeg3.setPos(4.0F, 20.0F, 2.0F);
		setRotationAngle(leftLeg3, 0.0F, -0.4363F, 0.2618F);
		leftLeg3.texOffs(21, 0).addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, true);

		rightLeg0 = new ModelRenderer(this);
		rightLeg0.setPos(-4.0F, 20.0F, -1.0F);
		setRotationAngle(rightLeg0, 0.0F, -0.8727F, -0.3491F);
		rightLeg0.texOffs(21, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);

		rightLeg1 = new ModelRenderer(this);
		rightLeg1.setPos(-4.0F, 20.0F, 0.0F);
		setRotationAngle(rightLeg1, 0.0F, -0.3491F, -0.3491F);
		rightLeg1.texOffs(21, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);

		rightLeg2 = new ModelRenderer(this);
		rightLeg2.setPos(-4.0F, 20.0F, 1.0F);
		setRotationAngle(rightLeg2, 0.0F, 0.0F, -0.3491F);
		rightLeg2.texOffs(21, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);

		rightLeg3 = new ModelRenderer(this);
		rightLeg3.setPos(-4.0F, 20.0F, 2.0F);
		setRotationAngle(rightLeg3, 0.0F, 0.5236F, -0.4363F);
		rightLeg3.texOffs(21, 0).addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F, false);
	}

	//hacky way
	private float throb = 0f;

	@Override
	public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		head.xRot = headPitch * ((float) Math.PI / 180F);

		float mouthAngle = (MathHelper.sin(0.1501f * entityIn.tickCount) * 0.5f + 0.5f) * (3.5f + MathHelper.abs(headPitch) * 0.8f) * ((float) Math.PI / 180F);
		topJaw.xRot = -mouthAngle;
		bottomJaw.xRot = mouthAngle;

		float f = (float) Math.PI / 9F;
		leftLeg3.zRot = leftLeg0.zRot = f;
		rightLeg3.zRot = rightLeg0.zRot = -f;
		leftLeg2.zRot = leftLeg1.zRot = f * 0.75F;
		rightLeg2.zRot = rightLeg1.zRot = -f * 0.75F;
		float f1 = (float) Math.PI / 16F;
		leftLeg3.yRot = -f * 1.75F;
		rightLeg3.yRot = f * 1.75F;
		leftLeg2.yRot = -f1;
		rightLeg2.yRot = f1;
		leftLeg1.yRot = f1;
		rightLeg1.yRot = -f1;
		leftLeg0.yRot = f * 1.75F;
		rightLeg0.yRot = -f * 1.75F;
		float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
		float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
		float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
		float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
		float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
		float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
		float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
		leftLeg3.yRot += f3;
		rightLeg3.yRot += -f3;
		leftLeg2.yRot += f4;
		rightLeg2.yRot += -f4;
		leftLeg1.yRot += f5;
		rightLeg1.yRot += -f5;
		leftLeg0.yRot += f6;
		rightLeg0.yRot += -f6;
		leftLeg3.zRot += f7;
		rightLeg3.zRot += -f7;
		leftLeg2.zRot += f8;
		rightLeg2.zRot += -f8;
		leftLeg1.zRot += f9;
		rightLeg1.zRot += -f9;
		leftLeg0.zRot += f10;
		rightLeg0.zRot += -f10;

		throb = MathHelper.cos(0.15f * entityIn.tickCount) * 0.05f;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		matrixStack.pushPose();
		matrixStack.translate(0, -throb, 0);
		matrixStack.scale(1f + throb, 1f + throb, 1f + throb);
		abdomen.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		matrixStack.popPose();

		super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public Iterable<ModelRenderer> parts() {
		return ImmutableList.of(body, head, leftLeg0, leftLeg1, leftLeg2, leftLeg3, rightLeg0, rightLeg1, rightLeg2, rightLeg3);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}