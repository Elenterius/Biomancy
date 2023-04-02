package com.github.elenterius.biomancy.client.render.block.bioforge;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.bioforge.BioForgeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BioForgeModel extends AnimatedGeoModel<BioForgeBlockEntity> {

	protected static final ResourceLocation MODEL = BiomancyMod.createRL("geo/block/bio_forge.geo.json");
	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/bio_forge.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/bio_forge.animation.json");

	@Override
	public ResourceLocation getModelResource(BioForgeBlockEntity blockEntity) {
		return MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(BioForgeBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(BioForgeBlockEntity blockEntity) {
		return ANIMATION;
	}

}
