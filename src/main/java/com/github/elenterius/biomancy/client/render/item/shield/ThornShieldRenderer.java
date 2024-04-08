package com.github.elenterius.biomancy.client.render.item.shield;

import com.github.elenterius.biomancy.item.shield.ThornShieldItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ThornShieldRenderer extends GeoItemRenderer<ThornShieldItem> {

	public ThornShieldRenderer() {
		super(new ThornShieldModel());
	}

}
