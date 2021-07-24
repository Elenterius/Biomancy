package com.github.elenterius.biomancy.client.renderer.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.entity.model.BoomlingModel;
import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class BoomlingRenderer extends MobRenderer<BoomlingEntity, BoomlingModel<BoomlingEntity>> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/boomling.png");
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(BiomancyMod.MOD_ID, "textures/entity/boomling_overlay.png");
	public static final RenderType RENDER_TYPE = RenderType.getEyes(OVERLAY_TEXTURE);

	public BoomlingRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new BoomlingModel<>(), 0.2F);
		addLayer(new LayerRenderer<BoomlingEntity, BoomlingModel<BoomlingEntity>>(this) {
			@Override
			public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, BoomlingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RENDER_TYPE);
				int rgb = entity.getColor();
				float r = (float) (rgb >> 16 & 255) / 255f;
				float g = (float) (rgb >> 8 & 255) / 255f;
				float b = (float) (rgb & 255) / 255f;
				getEntityModel().render(matrixStackIn, ivertexbuilder, 0xf00000, OverlayTexture.NO_OVERLAY, r, g, b, 1f);
			}
		});
	}

	@Override
	protected float getDeathMaxRotation(BoomlingEntity entity) {
		return 180f;
	}

	@Override
	@Nonnull
	public ResourceLocation getEntityTexture(BoomlingEntity entity) {
		return TEXTURE;
	}
}