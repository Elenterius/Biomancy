package com.github.elenterius.biomancy.client.render.block.cradle;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PrimordialCradleModel extends DefaultedBlockGeoModel<PrimordialCradleBlockEntity> {

	public PrimordialCradleModel() {
		super(BiomancyMod.createRL("primordial_cradle"));
	}

	@Override
	public RenderType getRenderType(PrimordialCradleBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
