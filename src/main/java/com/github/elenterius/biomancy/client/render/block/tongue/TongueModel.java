package com.github.elenterius.biomancy.client.render.block.tongue;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.tongue.TongueBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TongueModel extends AnimatedGeoModel<TongueBlockEntity> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/block/tongue.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/tongue.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/tongue.animation.json");

	@Override
	public ResourceLocation getModelResource(TongueBlockEntity blockEntity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(TongueBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(TongueBlockEntity blockEntity) {
		return ANIMATION;
	}

}
