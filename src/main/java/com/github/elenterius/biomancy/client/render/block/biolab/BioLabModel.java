package com.github.elenterius.biomancy.client.render.block.biolab;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.client.render.GeckolibModel;
import net.minecraft.resources.ResourceLocation;

public class BioLabModel extends GeckolibModel<BioLabBlockEntity> {

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
