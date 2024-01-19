package com.github.elenterius.biomancy.client.render.block.bioforge;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.bioforge.BioForgeBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class BioForgeModel extends DefaultedBlockGeoModel<BioForgeBlockEntity> {

	public BioForgeModel() {
		super(BiomancyMod.createRL("bio_forge"));
	}

	@Override
	public RenderType getRenderType(BioForgeBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
