package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.recipe.IngredientStack;
import com.github.elenterius.biomancy.world.block.entity.BioLabBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BioLabRecipeCategory implements IRecipeCategory<BioLabRecipe> {

	public static final RecipeType<BioLabRecipe> RECIPE_TYPE = new RecipeType<>(BiomancyMod.createRL(ModRecipes.BIO_BREWING_RECIPE_TYPE.getId()), BioLabRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	public BioLabRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModItems.BIO_LAB.get()));
		ResourceLocation texture = BiomancyMod.createRL("textures/gui/jei/bio_lab_recipe.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, 134, 54).setTextureSize(134, 54).addPadding(0, 4, 0, 0).build();
	}

	@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	@Override
	public RecipeType<BioLabRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public Class<? extends BioLabRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
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
	public void setRecipe(IRecipeLayoutBuilder builder, BioLabRecipe recipe, IFocusGroup focuses) {
		builder.setShapeless();

		List<IngredientStack> ingredientQuantities = recipe.getIngredientQuantities();
		addInputSlot(builder, 1, 10, ingredientQuantities, 0);
		addInputSlot(builder, 19, 10, ingredientQuantities, 1);
		addInputSlot(builder, 1, 10 + 18, ingredientQuantities, 2);
		addInputSlot(builder, 19, 10 + 18, ingredientQuantities, 3);

		builder.addSlot(RecipeIngredientRole.INPUT, 55, 19).addIngredients(recipe.getReactant());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 19).addItemStack(recipe.getResultItem());
	}

	private void addInputSlot(IRecipeLayoutBuilder builder, int x, int y, List<IngredientStack> ingredients, int index) {
		assert index >= 0;
		assert index < BioLabRecipe.MAX_INGREDIENTS;

		IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
		if (index < ingredients.size()) {
			slotBuilder.addItemStacks(ingredients.get(index).getItemsWithCount());
		}
	}

	@Override
	public void draw(BioLabRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
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
