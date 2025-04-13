package com.github.elenterius.biomancy.client.render.item.impaler;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.weapon.gun.ImpalerItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ImpalerModel extends DefaultedItemGeoModel<ImpalerItem> {

	public ImpalerModel() {
		super(BiomancyMod.createRL("weapon/impaler"));
	}

}
