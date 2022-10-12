package com.github.elenterius.biomancy.client.renderer.item;

import com.github.elenterius.biomancy.client.model.item.DevArmCannonModel;
import com.github.elenterius.biomancy.world.item.weapon.DevArmCannonItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class DevArmCannonRenderer extends GeoItemRenderer<DevArmCannonItem> {

	public DevArmCannonRenderer() {
		super(new DevArmCannonModel());
	}

}
