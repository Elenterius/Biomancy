package com.github.elenterius.biomancy.client.model.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.InjectorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class InjectorModel extends AnimatedGeoModel<InjectorItem> {

	@Override
	public ResourceLocation getModelResource(InjectorItem item) {
		return BiomancyMod.createRL("geo/item/injector.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(InjectorItem item) {
		return BiomancyMod.createRL("textures/item/injector_tint.png");
	}

	@Override
	public ResourceLocation getAnimationResource(InjectorItem item) {
		return BiomancyMod.createRL("animations/item/injector.animation.json");
	}

}
