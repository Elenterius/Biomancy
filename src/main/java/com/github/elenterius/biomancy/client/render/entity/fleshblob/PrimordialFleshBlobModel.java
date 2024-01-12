package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import com.github.elenterius.biomancy.entity.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.fleshblob.EaterFleshBlob;
import com.github.elenterius.biomancy.entity.fleshblob.PrimordialHangryEaterFleshBlob;
import net.minecraft.resources.ResourceLocation;

public class PrimordialFleshBlobModel<T extends EaterFleshBlob & PrimordialFleshkin> extends GeckolibModel<T> {

	protected static final ResourceLocation BASE_TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/primordial_flesh_blob_neutral.png");
	protected static final ResourceLocation HUNGRY_TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/primordial_flesh_blob_hostile.png");

	@Override
	public ResourceLocation getModelResource(T fleshBlob) {
		return FleshBlobModel.MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(T fleshBlob) {
		return fleshBlob instanceof PrimordialHangryEaterFleshBlob ? HUNGRY_TEXTURE : BASE_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(T fleshBlob) {
		return FleshBlobModel.ANIMATION;
	}

}