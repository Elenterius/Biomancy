package com.github.elenterius.biomancy.client.render.item.armor;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class AcolyteArmorRenderer extends GeoArmorRenderer<AcolyteArmorItem> {

	public AcolyteArmorRenderer() {
		super(new DefaultedItemGeoModel<>(BiomancyMod.createRL("armor/acolyte_armor")));
	}

}