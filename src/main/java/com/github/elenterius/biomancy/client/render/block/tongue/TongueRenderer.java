package com.github.elenterius.biomancy.client.render.block.tongue;

import com.github.elenterius.biomancy.block.tongue.TongueBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

public class TongueRenderer extends CustomGeoBlockRenderer<TongueBlockEntity> {

	private final RandomSource random = RandomSource.create();
	private final ItemRenderer itemRenderer;

	private ItemStack heldItemStack = ItemStack.EMPTY;

	public TongueRenderer(BlockEntityRendererProvider.Context context) {
		super(new TongueModel());
		itemRenderer = context.getItemRenderer();
	}

	@Override
	public void preRender(PoseStack poseStack, TongueBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		heldItemStack = animatable.getHeldItem();
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(PoseStack poseStack, TongueBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("_item") && !heldItemStack.isEmpty()) {
			int itemCount = Math.min(heldItemStack.getCount(), 3);
			int seed = Item.getId(heldItemStack.getItem()) + heldItemStack.getDamageValue();
			renderItems(poseStack, bufferSource, packedLight, packedOverlay, itemCount, seed);
		}
		super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}


	private void renderItems(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, int itemCount, int seed) {
		random.setSeed(seed);

		poseStack.pushPose();

		poseStack.translate(0, 0.385, 0);
		poseStack.mulPose(Axis.XP.rotationDegrees(90));
		poseStack.mulPose(Axis.YP.rotationDegrees(180));
		poseStack.scale(0.75f, 0.75f, 0.75f);

		BakedModel bakedmodel = itemRenderer.getModel(heldItemStack, null, null, seed);
		boolean isGUI3d = bakedmodel.isGui3d();
		if (isGUI3d) poseStack.translate(0, 0, 0.1);

		for (int i = 0; i < itemCount; i++) {
			poseStack.pushPose();
			if (i > 0) {
				float x = (random.nextFloat() * 2f - 1) * 0.15f;
				float y = (random.nextFloat() * 2f - 1) * 0.15f;
				if (isGUI3d) {
					poseStack.translate(x, y, (random.nextFloat() * 2f - 1) * 0.15f);
				}
				else {
					poseStack.translate(x * 0.5f, y * 0.5f, 0);
				}
			}
			itemRenderer.renderStatic(heldItemStack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, null, 0);
			poseStack.popPose();
			if (!isGUI3d) poseStack.translate(0, 0, 0.025F);
		}

		poseStack.popPose();
	}

}
