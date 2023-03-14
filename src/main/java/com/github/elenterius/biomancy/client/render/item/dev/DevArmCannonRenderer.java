package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.world.item.weapon.DevArmCannonItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class DevArmCannonRenderer extends GeoItemRenderer<DevArmCannonItem> {

	public DevArmCannonRenderer() {
		super(new DevArmCannonModel());
	}

}
