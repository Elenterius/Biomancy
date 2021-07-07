package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import com.google.common.collect.Lists;
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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.DecimalFormat;

public class DigesterRecipeCategory implements IRecipeCategory<DigesterRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.DIGESTER_RECIPE_TYPE);
	private static final int OUTPUT_SLOT = 0;
	private static final int BYPRODUCT_SLOT = 1;
	private static final int INPUT_SLOT = 2;
	private final IDrawable background;
	private final IDrawable icon;

	public DigesterRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.DIGESTER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/gui_digester.png"), 0, 0, 100, 47).setTextureSize(100, 47).build();
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends DigesterRecipe> getRecipeClass() {
		return DigesterRecipe.class;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.biomancy.recipe.digester");
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
	public void setIngredients(DigesterRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		if (recipe.getByproduct() != null) {
			ingredients.setOutputs(VanillaTypes.ITEM, Lists.newArrayList(recipe.getByproduct().getItemStack()));
		}
		ingredients.setOutputs(VanillaTypes.FLUID, Lists.newArrayList(recipe.getFluidOutput()));
	}

	@Override
	public void setRecipe(IRecipeLayout layout, DigesterRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(INPUT_SLOT, true, 0, 13);
		guiISGroup.init(BYPRODUCT_SLOT, false, 82, 18);
		guiISGroup.set(ingredients);

		IGuiFluidStackGroup guiFSGroup = layout.getFluidStacks();
		guiFSGroup.init(OUTPUT_SLOT, false, 59, 15);
		guiFSGroup.set(ingredients);

		guiFSGroup.addTooltipCallback((index, input, ingredient, tooltip) -> {
			if (index == OUTPUT_SLOT) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				tooltip.add(new StringTextComponent(df.format(ingredient.getAmount()) + " mb").mergeStyle(TextFormatting.GRAY));
			}
		});

		guiISGroup.addTooltipCallback((index, input, stack, tooltip) -> {
			if (index == BYPRODUCT_SLOT) {
				if (!stack.isEmpty()) {
					int chance = 100;
					Byproduct byproduct = recipe.getByproduct();
					if (byproduct != null && byproduct.getItem() == stack.getItem()) {
						chance = Math.round(byproduct.getChance() * 100);
					}
					tooltip.add(new StringTextComponent(chance + "% ").appendSibling(new TranslationTextComponent(TextUtil.getTranslationKey("tooltip", "chance"))).mergeStyle(TextFormatting.GRAY));
				}
			}
		});
	}

	@Override
	public void draw(DigesterRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			TranslationTextComponent timeString = new TranslationTextComponent("gui.jei.category.smelting.time.seconds", seconds);
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			int stringWidth = fontRenderer.getStringPropertyWidth(timeString);
			fontRenderer.drawText(matrixStack, timeString, background.getWidth() - stringWidth, 0, 0xff808080);
			int waterCost = ticks * DigesterTileEntity.FUEL_COST;
			IFormattableTextComponent costText = new StringTextComponent("+" + waterCost + " ").appendSibling(new TranslationTextComponent("tooltip.biomancy.water"));
			fontRenderer.drawText(matrixStack, costText, 0, background.getHeight() - fontRenderer.FONT_HEIGHT, 0xff808080);
		}
	}
}
