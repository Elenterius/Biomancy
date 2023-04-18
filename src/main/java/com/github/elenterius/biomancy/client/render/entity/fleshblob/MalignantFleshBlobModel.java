package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.fleshblob.MalignantFleshBlob;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MalignantFleshBlobModel<T extends MalignantFleshBlob> extends AnimatedGeoModel<T> {

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/malignant_flesh_blob.png");
	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/entity/malignant_flesh_blob.geo.json");

	@Override
	public ResourceLocation getModelResource(T fleshBlob) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(T fleshBlob) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(T fleshBlob) {
		return FleshBlobModel.ANIMATION;
	}

}