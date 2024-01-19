package com.github.elenterius.biomancy.client.render.block.fleshkinchest;

import com.github.elenterius.biomancy.block.fleshkinchest.FleshkinChestBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FleshkinChestRenderer extends CustomGeoBlockRenderer<FleshkinChestBlockEntity> {

	public FleshkinChestRenderer(BlockEntityRendererProvider.Context context) {
		super(new FleshkinChestModel());
	}

}
