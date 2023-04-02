package com.github.elenterius.biomancy.client.render.item.injector;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.item.InjectorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class InjectorModel extends AnimatedGeoModel<InjectorItem> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/item/injector.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/item/injector_tint.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/item/injector.animation.json");

	@Override
	public ResourceLocation getModelLocation(InjectorItem item) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(InjectorItem item) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(InjectorItem item) {
		return ANIMATION;
	}

}
