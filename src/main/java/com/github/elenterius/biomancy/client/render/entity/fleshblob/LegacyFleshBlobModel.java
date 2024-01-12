package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import com.github.elenterius.biomancy.entity.fleshblob.EaterFleshBlob;
import net.minecraft.resources.ResourceLocation;

public class LegacyFleshBlobModel<T extends EaterFleshBlob> extends GeckolibModel<T> {

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/flesh_blob_legacy.png");

	@Override
	public ResourceLocation getModelResource(T fleshBlob) {
		return FleshBlobModel.MODEL;
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