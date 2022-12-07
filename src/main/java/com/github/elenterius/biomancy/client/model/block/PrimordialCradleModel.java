package com.github.elenterius.biomancy.client.model.block;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.PrimordialCradleBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PrimordialCradleModel extends AnimatedGeoModel<PrimordialCradleBlockEntity> {

	@Override
	public ResourceLocation getModelLocation(PrimordialCradleBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/primordial_cradle.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PrimordialCradleBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/primordial_cradle.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PrimordialCradleBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/primordial_cradle.animation.json");
	}

}
