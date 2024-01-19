package com.github.elenterius.biomancy.client.render.block.cradle;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PrimordialCradleModel extends DefaultedBlockGeoModel<PrimordialCradleBlockEntity> {

	public PrimordialCradleModel() {
		super(BiomancyMod.createRL("primordial_cradle"));
	}

}
