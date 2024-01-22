package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

import java.util.Objects;

public class DigesterRecipeCategory implements IRecipeCategory<DigesterRecipe> {

	public static final RecipeType<DigesterRecipe> RECIPE_TYPE = new RecipeType<>(ModRecipes.DIGESTING_RECIPE_TYPE.getId(), DigesterRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	public DigesterRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.DIGESTER.get()));
		ResourceLocation texture = BiomancyMod.createRL("textures/gui/jei/digester_recipe.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, 80, 47).setTextureSize(80, 47).addPadding(0, 4, 0, 0).build();
	}

	@Override
	public RecipeType<DigesterRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public Component getTitle() {
		return ComponentUtil.translatable("jei.biomancy.recipe.digester");
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
	public void setRecipe(IRecipeLayoutBuilder builder, DigesterRecipe recipe, IFocusGroup focuses) {
		ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);

		builder.setShapeless();
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 4).addIngredients(recipe.getIngredient());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 5).addItemStack(recipe.getResultItem(level.registryAccess()));
	}

	@Override
	public void draw(DigesterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;

		int ticks = recipe.getCraftingTimeTicks();
		int seconds = ticks > 0 ? ticks / 20 : 0;
		Component timeText = ComponentUtil.translatable("gui.jei.category.smelting.time.seconds", seconds);
		guiGraphics.drawString(font, timeText, 48, 44 - font.lineHeight, ColorStyles.WHITE_ARGB);

		Component costText = ComponentUtil.literal("-" + recipe.getCraftingCostNutrients());
		guiGraphics.drawString(font, costText, 15, 44 - font.lineHeight, ColorStyles.WHITE_ARGB);
	}

}
