package com.github.elenterius.biomancy.client.render.item.injector;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import net.minecraft.resources.ResourceLocation;

public class InjectorModel extends GeckolibModel<InjectorItem> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/item/injector.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/item/injector.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/item/injector.animation.json");

	@Override
	public ResourceLocation getModelResource(InjectorItem item) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(InjectorItem item) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(InjectorItem item) {
		return ANIMATION;
	}

}
