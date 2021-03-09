package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.entity.model.BroodmotherModel;
import com.github.elenterius.biomancy.entity.hybrid.CrocospiderEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BroodmotherRenderer extends MobRenderer<CrocospiderEntity, BroodmotherModel<CrocospiderEntity>> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/broodmother.png");
	private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/broodmother_overlay.png");
	private static final RenderType EYE_RENDER_TYPE = RenderType.getEyes(OVERLAY_TEXTURE);

	public BroodmotherRenderer(EntityRendererManager rendererManager) {
		super(rendererManager, new BroodmotherModel<>(), 0.8F);
		addLayer(new AbstractEyesLayer<CrocospiderEntity, BroodmotherModel<CrocospiderEntity>>(this) {
			@Override
			public RenderType getRenderType() {
				return EYE_RENDER_TYPE;
			}
		});
	}

	@Override
	public ResourceLocation getEntityTexture(CrocospiderEntity entity) {
		return TEXTURE;
	}
}