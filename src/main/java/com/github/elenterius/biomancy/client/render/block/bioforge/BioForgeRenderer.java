package com.github.elenterius.biomancy.client.render.block.bioforge;

import com.github.elenterius.biomancy.block.bioforge.BioForgeBlockEntity;
import com.github.elenterius.biomancy.client.render.block.CustomGeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class BioForgeRenderer extends CustomGeoBlockRenderer<BioForgeBlockEntity> {

	public BioForgeRenderer(BlockEntityRendererProvider.Context context) {
		super(new BioForgeModel());
	}

}
