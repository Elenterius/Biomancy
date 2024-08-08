package com.github.elenterius.biomancy.client.render.entity.mob.sheep;

import com.github.elenterius.biomancy.entity.mob.ChromaSheep;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class ChromaSheepFurLayer extends RenderLayer<ChromaSheep, SheepModel<ChromaSheep>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");

	private final SheepFurModel<ChromaSheep> model;

	public ChromaSheepFurLayer(RenderLayerParent<ChromaSheep, SheepModel<ChromaSheep>> renderer, EntityModelSet modelSet) {
		super(renderer);
		model = new SheepFurModel<>(modelSet.bakeLayer(ModelLayers.SHEEP_FUR));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ChromaSheep livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (livingEntity.isSheared()) return;
		if (livingEntity.isInvisible()) return;

		getParentModel().copyPropertiesTo(model);
		model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
		model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		VertexConsumer vertexconsumer = buffer.getBuffer(getRenderType(TEXTURE));
		model.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(livingEntity, 0f), 1f, 1f, 1f, 1f);
	}

	protected RenderType getRenderType(ResourceLocation texture) {
		return ModRenderTypes.getCutoutPartyTime(texture);
	}

}