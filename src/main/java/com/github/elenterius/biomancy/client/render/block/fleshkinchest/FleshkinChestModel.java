package com.github.elenterius.biomancy.client.render.block.fleshkinchest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FleshkinChestModel extends AnimatedGeoModel<FleshkinChestBlockEntity> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/block/fleshkin_chest.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/fleshkin_chest.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/fleshkin_chest.animation.json");

	@Override
	public ResourceLocation getModelResource(FleshkinChestBlockEntity blockEntity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(FleshkinChestBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(FleshkinChestBlockEntity blockEntity) {
		return ANIMATION;
	}

}
