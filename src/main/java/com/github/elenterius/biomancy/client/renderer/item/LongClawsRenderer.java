package com.github.elenterius.biomancy.client.renderer.item;

import com.github.elenterius.biomancy.client.model.item.LongClawsModel;
import com.github.elenterius.biomancy.world.item.weapon.LongClawsItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class LongClawsRenderer extends GeoItemRenderer<LongClawsItem> {

	public LongClawsRenderer() {
		super(new LongClawsModel());
	}

}
