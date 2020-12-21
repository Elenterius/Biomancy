package com.github.elenterius.blightlings.client.renderer.entity;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.client.renderer.entity.model.BloblingModel;
import com.github.elenterius.blightlings.entity.BloblingEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BloblingRenderer extends MobRenderer<BloblingEntity, BloblingModel<BloblingEntity>> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(BlightlingsMod.MOD_ID, "textures/entity/blobling.png");
	private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(BlightlingsMod.MOD_ID, "textures/entity/blobling_overlay.png");
	private static final RenderType EYE_RENDER_TYPE = RenderType.getEyes(OVERLAY_TEXTURE);

	public BloblingRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new BloblingModel<>(), 0.2F);
		addLayer(new AbstractEyesLayer<BloblingEntity, BloblingModel<BloblingEntity>>(this) {
			@Override
			public RenderType getRenderType() {
				return EYE_RENDER_TYPE;
			}
		});
	}

	@Override
	protected float getDeathMaxRotation(BloblingEntity entity) {
		return 180.0F;
	}

	@Override
	@Nonnull
	public ResourceLocation getEntityTexture(BloblingEntity entity) {
		return TEXTURE;
	}
}