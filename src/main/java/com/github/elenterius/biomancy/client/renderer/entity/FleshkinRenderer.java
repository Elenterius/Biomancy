package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.entity.layers.FleshkinHeadLayer;
import com.github.elenterius.biomancy.client.renderer.entity.model.FleshkinModel;
import com.github.elenterius.biomancy.entity.golem.FleshkinEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FleshkinRenderer<T extends FleshkinEntity> extends BipedRenderer<T, FleshkinModel<T>> {

	private static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/fleshkin.png");

	public FleshkinRenderer(EntityRendererManager renderManagerIn) {
		this(renderManagerIn, new FleshkinModel<>(0f, false), new FleshkinModel<>(0.5f, true), new FleshkinModel<>(1f, true));
	}

	protected FleshkinRenderer(EntityRendererManager renderManagerIn, FleshkinModel<T> model, FleshkinModel<T> armor1, FleshkinModel<T> armor2) {
		super(renderManagerIn, model, 0.5f);
		addLayer(new BipedArmorLayer<>(this, armor1, armor2));
		addLayer(new FleshkinHeadLayer<>(this));
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEXTURE;
	}
}
