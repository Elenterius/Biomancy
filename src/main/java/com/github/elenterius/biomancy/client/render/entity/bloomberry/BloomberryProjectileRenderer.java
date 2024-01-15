package com.github.elenterius.biomancy.client.render.entity.bloomberry;

import com.github.elenterius.biomancy.client.render.entity.CustomGeoProjectileRenderer;
import com.github.elenterius.biomancy.entity.projectile.BloomberryProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BloomberryProjectileRenderer extends CustomGeoProjectileRenderer<BloomberryProjectile> {

	public BloomberryProjectileRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new BloomberryModel());
	}

	@Override
	public RenderType getRenderType(BloomberryProjectile projectile, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
