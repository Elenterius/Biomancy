package com.github.elenterius.biomancy.client.render.block.mawhopper;

import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MawHopperRenderer extends CustomGeoBlockRenderer<MawHopperBlockEntity> {
	public MawHopperRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new MawHopperModel());
	}

	@Override
	protected void rotateBlock(MawHopperBlockEntity blockEntity, PoseStack poseStack) {
		poseStack.translate(0, 0.5, 0);
		poseStack.mulPose(MawHopperBlock.getConnection(blockEntity.getBlockState()).getUnsafeQuaternion());
		poseStack.translate(0, -0.5, 0);
	}

}
