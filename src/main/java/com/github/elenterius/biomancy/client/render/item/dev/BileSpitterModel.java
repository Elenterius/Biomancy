package com.github.elenterius.biomancy.client.render.item.dev;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import com.github.elenterius.biomancy.item.weapon.BileSpitterItem;
import net.minecraft.resources.ResourceLocation;

public class BileSpitterModel extends GeckolibModel<BileSpitterItem> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/item/bile_spitter.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/item/weapon/bile_spitter.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/item/bile_spitter.animation.json");

	@Override
	public ResourceLocation getModelResource(BileSpitterItem item) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(BileSpitterItem item) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(BileSpitterItem item) {
		return ANIMATION;
	}

}
