package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.client.renderer.entity.layers.ThickWoolSheepLayer;
import com.github.elenterius.biomancy.entity.gmo.ThickWoolSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThickWoolSheepRenderer extends MobRenderer<ThickWoolSheepEntity, SheepModel<ThickWoolSheepEntity>> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");

	public ThickWoolSheepRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SheepModel<>(), 0.7f);
		addLayer(new ThickWoolSheepLayer(this));
	}

	@Override
	public ResourceLocation getEntityTexture(ThickWoolSheepEntity entity) {
		return TEXTURE;
	}

}
