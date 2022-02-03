package com.github.elenterius.biomancy.client.model.block;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.BioForgeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BioForgeModel extends AnimatedGeoModel<BioForgeBlockEntity> {

	@Override
	public ResourceLocation getModelLocation(BioForgeBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/bio_forge.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BioForgeBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/bio_forge.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(BioForgeBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/bio_forge.animation.json");
	}

}
