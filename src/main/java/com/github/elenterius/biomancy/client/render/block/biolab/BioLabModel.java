package com.github.elenterius.biomancy.client.render.block.biolab;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.biolab.BioLabBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BioLabModel extends AnimatedGeoModel<BioLabBlockEntity> {

	@Override
	public ResourceLocation getModelResource(BioLabBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/bio_lab.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BioLabBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/bio_lab.png");
	}

	@Override
	public ResourceLocation getAnimationResource(BioLabBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/bio_lab.animation.json");
	}

}
