package com.github.elenterius.biomancy.client.render.block.decomposer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.decomposer.DecomposerBlockEntity;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import net.minecraft.resources.ResourceLocation;

public class DecomposerModel extends GeckolibModel<DecomposerBlockEntity> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/block/decomposer.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/decomposer.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/decomposer.animation.json");

	@Override
	public ResourceLocation getModelResource(DecomposerBlockEntity blockEntity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(DecomposerBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(DecomposerBlockEntity blockEntity) {
		return ANIMATION;
	}

}
