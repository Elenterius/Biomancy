package com.github.elenterius.blightlings.client.renderer.entity;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.client.renderer.entity.model.FleshBlobModel;
import com.github.elenterius.blightlings.entity.FleshBlobEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class FleshBlobRenderer extends MobRenderer<FleshBlobEntity, FleshBlobModel<FleshBlobEntity>> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(BlightlingsMod.MOD_ID, "textures/entity/flesh_blob.png");
	private static final ResourceLocation AGGRESSIVE_TEXTURE = new ResourceLocation(BlightlingsMod.MOD_ID, "textures/entity/aggressive_flesh_blob.png");

	public FleshBlobRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new FleshBlobModel<>(), 0.66F);
	}

	@Override
	@Nonnull
	public ResourceLocation getEntityTexture(FleshBlobEntity entity) {
		return entity.getFleshBlobData() == 13 ? AGGRESSIVE_TEXTURE : TEXTURE;
	}

}