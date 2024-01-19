package com.github.elenterius.biomancy.client.render.item.guidebook;

import com.github.elenterius.biomancy.item.GuideBookItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GuideBookRenderer extends GeoItemRenderer<GuideBookItem> {

	public GuideBookRenderer() {
		super(new GuideBookModel());
	}

}
