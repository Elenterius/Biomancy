package com.github.elenterius.biomancy.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class GenericISTER<T extends TileEntity> extends ItemStackTileEntityRenderer {

	private final Supplier<T> tileSupplier;

	public GenericISTER(Supplier<T> tileEntitySupplier) {
		tileSupplier = tileEntitySupplier;
	}

	@Override
	public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType type, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		TileEntityRendererDispatcher.instance.renderItem(tileSupplier.get(), matrixStack, buffer, combinedLight, combinedOverlay);
	}

}
