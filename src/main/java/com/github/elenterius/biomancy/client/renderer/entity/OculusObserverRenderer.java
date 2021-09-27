package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.entity.model.OculusObserverModel;
import com.github.elenterius.biomancy.entity.aberration.OculusObserverEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class OculusObserverRenderer extends MobRenderer<OculusObserverEntity, OculusObserverModel<OculusObserverEntity>> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/oculus_observer.png");

	public OculusObserverRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new OculusObserverModel<>(), 0.3f);
	}

	@Override
	protected float getFlipDegrees(OculusObserverEntity entityLivingBaseIn) {
		return 180f;
	}

	@Override
	public ResourceLocation getTextureLocation(OculusObserverEntity entity) {
		return TEXTURE;
	}

}
