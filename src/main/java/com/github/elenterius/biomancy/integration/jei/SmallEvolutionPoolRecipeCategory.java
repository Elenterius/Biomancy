package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.EvolutionPoolRecipe;
import com.github.elenterius.biomancy.tileentity.EvolutionPoolTileEntity;
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

public class SmallEvolutionPoolRecipeCategory implements IRecipeCategory<EvolutionPoolRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.EVOLUTION_POOL_RECIPE_TYPE.toString());

	private final IDrawable background;
	private final IDrawable icon;

	public SmallEvolutionPoolRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.MUTAGENIC_BILE.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/gui_small_evolution_pool.png"), 0, 0, 116, 54).setTextureSize(116, 54).addPadding(0, 4, 0, 0).build();
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends EvolutionPoolRecipe> getRecipeClass() {
		return EvolutionPoolRecipe.class;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.biomancy.recipe.evolution_pool");
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
	public void setIngredients(EvolutionPoolRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}

	private static final int OUTPUT_SLOT = 0;
	private static final int INPUT_SLOT = 1;

	@Override
	public void setRecipe(IRecipeLayout layout, EvolutionPoolRecipe recipe, IIngredients ingredients) {
		layout.setShapeless();

		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(OUTPUT_SLOT, false, 94, 18);

		int index = INPUT_SLOT;
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 3; x++) {
				guiISGroup.init(index++, true, x * 18, 9 + y * 18);
			}
		}

		guiISGroup.set(ingredients);
	}

	@Override
	public void draw(EvolutionPoolRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			int mutagenCost = ticks * EvolutionPoolTileEntity.FUEL_COST;
			TranslationTextComponent timeText = new TranslationTextComponent("gui.jei.category.smelting.time.seconds", seconds);
			IFormattableTextComponent costText = new StringTextComponent("+" + mutagenCost + " ").append(new TranslationTextComponent("tooltip.biomancy.mutagen"));
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			fontRenderer.func_243248_b(matrixStack, timeText, background.getWidth() - fontRenderer.getStringPropertyWidth(timeText), 42, 0xff808080);
			fontRenderer.func_243248_b(matrixStack, costText, 0, 47, 0xff808080);
		}
	}
}
