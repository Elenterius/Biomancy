package com.github.elenterius.biomancy.client.render.block.decomposer;

import com.github.elenterius.biomancy.block.decomposer.DecomposerBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class DecomposerRenderer extends CustomGeoBlockRenderer<DecomposerBlockEntity> {

	public DecomposerRenderer(BlockEntityRendererProvider.Context context) {
		super(new DecomposerModel());
	}

}
