package com.github.elenterius.biomancy.client.render.block.storagesac;

import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.github.elenterius.biomancy.world.block.storagesac.StorageSacBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StorageSacRenderer implements BlockEntityRenderer<StorageSacBlockEntity> {

	private final RandomSource random = RandomSource.create();

	public StorageSacRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(StorageSacBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		int seed = (int) blockEntity.getBlockPos().asLong() + blockEntity.getBlockPos().getX() - 1;
		random.setSeed(seed);

		List<ItemStackCounter.CountedItem> items = blockEntity.getItemsForRendering();
		int size = items.size();

		for (int i = 0; i < size; i++) {
			ItemStackCounter.CountedItem countedItem = items.get(i);
			ItemStack stack = countedItem.stack();
			if (!stack.isEmpty()) {
				poseStack.pushPose();
				float x = 0.5f + (random.nextFloat() * 2f - 1) * 0.15f;
				float z = 0.5f + (random.nextFloat() * 2f - 1) * 0.15f;
				poseStack.translate(x, 0.15f + ((float) (i + 1) / size) * 0.5f, z);

				int amount = stack.getCount();
				for (int j = 0; j < amount; j++) {
					poseStack.pushPose();
					if (j > 0) {
						float x2 = (random.nextFloat() * 2f - 1) * 0.15f;
						float y2 = (random.nextFloat() * 2f - 1) * 0.35f;
						float z2 = (random.nextFloat() * 2f - 1) * 0.15f;
						poseStack.translate(x2 * 0.5f, y2 * 0.5f, z2 * 0.5f);
					}
					poseStack.scale(0.375f, 0.375f, 0.375f);
					Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, poseStack, bufferSource, 0);
					poseStack.popPose();
				}
				poseStack.popPose();
			}
		}
	}

}
