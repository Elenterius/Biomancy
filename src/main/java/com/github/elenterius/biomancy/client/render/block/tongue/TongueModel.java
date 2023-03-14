package com.github.elenterius.biomancy.client.render.block.tongue;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.tongue.TongueBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TongueModel extends AnimatedGeoModel<TongueBlockEntity> {

	@Override
	public ResourceLocation getModelResource(TongueBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/tongue.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TongueBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/tongue.png");
	}

	@Override
	public ResourceLocation getAnimationResource(TongueBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/tongue.animation.json");
	}

}
