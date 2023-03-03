package com.github.elenterius.biomancy.client.render.item.guidebook;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.GuideBookItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GuideBookModel extends AnimatedGeoModel<GuideBookItem> {
	@Override
	public ResourceLocation getModelResource(GuideBookItem item) {
		return BiomancyMod.createRL("geo/item/guide_book.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GuideBookItem item) {
		return BiomancyMod.createRL("textures/item/guide_book.png");
	}

	@Override
	public ResourceLocation getAnimationResource(GuideBookItem item) {
		return BiomancyMod.createRL("animations/item/guide_book.animation.json");
	}

}
