package com.github.elenterius.biomancy.client.render.item.guidebook;

import com.github.elenterius.biomancy.world.item.GuideBookItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class GuideBookRenderer extends GeoItemRenderer<GuideBookItem> {

	public GuideBookRenderer() {
		super(new GuideBookModel());
	}

}
