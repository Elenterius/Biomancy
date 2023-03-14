package com.github.elenterius.biomancy.client.render.block.fleshkinchest;

import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FleshkinChestRenderer extends CustomGeoBlockRenderer<FleshkinChestBlockEntity> {

	public FleshkinChestRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new FleshkinChestModel());
	}

}
