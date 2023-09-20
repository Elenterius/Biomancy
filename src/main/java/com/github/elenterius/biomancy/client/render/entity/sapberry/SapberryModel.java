package com.github.elenterius.biomancy.client.render.entity.sapberry;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.SapberryProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SapberryModel extends AnimatedGeoModel<SapberryProjectile> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/entity/sapberry.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/sapberry.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/entity/sapberry.animation.json");

	@Override
	public ResourceLocation getModelResource(SapberryProjectile projectile) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(SapberryProjectile projectile) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(SapberryProjectile projectile) {
		return ANIMATION;
	}

}
