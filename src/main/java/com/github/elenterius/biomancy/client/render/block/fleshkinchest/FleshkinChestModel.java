package com.github.elenterius.biomancy.client.render.block.fleshkinchest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.fleshkinchest.FleshkinChestBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class FleshkinChestModel extends DefaultedBlockGeoModel<FleshkinChestBlockEntity> {

	public FleshkinChestModel() {
		super(BiomancyMod.createRL("fleshkin_chest"));
	}

}
