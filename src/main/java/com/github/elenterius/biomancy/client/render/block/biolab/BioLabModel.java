package com.github.elenterius.biomancy.client.render.block.biolab;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class BioLabModel extends DefaultedBlockGeoModel<BioLabBlockEntity> {

	public BioLabModel() {
		super(BiomancyMod.createRL("bio_lab"));
	}

	@Override
	public RenderType getRenderType(BioLabBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
