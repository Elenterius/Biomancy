package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.world.block.entity.DigesterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DigesterRecipeCategory implements IRecipeCategory<DigesterRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.DIGESTING_RECIPE_TYPE.getId());

	private final IDrawable background;
	private final IDrawable icon;

	public DigesterRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModItems.DIGESTER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/digester_recipe.png"), 0, 0, 80, 47).setTextureSize(80, 47).addPadding(0, 4, 0, 0).build();
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<DigesterRecipe> getRecipeClass() {
		return DigesterRecipe.class;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("jei.biomancy.recipe.digester");
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
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, DigesterRecipe recipe, IIngredients ingredients) {
		layout.setShapeless();
		IGuiItemStackGroup guiISGroup = layout.getItemStacks();

		guiISGroup.init(0, true, 0, 13); //ingredient
		guiISGroup.init(1, false, 57, 13); //result

		guiISGroup.set(ingredients);
	}

	@Override
	public void draw(DigesterRecipe recipe, PoseStack poseStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			int fuelCost = ticks * DigesterBlockEntity.FUEL_COST;
			Component timeText = new TranslatableComponent("gui.jei.category.smelting.time.seconds", seconds);
			Component costText = new TextComponent("+" + fuelCost + " ").append(new TranslatableComponent("tooltip.biomancy.nutrients_fuel"));
			Font fontRenderer = Minecraft.getInstance().font;
			fontRenderer.draw(poseStack, timeText, (float) background.getWidth() - fontRenderer.width(timeText), 42, 0xff808080);
			fontRenderer.draw(poseStack, costText, 0, 48, 0xff808080);
		}
	}

}
