package com.github.elenterius.biomancy.client.render.block.decomposer;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.decomposer.DecomposerBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class DecomposerModel extends DefaultedBlockGeoModel<DecomposerBlockEntity> {

	public DecomposerModel() {
		super(BiomancyMod.createRL("decomposer"));
	}

	@Override
	public RenderType getRenderType(DecomposerBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucentCull(texture);
	}

}
