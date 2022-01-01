package com.github.elenterius.biomancy.client.renderer.item;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

//extends GeoItemRenderer<CreatorBlockItem>
public class CreatorBlockItemRenderer extends BlockEntityWithoutLevelRenderer {

	private final Lazy<CreatorBlockEntity> blockEntity = Lazy.of(() -> new CreatorBlockEntity(BlockPos.ZERO, ModBlocks.CREATOR.get().defaultBlockState()));

	public CreatorBlockItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity.get(), poseStack, buffer, packedLight, packedOverlay);
	}

}
