package com.github.elenterius.biomancy.client.render.block.digester;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class DigesterModel extends DefaultedBlockGeoModel<DigesterBlockEntity> {

	public DigesterModel() {
		super(BiomancyMod.createRL("digester"));
	}

	@Override
	public RenderType getRenderType(DigesterBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
