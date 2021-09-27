package com.github.elenterius.biomancy.client.gui.drawable;

import com.github.elenterius.biomancy.fluid.FluidRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTankBar extends Drawable {

	public FluidTank fluidTank;
	public float amountNormalized = 0f;

	public FluidTankBar(int posX, int posY, int widthIn, int heightIn) {
		super(posX, posY, widthIn, heightIn);
	}

	public FluidTankBar(int posX, int posY, int widthIn, int heightIn, FluidTank fluidTankIn) {
		super(posX, posY, widthIn, heightIn);
		fluidTank = fluidTankIn;
	}

	public void update(FluidTank fluidTankIn) {
		fluidTank = fluidTankIn;
		amountNormalized = MathHelper.clamp(fluidTank.getFluidAmount() / (float) fluidTank.getCapacity(), 0f, 1f);
	}

	@Override
	public void draw(Minecraft mc, MatrixStack matrixStack, int guiLeft, int guiTop, int mouseX, int mouseY) {
		int posX = guiLeft + x;
		int posY = guiTop + y;

		FluidStack fluidStack = fluidTank.getFluid();
		Fluid fluid = fluidStack.getFluid();
		FluidAttributes fluidAttributes = fluid.getAttributes();
		TextureAtlasSprite fluidTexture = Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(fluidAttributes.getStillTexture(fluidStack));
		int color = fluidAttributes.getColor(fluidStack);

		int scaledHeight = (int) (height * amountNormalized);
		if (amountNormalized > 0f && scaledHeight == 0) scaledHeight = 1;

		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		FluidRenderUtil.drawTiledSprite(mc, matrixStack, posX, posY, width, height, color, scaledHeight, fluidTexture);
		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
	}

}
