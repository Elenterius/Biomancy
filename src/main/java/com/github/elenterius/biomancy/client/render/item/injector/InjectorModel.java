package com.github.elenterius.biomancy.client.render.item.injector;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.InjectorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class InjectorModel extends AnimatedGeoModel<InjectorItem> {

	@Override
	public ResourceLocation getModelLocation(InjectorItem item) {
		return BiomancyMod.createRL("geo/item/injector.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(InjectorItem item) {
		return BiomancyMod.createRL("textures/item/injector_tint.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(InjectorItem item) {
		return BiomancyMod.createRL("animations/item/injector.animation.json");
	}

}
