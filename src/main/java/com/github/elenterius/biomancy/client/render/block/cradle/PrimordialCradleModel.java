package com.github.elenterius.biomancy.client.render.block.cradle;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import net.minecraft.resources.ResourceLocation;

public class PrimordialCradleModel extends GeckolibModel<PrimordialCradleBlockEntity> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/block/primordial_cradle.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/primordial_cradle.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/primordial_cradle.animation.json");

	@Override
	public ResourceLocation getModelResource(PrimordialCradleBlockEntity blockEntity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(PrimordialCradleBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(PrimordialCradleBlockEntity blockEntity) {
		return ANIMATION;
	}

}
