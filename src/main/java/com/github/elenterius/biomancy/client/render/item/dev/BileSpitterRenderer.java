package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.world.item.weapon.BileSpitterItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class BileSpitterRenderer extends GeoItemRenderer<BileSpitterItem> {

	public BileSpitterRenderer() {
		super(new BileSpitterModel());
	}

}
