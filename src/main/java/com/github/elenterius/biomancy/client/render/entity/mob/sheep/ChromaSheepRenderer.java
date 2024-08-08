package com.github.elenterius.biomancy.client.render.entity.mob.sheep;

import com.github.elenterius.biomancy.entity.mob.ChromaSheep;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChromaSheepRenderer extends MobRenderer<ChromaSheep, SheepModel<ChromaSheep>> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");

	public ChromaSheepRenderer(EntityRendererProvider.Context context) {
		super(context, new SheepModel<>(context.bakeLayer(ModelLayers.SHEEP)), 0.7f);
		addLayer(new ChromaSheepFurLayer(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(ChromaSheep entity) {
		return TEXTURE;
	}

}
