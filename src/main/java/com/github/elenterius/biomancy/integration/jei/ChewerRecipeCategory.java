package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.ChewerRecipe;
import com.github.elenterius.biomancy.tileentity.ChewerTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;

public class ChewerRecipeCategory implements IRecipeCategory<ChewerRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.CHEWER_RECIPE_TYPE);
	private static final int OUTPUT_SLOT = 0;
	private static final int INPUT_SLOT = 1;
	private static final int INPUT_SLOT_FUEL = 2;
	private final IDrawable background;
	private final IDrawable icon;

	public ChewerRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CHEWER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/gui_chewer.png"), 0, 0, 89, 54).setTextureSize(89, 54).build();
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends ChewerRecipe> getRecipeClass() {
		return ChewerRecipe.class;
	}

	@Override
	public String getTitle() {
		return I18n.get("jei.biomancy.recipe.chewer");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(ChewerRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());

		int fuelCost = recipe.getCraftingTime() * ChewerTileEntity.FUEL_COST;
		ingredients.setInput(VanillaTypes.FLUID, new FluidStack(ModFluids.NUTRIENT_SLURRY.get(), fuelCost));
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ChewerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(INPUT_SLOT, true, 9, 13);
		guiISGroup.init(OUTPUT_SLOT, false, 67, 14);
		guiISGroup.set(ingredients);

		IGuiFluidStackGroup guiFSGroup = layout.getFluidStacks();
		guiFSGroup.init(INPUT_SLOT_FUEL, true, 10, 37);
		guiFSGroup.set(ingredients);

		guiFSGroup.addTooltipCallback((index, input, ingredient, tooltip) -> {
			if (index == INPUT_SLOT_FUEL) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				tooltip.add(new StringTextComponent(df.format(ingredient.getAmount()) + " mb").withStyle(TextFormatting.GRAY));
			}
		});
	}

	@Override
	public void draw(ChewerRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			TranslationTextComponent timeString = new TranslationTextComponent("gui.jei.category.smelting.time.seconds", seconds);
			FontRenderer fontRenderer = Minecraft.getInstance().font;
			fontRenderer.draw(matrixStack, timeString, background.getWidth() - (float) fontRenderer.width(timeString), 0, 0xff808080);
		}
	}
}
