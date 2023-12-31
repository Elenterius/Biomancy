package com.github.elenterius.biomancy.client.render.entity.acidblob;

import com.github.elenterius.biomancy.client.render.entity.CustomGeoProjectileRenderer;
import com.github.elenterius.biomancy.entity.projectile.AcidBlobProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AcidBlobProjectileRenderer extends CustomGeoProjectileRenderer<AcidBlobProjectile> {

	public AcidBlobProjectileRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new AcidBlobModel());
	}

	@Override
	public RenderType getRenderType(AcidBlobProjectile projectile, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
