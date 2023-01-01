package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.recipe.IngredientStack;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BioForgeRecipeCategory implements IRecipeCategory<BioForgeRecipe> {

	public static final RecipeType<BioForgeRecipe> RECIPE_TYPE = new RecipeType<>(ModRecipes.BIO_FORGING_RECIPE_TYPE.getId(), BioForgeRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	public BioForgeRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.BIO_FORGE.get()));
		ResourceLocation texture = BiomancyMod.createRL("textures/gui/jei/bio_forge_recipe.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, 152, 32).setTextureSize(152, 32).build();
	}

	//TODO: Change this code.
	/*@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}*/

	@Override
	public RecipeType<BioForgeRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	//TODO: Change this code.
	/*@Override
	public Class<? extends BioForgeRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}*/

	@Override
	public Component getTitle() {
		return Component.translatable("jei.biomancy.recipe.bio_forge");
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
	public void setRecipe(IRecipeLayoutBuilder builder, BioForgeRecipe recipe, IFocusGroup focuses) {
		//		builder.setShapeless();

		List<IngredientStack> ingredientQuantities = recipe.getIngredientQuantities();
		int y = 5;
		addInputSlot(builder, 1, y, ingredientQuantities, 0);
		addInputSlot(builder, 19, y, ingredientQuantities, 1);
		addInputSlot(builder, 37, y, ingredientQuantities, 2);
		addInputSlot(builder, 55, y, ingredientQuantities, 3);
		addInputSlot(builder, 73, y, ingredientQuantities, 4);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 131, y).addItemStack(recipe.getResultItem());
	}

	private void addInputSlot(IRecipeLayoutBuilder builder, int x, int y, List<IngredientStack> ingredients, int index) {
		if (index < 0) throw new IllegalArgumentException("index must be larger or equal to zero");
		if (index >= BioForgeRecipe.MAX_INGREDIENTS) throw new IllegalArgumentException("index must be smaller than " + BioForgeRecipe.MAX_INGREDIENTS);

		IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
		if (index < ingredients.size()) {
			slotBuilder.addItemStacks(ingredients.get(index).getItemsWithCount());
		}
	}

	@Override
	public void draw(BioForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
		Font fontRenderer = Minecraft.getInstance().font;
		int fuelCost = 1; //TODO: use constant
		MutableComponent costString = Component.literal("-" + fuelCost);
		fontRenderer.draw(poseStack, costString, 108, 32f - fontRenderer.lineHeight + 1, 0xff808080);
	}
}
