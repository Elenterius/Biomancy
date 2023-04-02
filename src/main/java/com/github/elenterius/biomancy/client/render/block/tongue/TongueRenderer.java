package com.github.elenterius.biomancy.client.render.block.tongue;

import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.github.elenterius.biomancy.world.block.tongue.TongueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;

public class TongueRenderer extends CustomGeoBlockRenderer<TongueBlockEntity> {

	private final RandomSource random = RandomSource.create();

	private ItemStack heldItemStack = ItemStack.EMPTY;
	private MultiBufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
	private ResourceLocation texture;

	public TongueRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new TongueModel());
	}

	@Override
	public void renderEarly(TongueBlockEntity tongue, PoseStack poseStack, float ticks, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		heldItemStack = tongue.getHeldItem();
		multiBufferSource = bufferSource != null ? bufferSource : Minecraft.getInstance().renderBuffers().bufferSource();
		texture = getTextureLocation(tongue);
		super.renderEarly(tongue, poseStack, ticks, bufferSource, vertexBuilder, packedLight, packedOverlay, red, green, blue, partialTicks);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("item") && !heldItemStack.isEmpty()) {
			int itemCount = Math.min(heldItemStack.getCount(), 3);
			int seed = Item.getId(heldItemStack.getItem()) + heldItemStack.getDamageValue();
			renderItems(poseStack, packedLight, packedOverlay, itemCount, seed);
			buffer = multiBufferSource.getBuffer(RenderType.entityCutout(texture));
		}
		super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	private void renderItems(PoseStack poseStack, int packedLight, int packedOverlay, int itemCount, int seed) {
		random.setSeed(seed);

		poseStack.pushPose();

		poseStack.translate(0, 0.385, 0);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
		poseStack.scale(0.75f, 0.75f, 0.75f);

		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
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
			itemRenderer.renderStatic(heldItemStack, ItemTransforms.TransformType.GROUND, packedLight, packedOverlay, poseStack, multiBufferSource, 0);
			poseStack.popPose();
			if (!isGUI3d) poseStack.translate(0, 0, 0.025F);
		}

		poseStack.popPose();
	}

}
