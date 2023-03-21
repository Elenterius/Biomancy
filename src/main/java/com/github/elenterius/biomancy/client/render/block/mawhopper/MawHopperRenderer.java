package com.github.elenterius.biomancy.client.render.block.mawhopper;

import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.github.elenterius.biomancy.world.block.mawhopper.MawHopperBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class MawHopperRenderer extends CustomGeoBlockRenderer<MawHopperBlockEntity> {
	public MawHopperRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new MawHopperModel());
	}

	@Override
	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		poseStack.translate(0, 0.5, 0);
		poseStack.mulPose(facing.getRotation());
		poseStack.translate(0, -0.5, 0);
	}

}
