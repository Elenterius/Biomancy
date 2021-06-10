package com.github.elenterius.biomancy.client.renderer.tileentity;

import com.github.elenterius.biomancy.tileentity.FleshbornChestTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;

@OnlyIn(Dist.CLIENT)
public class FleshbornChestTileEntityRender extends ItemStackTileEntityRenderer {

	private final Lazy<FleshbornChestTileEntity> tileEntityLazy = Lazy.of(FleshbornChestTileEntity::new);

	@Override
	public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType type, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		TileEntityRendererDispatcher.instance.renderItem(tileEntityLazy.get(), matrixStack, buffer, combinedLight, combinedOverlay);
	}

}
