package com.github.elenterius.biomancy.client.render.item;

import com.github.elenterius.biomancy.world.item.BEWLBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.core.IAnimatable;

public class BEWLItemRenderer extends BlockEntityWithoutLevelRenderer {

	public static final BEWLItemRenderer INSTANCE = new BEWLItemRenderer();
	private static final Camera itemCamera = new Camera();

	public BEWLItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		//do nothing
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (stack.getItem() instanceof BEWLBlockItem blockItem) {
			BlockEntity cachedBEWL = blockItem.getCachedBEWL();
			if (cachedBEWL == null) return;

			if (cachedBEWL instanceof IAnimatable && transformType == ItemTransforms.TransformType.GUI) {
				poseStack.pushPose();
				poseStack.translate(0f, 0.5f, 0f); //fix for display translation offset of geo block models
				renderBlockEntity(poseStack, buffer, packedLight, packedOverlay, cachedBEWL);
				poseStack.popPose();
			}
			else {
				renderBlockEntity(poseStack, buffer, packedLight, packedOverlay, cachedBEWL);
			}
		}
	}

	private void renderBlockEntity(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockEntity blockEntity) {
		BlockEntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
		Camera originalCamera = renderDispatcher.camera;
		renderDispatcher.camera = itemCamera; //hack to keep items animated, see CustomGeoBlockRenderer#shouldAnimate()
		renderDispatcher.renderItem(blockEntity, poseStack, buffer, packedLight, packedOverlay);
		renderDispatcher.camera = originalCamera;
	}

}
