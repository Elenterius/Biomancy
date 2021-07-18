package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.ChewerRecipe;
import com.github.elenterius.biomancy.tileentity.ChewerTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ChewerRecipeCategory implements IRecipeCategory<ChewerRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.CHEWER_RECIPE_TYPE);
	private static final int OUTPUT_SLOT = 0;
	private static final int INPUT_SLOT = 1;
	private final IDrawable background;
	private final IDrawable icon;

	public ChewerRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CHEWER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/gui_chewer.png"), 0, 0, 80, 47).setTextureSize(80, 47).build();
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
		return I18n.format("jei.biomancy.recipe.chewer");
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
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, ChewerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(INPUT_SLOT, true, 0, 13);
		guiISGroup.init(OUTPUT_SLOT, false, 58, 14);

		guiISGroup.set(ingredients);
	}

	@Override
	public void draw(ChewerRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			TranslationTextComponent timeString = new TranslationTextComponent("gui.jei.category.smelting.time.seconds", seconds);
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			fontRenderer.drawText(matrixStack, timeString, background.getWidth() - fontRenderer.getStringPropertyWidth(timeString), 0, 0xff808080);
			int fuelCost = ticks * ChewerTileEntity.FUEL_COST;
			IFormattableTextComponent costText = new StringTextComponent("+" + fuelCost + "mb ").appendSibling(new TranslationTextComponent("fluid.biomancy.nutrient_slurry"));
			fontRenderer.drawText(matrixStack, costText, 0, background.getHeight() - fontRenderer.FONT_HEIGHT, 0xff808080);
		}
	}
}
