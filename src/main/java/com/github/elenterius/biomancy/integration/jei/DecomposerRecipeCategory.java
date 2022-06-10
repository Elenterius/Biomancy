package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.ItemCountRange;
import com.github.elenterius.biomancy.recipe.VariableProductionOutput;
import com.github.elenterius.biomancy.world.block.entity.DecomposerBlockEntity;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Arrays;
import java.util.List;

public class DecomposerRecipeCategory implements IRecipeCategory<DecomposerRecipe> {

	public static final RecipeType<DecomposerRecipe> RECIPE_TYPE = new RecipeType<>(BiomancyMod.createRL(ModRecipes.DECOMPOSING_RECIPE_TYPE.getId()), DecomposerRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	public DecomposerRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.DECOMPOSER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/decomposer_recipe.png"), 0, 0, 126, 60).setTextureSize(126, 60).build();
	}

	@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	@Override
	public Class<? extends DecomposerRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}

	@Override
	public RecipeType<DecomposerRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("jei.biomancy.recipe.decomposer");
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
			ItemCountRange countRange = output.getCountRange();
			ItemStack stack = output.getItemStack();
			if (countRange instanceof ItemCountRange.ConstantValue constant) stack.setCount(constant.value());

			slotBuilder.addItemStack(stack);
			slotBuilder.addTooltipCallback((recipeSlotView, tooltip) -> {
				tooltip.add(TextComponent.EMPTY);
				tooltip.add(new TextComponent("Item Count"));
				if (countRange instanceof ItemCountRange.UniformRange uniform) {
					tooltip.add(new TextComponent(" uniform(min: %d, max: %d)".formatted(uniform.min(), uniform.max())).withStyle(ChatFormatting.GRAY));
				} else if (countRange instanceof ItemCountRange.ConstantValue constant) {
					tooltip.add(new TextComponent(" constant value: %d".formatted(constant.value())).withStyle(ChatFormatting.GRAY));
				} else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
					tooltip.add(new TextComponent(" binomial(n: %d, p: %s)".formatted(binomialRange.n(), binomialRange.p())).withStyle(ChatFormatting.GRAY));
				}
			});
		}
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DecomposerRecipe recipe, IFocusGroup focuses) {
		int posY = 12;
		int ingredientCount = recipe.getIngredientCount();
		List<ItemStack> ingredients = ingredientCount == 1 ? List.of(recipe.getIngredient().getItems()) : Arrays.stream(recipe.getIngredient().getItems()).map(stack -> ItemHandlerHelper.copyStackWithSize(stack, ingredientCount)).toList();
		builder.addSlot(RecipeIngredientRole.INPUT, 10, posY).addItemStacks(ingredients);

		int posX = 63;
		List<VariableProductionOutput> outputs = recipe.getOutputs();
		addOutputSlot(builder, posX, posY, outputs, 0);
		addOutputSlot(builder, posX, posY + 18, outputs, 1);
		addOutputSlot(builder, posX + 18, posY, outputs, 2);
		addOutputSlot(builder, posX + 18, posY + 18, outputs, 3);
		addOutputSlot(builder, posX + 18 * 2, posY, outputs, 4);
		addOutputSlot(builder, posX + 18 * 2, posY + 18, outputs, 5);
	}

	@Override
	public void draw(DecomposerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int fuelCost = ticks * DecomposerBlockEntity.FUEL_COST;
			int seconds = ticks / 20;
			Font fontRenderer = Minecraft.getInstance().font;
			TranslatableComponent timeString = new TranslatableComponent("gui.jei.category.smelting.time.seconds", seconds);
			TextComponent costString = new TextComponent("" + fuelCost);
			float pY = (float) background.getHeight() - fontRenderer.lineHeight;
			fontRenderer.draw(poseStack, timeString, (background.getWidth() - fontRenderer.width(timeString)), pY, 0xff808080);
			fontRenderer.draw(poseStack, costString, 9, pY - fontRenderer.lineHeight, 0xff808080);
		}
	}

}
