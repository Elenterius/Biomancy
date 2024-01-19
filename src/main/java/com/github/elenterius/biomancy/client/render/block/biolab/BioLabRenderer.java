package com.github.elenterius.biomancy.client.render.block.biolab;

import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class BioLabRenderer extends CustomGeoBlockRenderer<BioLabBlockEntity> {

	public BioLabRenderer(BlockEntityRendererProvider.Context context) {
		super(new BioLabModel());
	}

}
