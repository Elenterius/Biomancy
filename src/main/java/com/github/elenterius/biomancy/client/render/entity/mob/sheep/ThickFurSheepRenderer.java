package com.github.elenterius.biomancy.client.render.entity.mob.sheep;

import com.github.elenterius.biomancy.entity.mob.ThickFurSheep;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ThickFurSheepRenderer extends MobRenderer<ThickFurSheep, SheepModel<ThickFurSheep>> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");

	public ThickFurSheepRenderer(EntityRendererProvider.Context context) {
		super(context, new SheepModel<>(context.bakeLayer(ModelLayers.SHEEP)), 0.7f);
		addLayer(new ThickFurSheepLayer<>(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(ThickFurSheep entity) {
		return TEXTURE;
	}

}
