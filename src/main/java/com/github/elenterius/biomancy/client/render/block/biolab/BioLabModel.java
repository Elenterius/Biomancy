package com.github.elenterius.biomancy.client.render.block.biolab;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.biolab.BioLabBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BioLabModel extends AnimatedGeoModel<BioLabBlockEntity> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/block/bio_lab.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/bio_lab.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/bio_lab.animation.json");

	@Override
	public ResourceLocation getModelResource(BioLabBlockEntity blockEntity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(BioLabBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(BioLabBlockEntity blockEntity) {
		return ANIMATION;
	}

}
