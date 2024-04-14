package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.crafting.recipe.BioLabRecipe;
import com.github.elenterius.biomancy.crafting.recipe.IngredientStack;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Objects;

public class BioLabRecipeCategory implements IRecipeCategory<BioLabRecipe> {

	public static final RecipeType<BioLabRecipe> RECIPE_TYPE = new RecipeType<>(ModRecipes.BIO_BREWING_RECIPE_TYPE.getId(), BioLabRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	private final RecipeWrapper inputInventoryWrapper;

	public BioLabRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.BIO_LAB.get()));
		ResourceLocation texture = BiomancyMod.createRL("textures/gui/jei/bio_lab_recipe.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, 134, 54).setTextureSize(134, 54).addPadding(0, 4, 0, 0).build();

		inputInventoryWrapper = new RecipeWrapper(new ItemStackHandler(BioLabBlockEntity.INPUT_SLOTS));
	}

	@Override
	public RecipeType<BioLabRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

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
		ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);

		builder.setShapeless();

		List<IngredientStack> ingredientQuantities = recipe.getIngredientQuantities();
		addInputSlot(builder, 1, 1, ingredientQuantities, 0);
		addInputSlot(builder, 19, 1, ingredientQuantities, 1);
		addInputSlot(builder, 1, 1 + 18, ingredientQuantities, 2);
		addInputSlot(builder, 19, 1 + 18, ingredientQuantities, 3);

		builder.addSlot(RecipeIngredientRole.INPUT, 55, 10).addIngredients(recipe.getReactant());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 10).addItemStack(recipe.getResultItem(level.registryAccess()));
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
	public void draw(BioLabRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;

		List<IRecipeSlotView> slotViews = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT);
		for (int i = 0; i < slotViews.size(); i++) {
			IRecipeSlotView slotView = slotViews.get(i);
			ItemStack itemStack = slotView.getDisplayedItemStack().orElse(ItemStack.EMPTY);
			inputInventoryWrapper.setItem(i, itemStack);
		}

		int ticks = recipe.getCraftingTimeTicks(inputInventoryWrapper);
		int seconds = ticks > 0 ? ticks / 20 : 0;
		Component timeText = ComponentUtil.translatable("gui.jei.category.smelting.time.seconds", seconds);
		guiGraphics.drawString(font, timeText, 102, 50 - font.lineHeight, ColorStyles.WHITE_ARGB);

		Component costText = ComponentUtil.literal("-" + recipe.getCraftingCostNutrients(inputInventoryWrapper));
		guiGraphics.drawString(font, costText, 69, 50 - font.lineHeight, ColorStyles.WHITE_ARGB);
	}

}
