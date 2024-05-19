package com.github.elenterius.biomancy.client.render.item.caustic_gunblade;

import com.github.elenterius.biomancy.item.weapon.gun.CausticGunbladeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CausticGunbladeRenderer extends GeoItemRenderer<CausticGunbladeItem> {

	public CausticGunbladeRenderer() {
		super(new CausticGunbladeModel());
	}

}
