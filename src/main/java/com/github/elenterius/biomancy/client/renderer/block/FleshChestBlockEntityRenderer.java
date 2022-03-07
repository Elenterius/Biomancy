package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.FleshChestModel;
import com.github.elenterius.biomancy.world.block.entity.FleshChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class FleshChestBlockEntityRenderer extends GeoBlockRenderer<FleshChestBlockEntity> {

	public FleshChestBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new FleshChestModel());
	}

}
