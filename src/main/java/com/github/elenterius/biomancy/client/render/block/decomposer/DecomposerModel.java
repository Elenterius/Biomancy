package com.github.elenterius.biomancy.client.render.block.decomposer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.decomposer.DecomposerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DecomposerModel extends AnimatedGeoModel<DecomposerBlockEntity> {

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
