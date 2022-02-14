package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.model.entity.FleshkinModel;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.world.entity.ownable.Fleshkin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FleshkinRenderer extends HumanoidMobRenderer<Fleshkin, FleshkinModel<Fleshkin>> {

	private static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/fleshkin.png");

	public FleshkinRenderer(EntityRendererProvider.Context context) {
		this(context, FleshkinModel.MODEL_LAYER, FleshkinModel.INNER_ARMOR_LAYER, FleshkinModel.OUTER_ARMOR_LAYER);
	}

	public FleshkinRenderer(EntityRendererProvider.Context context, ModelLayerLocation main, ModelLayerLocation innerArmor, ModelLayerLocation outerArmor) {
		this(context, new FleshkinModel<>(context.bakeLayer(main)), new FleshkinModel<>(context.bakeLayer(innerArmor)), new FleshkinModel<>(context.bakeLayer(outerArmor)));
	}

	protected FleshkinRenderer(EntityRendererProvider.Context context, FleshkinModel<Fleshkin> main, FleshkinModel<Fleshkin> innerArmor, FleshkinModel<Fleshkin> outerArmor) {
		super(context, main, 0.5f);
		addLayer(new HumanoidArmorLayer<>(this, innerArmor, outerArmor));
		addLayer(new FleshkinHeadLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(Fleshkin entity) {
		return TEXTURE;
	}

	public static class FleshkinHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

		private static final ItemStack BLOCK_TO_RENDER = new ItemStack(ModBlocks.TONGUE.get());

		public FleshkinHeadLayer(RenderLayerParent<T, M> renderer) {
			super(renderer);
		}

		@Override
		public void render(PoseStack matrixStackIn, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			matrixStackIn.pushPose();
//			matrixStackIn.scale(1f, 1f, 1f);
			if (livingEntity.isBaby()) {
				matrixStackIn.translate(0d, 0.03125d, 0d);
				matrixStackIn.scale(0.7f, 0.7f, 0.7f);
				matrixStackIn.translate(0d, 1d, 0d);
			}
			getParentModel().getHead().translateAndRotate(matrixStackIn);
			matrixStackIn.translate(0d, -0.25d, 0d);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180f));
			matrixStackIn.scale(0.625f, -0.625f, -0.625f);
			Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, BLOCK_TO_RENDER, ItemTransforms.TransformType.HEAD, false, matrixStackIn, buffer, packedLight);
			matrixStackIn.popPose();
		}

	}

}
