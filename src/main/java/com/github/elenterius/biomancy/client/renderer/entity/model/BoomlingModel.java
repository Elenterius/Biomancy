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
public class BoomlingModel<T extends Entity> extends SegmentedModel<T> {
	private final ModelRenderer body;
	private final ModelRenderer blob;
	private final ModelRenderer head;
	private final ModelRenderer leftLeg0;
	private final ModelRenderer leftLeg2;
	private final ModelRenderer leftLeg3;
	private final ModelRenderer rightLeg0;
	private final ModelRenderer rightLeg1;
	private final ModelRenderer rightLeg2;

	public BoomlingModel() {
		texWidth = 16;
		texHeight = 16;

		body = new ModelRenderer(this);
		body.setPos(0.0F, 23.0F, 0.0F);
		body.texOffs(0, 12).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);

		blob = new ModelRenderer(this);
		blob.setPos(0.5F, 22.0F, -0.5F);
		setRotationAngle(blob, -0.0873F, 0.0F, 0.0F);
		blob.texOffs(0, 0).addBox(-2.5F, -3.8F, -1.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setPos(0.0F, -1.0F, -2.0F);
		body.addChild(head);
		head.texOffs(0, 8).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		head.texOffs(13, 0).addBox(-1.09F, -0.83F, -1.75F, 0.5F, 0.5F, 0.5F, 0.0F, true);
		head.texOffs(13, 0).addBox(0.61F, -0.83F, -1.75F, 0.5F, 0.5F, 0.5F, 0.0F, true);
		head.texOffs(14, 0).addBox(0.1625F, -0.2375F, -1.7125F, 0.625F, 0.625F, 0.625F, 0.0F, true);
		head.texOffs(14, 0).addBox(-0.775F, -0.2375F, -1.7125F, 0.625F, 0.625F, 0.625F, 0.0F, true);

		leftLeg0 = new ModelRenderer(this);
		leftLeg0.setPos(1.5F, 0.0F, -1.25F);
		body.addChild(leftLeg0);
		setRotationAngle(leftLeg0, -0.4363F, 0.0F, -0.7854F);
		leftLeg0.texOffs(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		leftLeg2 = new ModelRenderer(this);
		leftLeg2.setPos(1.5F, 0.0F, -0.25F);
		body.addChild(leftLeg2);
		setRotationAngle(leftLeg2, 0.0F, 0.0F, -0.7854F);
		leftLeg2.texOffs(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, true);

		leftLeg3 = new ModelRenderer(this);
		leftLeg3.setPos(1.5F, 0.0F, 0.75F);
		body.addChild(leftLeg3);
		setRotationAngle(leftLeg3, 0.4363F, 0.0F, -0.7854F);
		leftLeg3.texOffs(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		rightLeg0 = new ModelRenderer(this);
		rightLeg0.setPos(-1.5F, 0.0F, -1.25F);
		body.addChild(rightLeg0);
		setRotationAngle(rightLeg0, -0.4363F, 0.0F, 0.7854F);
		rightLeg0.texOffs(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		rightLeg1 = new ModelRenderer(this);
		rightLeg1.setPos(-1.5F, 0.0F, -0.25F);
		body.addChild(rightLeg1);
		setRotationAngle(rightLeg1, 0.0F, 0.0F, 0.7854F);
		rightLeg1.texOffs(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, true);

		rightLeg2 = new ModelRenderer(this);
		rightLeg2.setPos(-1.5F, 0.0F, 0.75F);
		body.addChild(rightLeg2);
		setRotationAngle(rightLeg2, 0.4363F, 0.0F, 0.7854F);
		rightLeg2.texOffs(0, 0).addBox(-0.5F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
	}

	//hacky way
	private float throb = 0f;

	@Override
	public void setupAnim(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		head.xRot = headPitch * ((float) Math.PI / 180F);

		float f = 0.7854F;
		leftLeg0.zRot = leftLeg2.zRot = leftLeg3.zRot = -f;
		rightLeg0.zRot = rightLeg1.zRot = rightLeg2.zRot = f;
		leftLeg0.xRot = rightLeg0.xRot = -0.4363F;
		leftLeg2.xRot = rightLeg1.xRot = 0;
		leftLeg3.xRot = rightLeg2.xRot = 0.4363F;
		float m = 1.6f;
		float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + 0F) * m) * limbSwingAmount;
		float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + (float) Math.PI) * m) * limbSwingAmount;
		float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + ((float) Math.PI * 0.5F)) * m) * limbSwingAmount;
		float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2F + ((float) Math.PI * 1.5F)) * m) * limbSwingAmount;
		float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0F) * m) * limbSwingAmount;
		float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float) Math.PI) * m) * limbSwingAmount;
		float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 0.5F)) * m) * limbSwingAmount;
		float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 1.5F)) * m) * limbSwingAmount;
		leftLeg0.xRot += f3;
		leftLeg2.xRot += -f3;
		leftLeg3.xRot += f4;
		rightLeg0.xRot += -f5;
		rightLeg1.xRot += f6;
		rightLeg2.xRot += -f6;
		leftLeg0.zRot += f7;
		leftLeg2.zRot += -f7;
		leftLeg3.zRot += f8;
		rightLeg0.zRot += -f9;
		rightLeg1.zRot += f10;
		rightLeg2.zRot += -f10;

		throb = MathHelper.cos(0.15f * entityIn.tickCount) * 0.05f;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, -throb, 0);
		matrixStackIn.scale(1f + throb, 1f + throb, 1f + throb);
		blob.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.popPose();

		super.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public Iterable<ModelRenderer> parts() {
		return ImmutableList.of(body);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}