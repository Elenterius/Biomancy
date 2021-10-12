package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
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
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;

public class DigesterRecipeCategory implements IRecipeCategory<DigesterRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.DIGESTER_RECIPE_TYPE);
	private static final int OUTPUT_SLOT = 0;
	private static final int BYPRODUCT_SLOT = 1;
	private static final int INPUT_SLOT = 2;
	private static final int INPUT_SLOT_WATER = 3;
	private final IDrawable background;
	private final IDrawable icon;

	public DigesterRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.DIGESTER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/gui_digester.png"), 0, 0, 136, 47).setTextureSize(136, 47).build();
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
		return I18n.get("jei.biomancy.recipe.digester");
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

		int waterCost = recipe.getCraftingTime() * DigesterTileEntity.FUEL_COST;
		ingredients.setInput(VanillaTypes.FLUID, new FluidStack(Fluids.WATER, waterCost));

		if (recipe.getByproduct() != null) {
			ingredients.setOutput(VanillaTypes.ITEM, recipe.getByproduct().getItemStack());
		}
		ingredients.setOutput(VanillaTypes.FLUID, recipe.getFluidOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, DigesterRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(INPUT_SLOT, true, 36, 13);
		guiISGroup.init(BYPRODUCT_SLOT, false, 82 + 36, 18);
		guiISGroup.set(ingredients);

		IGuiFluidStackGroup guiFSGroup = layout.getFluidStacks();
		guiFSGroup.init(INPUT_SLOT_WATER, true, 1, 14);
		guiFSGroup.init(OUTPUT_SLOT, false, 59 + 36, 15);
		guiFSGroup.set(ingredients);

		guiFSGroup.addTooltipCallback((index, input, ingredient, tooltip) -> {
			if (index == OUTPUT_SLOT || index == INPUT_SLOT_WATER) {
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				tooltip.add(new StringTextComponent(df.format(ingredient.getAmount()) + " mb").withStyle(TextFormatting.GRAY));
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
					tooltip.add(new StringTextComponent(chance + "% ").append(new TranslationTextComponent(TextUtil.getTranslationKey("tooltip", "chance"))).withStyle(TextFormatting.GRAY));
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
			FontRenderer fontRenderer = Minecraft.getInstance().font;
			int stringWidth = fontRenderer.width(timeString);
			fontRenderer.draw(matrixStack, timeString, background.getWidth() - stringWidth, 0, 0xff808080);
		}
	}
}
