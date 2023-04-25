package com.github.elenterius.biomancy.client.render.block.fleshspike;

import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class FleshSpikeRenderer extends CustomGeoBlockRenderer<FleshSpikeBlockEntity> {
	public FleshSpikeRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new FleshSpikeModel());
	}

	@Override
	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		poseStack.translate(0, 0.5, 0);
		poseStack.mulPose(facing.getRotation());
		poseStack.translate(0, -0.5, 0);
	}

}
