package com.github.elenterius.biomancy.client.render.block.tongue;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.tongue.TongueBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class TongueModel extends DefaultedBlockGeoModel<TongueBlockEntity> {

	public TongueModel() {
		super(BiomancyMod.createRL("tongue"));
	}

	@Override
	public boolean crashIfBoneMissing() {
		return true;
	}

	@Override
	public RenderType getRenderType(TongueBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityCutout(texture);
	}

}
