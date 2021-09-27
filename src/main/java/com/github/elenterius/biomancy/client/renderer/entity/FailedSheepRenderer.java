package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.aberration.FailedSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FailedSheepRenderer extends MobRenderer<FailedSheepEntity, SheepModel<FailedSheepEntity>> {

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/sheep/failed_sheep.png");

	public FailedSheepRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SheepModel<>(), 0.7f);
//		addLayer(new SheepWoolLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(FailedSheepEntity entity) {
		return TEXTURE;
	}

}
