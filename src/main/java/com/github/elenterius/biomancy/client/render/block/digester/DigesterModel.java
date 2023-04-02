package com.github.elenterius.biomancy.client.render.block.digester;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.digester.DigesterBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DigesterModel extends AnimatedGeoModel<DigesterBlockEntity> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/block/digester.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/digester.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/digester.animation.json");

	@Override
	public ResourceLocation getModelResource(DigesterBlockEntity blockEntity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(DigesterBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(DigesterBlockEntity blockEntity) {
		return ANIMATION;
	}

}
