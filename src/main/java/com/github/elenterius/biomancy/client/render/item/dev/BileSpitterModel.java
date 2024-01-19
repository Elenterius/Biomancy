package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.weapon.BileSpitterItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class BileSpitterModel extends DefaultedItemGeoModel<BileSpitterItem> {

	public BileSpitterModel() {
		super(BiomancyMod.createRL("weapon/bile_spitter"));
	}

}
