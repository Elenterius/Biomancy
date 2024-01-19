package com.github.elenterius.biomancy.client.render.block.tongue;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.tongue.TongueBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class TongueModel extends DefaultedBlockGeoModel<TongueBlockEntity> {

	public TongueModel() {
		super(BiomancyMod.createRL("tongue"));
	}

	@Override
	public boolean crashIfBoneMissing() {
		return true;
	}

}
