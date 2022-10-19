package com.github.elenterius.biomancy.client.renderer.item;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.world.block.entity.BioLabBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import com.github.elenterius.biomancy.world.item.BEWLBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Lazy;

public class BEWLRenderer extends BlockEntityWithoutLevelRenderer {

	public static final BEWLRenderer INSTANCE = new BEWLRenderer();

	private final Lazy<CreatorBlockEntity> creator = Lazy.of(() -> new CreatorBlockEntity(BlockPos.ZERO, ModBlocks.CREATOR.get().defaultBlockState()));
	private final Lazy<BioLabBlockEntity> bioLab = Lazy.of(() -> new BioLabBlockEntity(BlockPos.ZERO, ModBlocks.BIO_LAB.get().defaultBlockState()));

	public BEWLRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		//do nothing
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Item item = stack.getItem();

		if (item instanceof BEWLBlockItem) {
			if (item == ModItems.CREATOR.get()) {
				renderBlockEntity(poseStack, buffer, packedLight, packedOverlay, creator.get());
			} else if (item == ModItems.BIO_LAB.get()) {
				renderBlockEntity(poseStack, buffer, packedLight, packedOverlay, bioLab.get());
			}
		}
	}

	private void renderBlockEntity(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BlockEntity blockEntity) {
		Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack, buffer, packedLight, packedOverlay);
	}

}
