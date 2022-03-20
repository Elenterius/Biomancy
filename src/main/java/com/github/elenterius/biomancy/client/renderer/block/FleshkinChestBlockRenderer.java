package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.FleshkinChestModel;
import com.github.elenterius.biomancy.world.block.entity.FleshkinChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class FleshkinChestBlockRenderer extends GeoBlockRenderer<FleshkinChestBlockEntity> {

	public FleshkinChestBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new FleshkinChestModel());
	}

}
