package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.weapon.DevArmCannonItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DevArmCannonModel extends DefaultedItemGeoModel<DevArmCannonItem> {

	public DevArmCannonModel() {
		super(BiomancyMod.createRL("weapon/arm_cannon"));
	}

}
