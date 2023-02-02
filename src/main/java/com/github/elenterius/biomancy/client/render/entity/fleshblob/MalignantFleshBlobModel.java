package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.fleshblob.MalignantFleshBlob;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MalignantFleshBlobModel<T extends MalignantFleshBlob> extends AnimatedGeoModel<T> {

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/flesh_blob/malignant_flesh_blob.png");
	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/entity/malignant_flesh_blob.geo.json");

	@Override
	public ResourceLocation getModelLocation(T fleshBlob) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(T fleshBlob) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T fleshBlob) {
		return FleshBlobModel.ANIMATION;
	}

}