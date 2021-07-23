package com.github.elenterius.biomancy.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OculusObserverModel extends EntityModel<Entity> {

	public final ModelRenderer body;
	public final ModelRenderer eastWing;
	public final ModelRenderer westWing;
	private final ModelRenderer tail;

	public OculusObserverModel() {
		textureWidth = 32;
		textureHeight = 32;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 20.0F, 0.0F);
		body.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 3.5F, 4.0F);
		body.addChild(tail);
		setRotationAngle(tail, -0.1745F, 0.0F, 0.0F);
		tail.setTextureOffset(0, 12).addBox(0.0F, -3.5F, -0.25F, 0.0F, 4.0F, 4.0F, 0.0F, false);

		eastWing = new ModelRenderer(this);
		eastWing.setRotationPoint(-4.0F, 0.0F, 0.0F);
		body.addChild(eastWing);
		eastWing.setTextureOffset(0, 23).addBox(-11.0F, -7.0F, 0.0F, 11.0F, 9.0F, 0.0F, 0.0F, true);

		westWing = new ModelRenderer(this);
		westWing.setRotationPoint(4.0F, 0.0F, 0.0F);
		body.addChild(westWing);
		westWing.setTextureOffset(0, 23).addBox(0.0F, -7.0F, 0.0F, 11.0F, 9.0F, 0.0F, 0.0F, false);
	}

	public void renderOnPlayer(State state, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, int ticksExisted) {
		if (state == State.ON_SHOULDER) {
			body.rotateAngleX = -10f / 180f * (float) Math.PI;
			eastWing.rotateAngleY = 78f / 180f * (float) Math.PI;
			westWing.rotateAngleY = -eastWing.rotateAngleY;
		}
		else if (state == State.HOVERING) {
			eastWing.rotateAngleY = MathHelper.cos(ticksExisted * 2.1f) * (float) Math.PI * 0.15f;
			westWing.rotateAngleY = -eastWing.rotateAngleY;
		}
		body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		boolean isNotMoving = entity.isOnGround() && entity.getMotion().lengthSquared() < 1.0E-07;
		if (isNotMoving) {
			westWing.rotateAngleY = eastWing.rotateAngleY = 0;
		}
		else {
			eastWing.rotateAngleY = MathHelper.cos(ageInTicks * 2.1f) * (float) Math.PI * 0.15f;
			westWing.rotateAngleY = -eastWing.rotateAngleY;
		}
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public enum State {
		ON_SHOULDER,
		HOVERING
	}

}