package com.github.elenterius.biomancy.client.renderer.item;

import com.github.elenterius.biomancy.client.model.item.ArmCannonModel;
import com.github.elenterius.biomancy.world.item.weapon.ArmCannonItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class ArmCannonRenderer extends GeoItemRenderer<ArmCannonItem> {

	public ArmCannonRenderer() {
		super(new ArmCannonModel());
	}

}
