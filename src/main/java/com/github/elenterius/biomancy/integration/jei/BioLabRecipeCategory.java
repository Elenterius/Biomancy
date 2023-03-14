package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.recipe.IngredientStack;
import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import com.github.elenterius.biomancy.world.block.biolab.BioLabBlockEntity;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BioLabRecipeCategory implements IRecipeCategory<BioLabRecipe> {

	public static final RecipeType<BioLabRecipe> RECIPE_TYPE = new RecipeType<>(ModRecipes.BIO_BREWING_RECIPE_TYPE.getId(), BioLabRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	public BioLabRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.BIO_LAB.get()));
		ResourceLocation texture = BiomancyMod.createRL("textures/gui/jei/bio_lab_recipe.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, 134, 54).setTextureSize(134, 54).addPadding(0, 4, 0, 0).build();
	}

	//TODO: Change this code.
	/*@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}*/

	@Override
	public RecipeType<BioLabRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	//TODO: Change this code.
	/*@Override
	public Class<? extends BioLabRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}*/

	@Override
	public Component getTitle() {
		return ComponentUtil.translatable("jei.biomancy.recipe.bio_lab");
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
		addInputSlot(builder, 1, 1, ingredientQuantities, 0);
		addInputSlot(builder, 19, 1, ingredientQuantities, 1);
		addInputSlot(builder, 1, 1 + 18, ingredientQuantities, 2);
		addInputSlot(builder, 19, 1 + 18, ingredientQuantities, 3);

		builder.addSlot(RecipeIngredientRole.INPUT, 55, 10).addIngredients(recipe.getReactant());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 10).addItemStack(recipe.getResultItem());
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
		Font fontRenderer = Minecraft.getInstance().font;

		int ticks = recipe.getCraftingTime();
		int seconds = ticks > 0 ? ticks / 20 : 0;
		Component timeText = ComponentUtil.translatable("gui.jei.category.smelting.time.seconds", seconds);
		fontRenderer.draw(poseStack, timeText, 102, 50 - fontRenderer.lineHeight, 0xff808080);

		int fuelCost = NutrientFuelUtil.getFuelCost(BioLabBlockEntity.BASE_COST, ticks);
		Component costText = ComponentUtil.literal("-" + fuelCost);
		fontRenderer.draw(poseStack, costText, 69, 50 - fontRenderer.lineHeight, 0xff808080);
	}

}
