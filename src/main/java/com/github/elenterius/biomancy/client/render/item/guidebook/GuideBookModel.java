package com.github.elenterius.biomancy.client.render.item.guidebook;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.GuideBookItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class GuideBookModel extends DefaultedItemGeoModel<GuideBookItem> {

	public GuideBookModel() {
		super(BiomancyMod.createRL("guide_book"));
	}

	@Override
	public RenderType getRenderType(GuideBookItem animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
