package com.github.elenterius.biomancy.client.render.entity.bloomberry;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.BloomberryProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BloomberryModel extends AnimatedGeoModel<BloomberryProjectile> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/entity/bloomberry.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/bloomberry.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/entity/bloomberry.animation.json");

	@Override
	public ResourceLocation getModelResource(BloomberryProjectile projectile) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(BloomberryProjectile projectile) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(BloomberryProjectile projectile) {
		return ANIMATION;
	}

}
