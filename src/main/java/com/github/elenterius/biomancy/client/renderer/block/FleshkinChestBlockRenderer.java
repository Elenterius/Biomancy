package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.FleshkinChestModel;
import com.github.elenterius.biomancy.world.block.entity.FleshkinChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FleshkinChestBlockRenderer extends CustomGeoBlockRenderer<FleshkinChestBlockEntity> {

	public FleshkinChestBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new FleshkinChestModel());
	}

}
