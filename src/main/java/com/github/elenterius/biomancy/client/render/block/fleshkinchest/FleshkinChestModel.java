package com.github.elenterius.biomancy.client.render.block.fleshkinchest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.fleshkinchest.FleshkinChestBlockEntity;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import net.minecraft.resources.ResourceLocation;

public class FleshkinChestModel extends GeckolibModel<FleshkinChestBlockEntity> {

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
