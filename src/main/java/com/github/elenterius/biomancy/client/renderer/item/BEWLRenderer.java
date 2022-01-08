package com.github.elenterius.biomancy.client.renderer.item;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

public class BEWLRenderer extends BlockEntityWithoutLevelRenderer {

	public static final BEWLRenderer INSTANCE = new BEWLRenderer();

	private final Lazy<CreatorBlockEntity> creatorBE = Lazy.of(() -> new CreatorBlockEntity(BlockPos.ZERO, ModBlocks.CREATOR.get().defaultBlockState()));

	public BEWLRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		//do nothing
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (stack.is(ModItems.CREATOR.get())) {
			Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(creatorBE.get(), poseStack, buffer, packedLight, packedOverlay);
		}
	}

}
