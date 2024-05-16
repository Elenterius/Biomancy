package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.item.weapon.gun.DevArmCannonItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DevArmCannonRenderer extends GeoItemRenderer<DevArmCannonItem> {

	public DevArmCannonRenderer() {
		super(new DevArmCannonModel());
	}

}
