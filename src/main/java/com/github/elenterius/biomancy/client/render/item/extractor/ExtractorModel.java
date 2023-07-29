package com.github.elenterius.biomancy.client.render.item.extractor;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ExtractorModel extends DefaultedItemGeoModel<ExtractorItem> {

	public ExtractorModel() {
		super(BiomancyMod.createRL("extractor"));
	}

}
