package com.github.elenterius.biomancy.client.model.block;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.DecomposerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DecomposerModel extends AnimatedGeoModel<DecomposerBlockEntity> {

	@Override
	public ResourceLocation getModelLocation(DecomposerBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/decomposer.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DecomposerBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/decomposer.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DecomposerBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/decomposer.animation.json");
	}

}
