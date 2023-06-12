package com.github.elenterius.biomancy.client.render.item.ravenousclaws;

import com.github.elenterius.biomancy.item.weapon.RavenousClawsItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RavenousClawsRenderer extends GeoItemRenderer<RavenousClawsItem> {

	public RavenousClawsRenderer() {
		super(new RavenousClawsModel());
	}

}
