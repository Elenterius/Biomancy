package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.client.renderer.entity.layers.ChromaSheepWoolLayer;
import com.github.elenterius.biomancy.entity.gmo.ChromaSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChromaSheepRenderer extends MobRenderer<ChromaSheepEntity, SheepModel<ChromaSheepEntity>> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");

	public ChromaSheepRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SheepModel<>(), 0.7f);
		addLayer(new ChromaSheepWoolLayer(this));
	}

	@Override
	public ResourceLocation getEntityTexture(ChromaSheepEntity entity) {
		return TEXTURE;
	}

}
