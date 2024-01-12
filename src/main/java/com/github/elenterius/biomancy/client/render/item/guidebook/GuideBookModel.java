package com.github.elenterius.biomancy.client.render.item.guidebook;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import com.github.elenterius.biomancy.item.GuideBookItem;
import net.minecraft.resources.ResourceLocation;

public class GuideBookModel extends GeckolibModel<GuideBookItem> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/item/guide_book.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/item/guide_book.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/item/guide_book.animation.json");

	@Override
	public ResourceLocation getModelResource(GuideBookItem item) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(GuideBookItem item) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(GuideBookItem item) {
		return ANIMATION;
	}

}
