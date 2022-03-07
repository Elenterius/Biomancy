package com.github.elenterius.biomancy.client.model.block;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.FleshChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FleshChestModel extends AnimatedGeoModel<FleshChestBlockEntity> {

	@Override
	public ResourceLocation getModelLocation(FleshChestBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/flesh_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(FleshChestBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/flesh_chest.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(FleshChestBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/flesh_chest.animation.json");
	}

}
