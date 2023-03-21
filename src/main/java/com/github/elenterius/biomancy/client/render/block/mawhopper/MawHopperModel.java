package com.github.elenterius.biomancy.client.render.block.mawhopper;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.mawhopper.MawHopperBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MawHopperModel extends AnimatedGeoModel<MawHopperBlockEntity> {
	@Override
	public ResourceLocation getModelResource(MawHopperBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/maw_hopper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MawHopperBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/maw_hopper.png");
	}

	@Override
	public ResourceLocation getAnimationResource(MawHopperBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/maw_hopper.animation.json");
	}
}
