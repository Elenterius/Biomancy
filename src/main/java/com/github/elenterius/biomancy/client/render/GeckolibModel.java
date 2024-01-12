package com.github.elenterius.biomancy.client.render;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class GeckolibModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

	public abstract ResourceLocation getModelResource(T animatable);

	public abstract ResourceLocation getTextureResource(T animatable);

	public abstract ResourceLocation getAnimationResource(T animatable);

	@Override
	public ResourceLocation getModelLocation(T t) {
		return getModelResource(t);
	}

	@Override
	public ResourceLocation getTextureLocation(T t) {
		return getTextureResource(t);
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T t) {
		return getAnimationResource(t);
	}

}
