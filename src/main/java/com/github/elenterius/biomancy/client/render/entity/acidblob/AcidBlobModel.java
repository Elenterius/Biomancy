package com.github.elenterius.biomancy.client.render.entity.acidblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.AcidBlobProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AcidBlobModel extends AnimatedGeoModel<AcidBlobProjectile> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/entity/acid_blob.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/acid_blob.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/entity/acid_blob.animation.json");

	@Override
	public ResourceLocation getModelResource(AcidBlobProjectile projectile) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(AcidBlobProjectile projectile) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(AcidBlobProjectile projectile) {
		return ANIMATION;
	}

}
