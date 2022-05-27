package com.github.elenterius.biomancy.client.model.block;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.DigesterBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DigesterModel extends AnimatedGeoModel<DigesterBlockEntity> {

	@Override
	public ResourceLocation getModelLocation(DigesterBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/digester.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DigesterBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/digester.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DigesterBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/digester.animation.json");
	}

}
