package com.github.elenterius.biomancy.client.render.entity.sapberry;

import com.github.elenterius.biomancy.client.render.entity.CustomGeoProjectileRenderer;
import com.github.elenterius.biomancy.entity.projectile.SapberryProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SapberryProjectileRenderer extends CustomGeoProjectileRenderer<SapberryProjectile> {

	public SapberryProjectileRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new SapberryModel());
	}

	@Override
	public RenderType getRenderType(SapberryProjectile projectile, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
