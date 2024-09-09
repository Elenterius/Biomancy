package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.crafting.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.crafting.recipe.ItemCountRange;
import com.github.elenterius.biomancy.crafting.recipe.VariableProductionOutput;
import com.github.elenterius.biomancy.init.ModBlocks;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;

public class DecomposerRecipeCategory implements IRecipeCategory<DecomposerRecipe> {

	public static final RecipeType<DecomposerRecipe> RECIPE_TYPE = new RecipeType<>(ModRecipes.DECOMPOSING_RECIPE_TYPE.getId(), DecomposerRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	private final RecipeWrapper inputInventoryWrapper;

	public DecomposerRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DECOMPOSER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/decomposer_recipe.png"), 0, 0, 128, 64).setTextureSize(128, 64).build();

		inputInventoryWrapper = new RecipeWrapper(new ItemStackHandler(DigesterBlockEntity.INPUT_SLOTS));
	}

	@Override
	public RecipeType<DecomposerRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public Component getTitle() {
		return ComponentUtil.translatable("jei.biomancy.recipe.decomposer");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	private void addOutputSlot(IRecipeLayoutBuilder builder, int x, int y, List<VariableProductionOutput> outputs, int index) {
		assert index >= 0;
		assert index < DecomposerRecipe.MAX_OUTPUTS;

		IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);
		if (index < outputs.size()) {
			VariableProductionOutput output = outputs.get(index);
			ItemStack stack = output.getItemStack();
			slotBuilder.addItemStack(stack);
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DecomposerRecipe recipe, IFocusGroup focuses) {
		int x = 51;
		int y0 = 9;
		int y1 = 38;

		builder.addSlot(RecipeIngredientRole.INPUT, 5, y0).addItemStacks(recipe.getIngredientQuantity().getItemsWithCount());

		List<VariableProductionOutput> outputs = recipe.getOutputs();
		addOutputSlot(builder, x, y0, outputs, 0);
		addOutputSlot(builder, x, y1, outputs, 1);
		addOutputSlot(builder, x + 30, y0, outputs, 2);
		addOutputSlot(builder, x + 30, y1, outputs, 3);
		addOutputSlot(builder, x + 30 * 2, y0, outputs, 4);
		addOutputSlot(builder, x + 30 * 2, y1, outputs, 5);
	}

	@Override
	public void draw(DecomposerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;

		IRecipeSlotView slotView = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).get(0);
		ItemStack itemStack = slotView.getDisplayedItemStack().orElse(ItemStack.EMPTY);
		inputInventoryWrapper.setItem(0, itemStack);

		int ticks = recipe.getCraftingTimeTicks(inputInventoryWrapper);
		int seconds = ticks > 0 ? ticks / 20 : 0;
		MutableComponent timeString = ComponentUtil.translatable("gui.jei.category.smelting.time.seconds", seconds);
		guiGraphics.drawString(font, timeString, 16, 59 - font.lineHeight, ColorStyles.WHITE_ARGB);

		MutableComponent costString = ComponentUtil.literal("-" + recipe.getCraftingCostNutrients(inputInventoryWrapper));
		guiGraphics.drawString(font, costString, 16, 43 - font.lineHeight, ColorStyles.WHITE_ARGB);

		int x = 68;
		List<VariableProductionOutput> outputs = recipe.getOutputs();
		drawOutputAmount(font, guiGraphics, x, 26, outputs, 0);
		drawOutputAmount(font, guiGraphics, x, 55, outputs, 1);
		drawOutputAmount(font, guiGraphics, x + 30, 26, outputs, 2);
		drawOutputAmount(font, guiGraphics, x + 30, 55, outputs, 3);
		drawOutputAmount(font, guiGraphics, x + 30 * 2, 26, outputs, 4);
		drawOutputAmount(font, guiGraphics, x + 30 * 2, 55, outputs, 5);
	}

	private void drawOutputAmount(Font font, GuiGraphics guiGraphics, int x, int y, List<VariableProductionOutput> outputs, int index) {
		assert index >= 0;
		assert index < DecomposerRecipe.MAX_OUTPUTS;

		if (index < outputs.size()) {
			VariableProductionOutput output = outputs.get(index);
			if (output.getItemStack().isEmpty()) return;

			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(x, y, 0);
			guiGraphics.pose().scale(0.75f, 0.75f, 1f);
			guiGraphics.pose().translate(-x, -y, 0);

			ItemCountRange countRange = output.getCountRange();
			if (countRange instanceof ItemCountRange.UniformRange uniform) {
				MutableComponent component = ComponentUtil.literal("%d–%d".formatted(uniform.min(), uniform.max()));
				guiGraphics.drawString(font, component, x - font.width(component), y, ColorStyles.WHITE_ARGB);
			}
			else if (countRange instanceof ItemCountRange.ConstantValue constant) {
				MutableComponent component = ComponentUtil.literal("" + constant.value());
				guiGraphics.drawString(font, component, x - font.width(component), y, ColorStyles.WHITE_ARGB);
			}
			else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
				MutableComponent component = ComponentUtil.literal("n=%d p=%s".formatted(binomialRange.n(), binomialRange.p()));
				guiGraphics.drawString(font, component, x - font.width(component), y, ColorStyles.WHITE_ARGB);
			}

			guiGraphics.pose().popPose();
		}
	}

}
