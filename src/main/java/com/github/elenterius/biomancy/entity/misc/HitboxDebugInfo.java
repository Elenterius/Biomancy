package com.github.elenterius.biomancy.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public interface HitboxDebugInfo {

	void renderHitboxInfo(EntityRenderDispatcher renderDispatcher, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks);

}
