package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.client.renderer.entity.layers.SheepSilkLayer;
import com.github.elenterius.biomancy.entity.mutation.SilkyWoolSheepEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilkyWoolSheepRenderer extends MobRenderer<SilkyWoolSheepEntity, SheepModel<SilkyWoolSheepEntity>> {

	protected static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");

	public SilkyWoolSheepRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SheepModel<>(), 0.7f);
		addLayer(new SheepSilkLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(SilkyWoolSheepEntity entity) {
		return TEXTURE;
	}

}
