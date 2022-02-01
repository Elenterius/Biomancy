package com.github.elenterius.biomancy.client.model.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FleshBlobModel<T extends FleshBlob> extends AnimatedGeoModel<T> {

	protected static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
			BiomancyMod.createRL("textures/entity/fleshkin_blob_0.png"),
			BiomancyMod.createRL("textures/entity/fleshkin_blob_1.png"),
	};

	@Override
	public ResourceLocation getModelLocation(FleshBlob fleshBlob) {
		return BiomancyMod.createRL("geo/entity/fleshkin_blob.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(FleshBlob fleshBlob) {
		return TEXTURES[fleshBlob.getBlobType()];
	}

	@Override
	public ResourceLocation getAnimationFileLocation(FleshBlob fleshBlob) {
		return BiomancyMod.createRL("animations/entity/fleshkin_blob.animation.json");
	}

}