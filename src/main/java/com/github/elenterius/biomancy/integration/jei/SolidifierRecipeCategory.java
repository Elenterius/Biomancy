package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.SolidifierRecipe;
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

import java.text.DecimalFormat;

public class SolidifierRecipeCategory implements IRecipeCategory<SolidifierRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.SOLIDIFIER_RECIPE_TYPE);
	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private final IDrawable background;
	private final IDrawable icon;

	public SolidifierRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.SOLIDIFIER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/gui_solidifier.png"), 0, 0, 80, 47).setTextureSize(80, 47).build();
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends SolidifierRecipe> getRecipeClass() {
		return SolidifierRecipe.class;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.biomancy.recipe.solidifier");
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
	public void setIngredients(SolidifierRecipe recipe, IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.FLUID, recipe.getFluidIngredient().getMatchingFluidStacks());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, SolidifierRecipe recipe, IIngredients ingredients) {
		IGuiFluidStackGroup guiFSGroup = layout.getFluidStacks();
		guiFSGroup.init(INPUT_SLOT, true, 1, 14);
		guiFSGroup.set(ingredients);

		guiFSGroup.addTooltipCallback((index, input, ingredient, tooltip) -> {
			if (index == INPUT_SLOT) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				tooltip.add(new StringTextComponent(df.format(ingredient.getAmount()) + " mb").mergeStyle(TextFormatting.GRAY));
			}
		});

		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(OUTPUT_SLOT, false, 58, 14);
		guiISGroup.set(ingredients);
	}

	@Override
	public void draw(SolidifierRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			TranslationTextComponent timeString = new TranslationTextComponent("gui.jei.category.smelting.time.seconds", seconds);
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			fontRenderer.drawText(matrixStack, timeString, background.getWidth() - fontRenderer.getStringPropertyWidth(timeString), 0, 0xff808080);
		}
	}
}
