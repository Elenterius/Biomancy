package com.github.elenterius.biomancy.client.render.block.fleshkinchest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FleshkinChestModel extends AnimatedGeoModel<FleshkinChestBlockEntity> {

	@Override
	public ResourceLocation getModelResource(FleshkinChestBlockEntity blockEntity) {
		return BiomancyMod.createRL("geo/block/fleshkin_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FleshkinChestBlockEntity blockEntity) {
		return BiomancyMod.createRL("textures/block/fleshkin_chest.png");
	}

	@Override
	public ResourceLocation getAnimationResource(FleshkinChestBlockEntity blockEntity) {
		return BiomancyMod.createRL("animations/block/fleshkin_chest.animation.json");
	}

}
