package com.github.elenterius.biomancy.client.render.block.mawhopper;

import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class MawHopperRenderer extends CustomGeoBlockRenderer<MawHopperBlockEntity> {

	public MawHopperRenderer(BlockEntityRendererProvider.Context context) {
		super(new MawHopperModel());
	}

	@Override
	protected Direction getFacing(MawHopperBlockEntity block) {
		return Direction.NORTH;
	}

	@Override
	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		poseStack.translate(0, 0.5, 0);
		poseStack.mulPose(MawHopperBlock.getConnection(animatable.getBlockState()).getUnsafeQuaternion());
		poseStack.translate(0, -0.5, 0);
	}

}
