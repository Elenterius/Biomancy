package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.world.block.entity.BioLabBlockEntity;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;

public class BioLabRecipeCategory implements IRecipeCategory<BioLabRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.BIO_BREWING_RECIPE_TYPE.getId());

	private final IDrawable background;
	private final IDrawable icon;

	public BioLabRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModItems.BIO_LAB.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/bio_lab_jei_gui.png"), 0, 0, 134, 54).setTextureSize(134, 54).addPadding(0, 4, 0, 0).build();
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<BioLabRecipe> getRecipeClass() {
		return BioLabRecipe.class;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("jei.biomancy.recipe.bio_lab");
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
	public void setIngredients(BioLabRecipe recipe, IIngredients ingredients) {
		ArrayList<Ingredient> list = new ArrayList<>(recipe.getIngredients());
		int emptySlots = BioLabRecipe.MAX_INGREDIENTS - list.size();
		for (int i = 0; i < emptySlots; i++) {
			list.add(Ingredient.EMPTY);
		}
		list.add(recipe.getReactant());

		ingredients.setInputIngredients(list);
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout layout, BioLabRecipe recipe, IIngredients ingredients) {
		layout.setShapeless();
		IGuiItemStackGroup guiISGroup = layout.getItemStacks();

		int index = 0;

		//ingredients
		guiISGroup.init(index++, true, 0, 9);
		guiISGroup.init(index++, true, 18, 9);
		guiISGroup.init(index++, true, 0, 9 + 18);
		guiISGroup.init(index++, true, 18, 9 + 18);

		guiISGroup.init(index++, true, 54, 18); //reactant

		guiISGroup.init(index, false, 112, 18); //result

		guiISGroup.set(ingredients);
	}

	@Override
	public void draw(BioLabRecipe recipe, PoseStack poseStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			int fuelCost = ticks * BioLabBlockEntity.FUEL_COST;
			Component timeText = new TranslatableComponent("gui.jei.category.smelting.time.seconds", seconds);
			Component costText = new TextComponent("+" + fuelCost + " ").append(new TranslatableComponent("tooltip.biomancy.nutrients_fuel"));
			Font fontRenderer = Minecraft.getInstance().font;
			fontRenderer.draw(poseStack, timeText, (float) background.getWidth() - fontRenderer.width(timeText), 42, 0xff808080);
			fontRenderer.draw(poseStack, costText, 0, 48, 0xff808080);
		}
	}
}
