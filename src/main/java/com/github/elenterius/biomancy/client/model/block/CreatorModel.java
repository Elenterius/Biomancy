package com.github.elenterius.biomancy.client.model.block;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CreatorModel extends AnimatedGeoModel<CreatorBlockEntity> {

	@Override
	public ResourceLocation getModelLocation(CreatorBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/creator.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(CreatorBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/creator.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(CreatorBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/creator.animation.json");
	}

}
