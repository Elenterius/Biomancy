package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.entity.model.BeetlingModel;
import com.github.elenterius.biomancy.entity.MasonBeetleEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BlockBeetleRenderer extends MobRenderer<MasonBeetleEntity, BeetlingModel<MasonBeetleEntity>> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/beetle.png");
	private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/beetle_overlay.png");
	private static final RenderType EYE_RENDER_TYPE = RenderType.getEyes(OVERLAY_TEXTURE);

	public BlockBeetleRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new BeetlingModel<>(), 0.24F);
		addLayer(new AbstractEyesLayer<MasonBeetleEntity, BeetlingModel<MasonBeetleEntity>>(this) {
			@Override
			public RenderType getRenderType() {
				return EYE_RENDER_TYPE;
			}
		});
	}

	@Override
	protected float getDeathMaxRotation(MasonBeetleEntity entity) {
		return 180.0F;
	}

	@Override
	@Nonnull
	public ResourceLocation getEntityTexture(MasonBeetleEntity entity) {
		return TEXTURE;
	}
}