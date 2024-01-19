package com.github.elenterius.biomancy.client.render.item.ravenousclaws;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.weapon.RavenousClawsItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RavenousClawsModel extends DefaultedItemGeoModel<RavenousClawsItem> {

	public RavenousClawsModel() {
		super(BiomancyMod.createRL("weapon/ravenous_claws"));
	}

}
