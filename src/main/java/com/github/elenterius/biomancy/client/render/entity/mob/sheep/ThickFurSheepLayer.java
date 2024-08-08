package com.github.elenterius.biomancy.client.render.entity.mob.sheep;

import com.github.elenterius.biomancy.entity.mob.ThickFurSheep;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

public class ThickFurSheepLayer<T extends ThickFurSheep> extends RenderLayer<T, SheepModel<T>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");

	private final InflatedSheepFurModel<T> model;

	public ThickFurSheepLayer(RenderLayerParent<T, SheepModel<T>> renderer, EntityModelSet modelSet) {
		super(renderer);
		model = new InflatedSheepFurModel<>(modelSet.bakeLayer(ModelLayers.SHEEP_FUR));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T sheep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (sheep.isSheared() || sheep.isInvisible()) return;

		float[] rgb = Sheep.getColorArray(sheep.getColor());
		float r = rgb[0];
		float g = rgb[1];
		float b = rgb[2];

		model.setBodyVisible(false);
		coloredCutoutModelCopyLayerRender(getParentModel(), model, TEXTURE, poseStack, buffer, packedLight, sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, r, g, b);

		poseStack.pushPose();
		float scale = 1f + (sheep.getWoolSize() - 1f) / (float) (ThickFurSheep.MAX_WOOL_SIZE - 1);
		poseStack.scale(scale, scale, 0.5f + scale * 0.5f);
		poseStack.translate(0d, -(scale / 10d), scale * (scale * 0.6d) / 10d);
		model.setBodyVisible(true);
		coloredCutoutModelCopyLayerRender(getParentModel(), model, TEXTURE, poseStack, buffer, packedLight, sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, r, g, b);
		poseStack.popPose();
	}

	static class InflatedSheepFurModel<T extends Sheep> extends SheepFurModel<T> {

		public InflatedSheepFurModel(ModelPart root) {
			super(root);
		}

		void setBodyVisible(boolean flag) {
			body.visible = flag;
			head.visible = !flag;
			leftFrontLeg.visible = !flag;
			rightFrontLeg.visible = !flag;
			leftHindLeg.visible = !flag;
			rightHindLeg.visible = !flag;
		}
	}

}