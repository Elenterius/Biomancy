package com.github.elenterius.biomancy.client.renderer.block;

import com.github.elenterius.biomancy.client.model.block.CreatorModel;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class CreatorBlockEntityRenderer extends GeoBlockRenderer<CreatorBlockEntity> {

	public CreatorBlockEntityRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
		super(rendererDispatcher, new CreatorModel());
	}

}
