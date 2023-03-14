package com.github.elenterius.biomancy.client.render.item.longclaws;

import com.github.elenterius.biomancy.world.item.weapon.LivingLongClawsItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class LongClawsRenderer extends GeoItemRenderer<LivingLongClawsItem> {

	public LongClawsRenderer() {
		super(new LongClawsModel());
	}

}
