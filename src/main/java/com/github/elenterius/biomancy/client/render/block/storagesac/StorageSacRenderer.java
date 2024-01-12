package com.github.elenterius.biomancy.client.render.block.storagesac;

import com.github.elenterius.biomancy.block.storagesac.StorageSacBlock;
import com.github.elenterius.biomancy.block.storagesac.StorageSacBlockEntity;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

public class StorageSacRenderer implements BlockEntityRenderer<StorageSacBlockEntity> {

	private final Random random = new Random();
	private final ItemRenderer itemRenderer;

	public StorageSacRenderer(BlockEntityRendererProvider.Context context) {
		itemRenderer = Minecraft.getInstance().getItemRenderer();
	}

	private float randomOffset(float amount) {
		return (random.nextFloat() * 2f - 1) * amount;
	}

	@Override
	public void render(StorageSacBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		int seed = (int) blockEntity.getBlockPos().asLong() + blockEntity.getBlockPos().getX() - 1;
		random.setSeed(seed);

		ItemTranslator itemTranslator = ItemTranslator.from(StorageSacBlock.getFacing(blockEntity.getBlockState()));
		renderItemsOnAxis(poseStack, bufferSource, packedLight, packedOverlay, blockEntity.getItemsForRendering(), itemTranslator, 0.375f);
	}

	private void renderItemsOnAxis(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, List<ItemStackCounter.CountedItem> items, ItemTranslator itemTranslator, float itemScale) {
		int numberOfItems = items.size();

		for (int i = 0; i < numberOfItems; i++) {
			ItemStack stack = items.get(i).stack();
			if (stack.isEmpty()) continue;

			poseStack.pushPose();

			float x = 0.5f + randomOffset(0.175f);
			float y = 0.25f + ((i + 1f) / numberOfItems) * 0.5f;
			float z = 0.5f + randomOffset(0.175f);
			itemTranslator.translate(poseStack, x, y, z);

			int amount = stack.getCount();
			for (int j = 0; j < amount; j++) {
				poseStack.pushPose();
				if (j > 0) {
					float x2 = randomOffset(0.075f);
					float y2 = randomOffset(0.1f);
					float z2 = randomOffset(0.075f);
					itemTranslator.translate(poseStack, x2, y2, z2);
				}
				poseStack.scale(itemScale, itemScale, itemScale);
				itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, poseStack, bufferSource, 0);
				poseStack.popPose();
			}

			poseStack.popPose();
		}
	}


	enum ItemTranslator {
		X_AXIS((poseStack, x, y, z) -> poseStack.translate(y, x, z)),
		Y_AXIS(PoseStack::translate),
		Z_AXIS((poseStack, x, y, z) -> poseStack.translate(x, z, y));

		private final TranslationFunc translationFunc;

		ItemTranslator(TranslationFunc translationFunc) {
			this.translationFunc = translationFunc;
		}

		static ItemTranslator from(Direction facing) {
			Direction.Axis axis = facing.getAxis();
			if (axis == Direction.Axis.X) return X_AXIS;
			else if (axis == Direction.Axis.Y) return Y_AXIS;
			return Z_AXIS;
		}

		void translate(PoseStack poseStack, float x, float y, float z) {
			translationFunc.translate(poseStack, x, y, z);
		}

		interface TranslationFunc {
			void translate(PoseStack poseStack, float x, float y, float z);
		}

	}

}
