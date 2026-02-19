package com.github.elenterius.biomancy.util.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class DebugInfo {

	private DebugInfo() {}

	public interface EntityContext {
		void renderDebugInfo(EntityRenderDispatcher renderDispatcher, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks);
	}

	public interface ItemContext {
		void renderDebugInfo(LivingEntity livingEntity, ItemStack mainHandItemStack, EntityRenderDispatcher renderDispatcher, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks);
	}

}
