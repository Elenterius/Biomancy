package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.aberration.FailedCowEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FailedCowRenderer extends MobRenderer<FailedCowEntity, CowModel<FailedCowEntity>> {

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/entity/failed_cow.png");

	public FailedCowRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new CowModel<>(), 0.7f);
//		addLayer(new SheepWoolLayer(this));
	}

	@Override
	public ResourceLocation getTextureLocation(FailedCowEntity entity) {
		return TEXTURE;
	}

}
