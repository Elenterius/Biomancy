package com.github.elenterius.biomancy.client.render.item.extractor;

import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ExtractorRenderer extends GeoItemRenderer<ExtractorItem> {
	public ExtractorRenderer() {
		super(new ExtractorModel());
	}
}
