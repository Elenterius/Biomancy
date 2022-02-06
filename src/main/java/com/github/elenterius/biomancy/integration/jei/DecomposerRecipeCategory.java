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
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DecomposerRecipeCategory implements IRecipeCategory<DecomposerRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.DECOMPOSING_RECIPE_TYPE);
	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private final IDrawable background;
	private final IDrawable icon;

	public DecomposerRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.DECOMPOSER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/decomposer_jei_gui.png"), 0, 0, 162, 60).setTextureSize(162, 60).build();
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends DecomposerRecipe> getRecipeClass() {
		return DecomposerRecipe.class;
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

	@Override
	public void setIngredients(DecomposerRecipe recipe, IIngredients ingredients) {
//		if (recipe.getIngredientCount() > 1) {
//			List<List<ItemStack>> inputs = List.of(new ArrayList<>());
//			List<ItemStack> slot = inputs.get(0);
//			ItemStack[] possibleStacks = recipe.getIngredient().getItems();
//			for (ItemStack stack : possibleStacks) {
//				ItemStack copy = stack.copy();
//				copy.setCount(recipe.getIngredientCount());
//				slot.add(copy);
//			}
//			ingredients.setInputLists(VanillaTypes.ITEM, inputs);
//		}
//		else {
//			ingredients.setInputIngredients(recipe.getIngredients());
//		}

		ingredients.setInputIngredients(List.of(recipe.getIngredient()));
		int ingredientCount = recipe.getIngredientCount();
		if (ingredientCount > 1) {
			for (List<ItemStack> list : ingredients.getInputs(VanillaTypes.ITEM)) {
				for (ItemStack stack : list) {
					stack.setCount(ingredientCount);
				}
			}
		}

		List<ItemStack> outputs = new ArrayList<>();
		for (VariableProductionOutput output : recipe.getOutputs()) {
			outputs.add(output.getItemStack());
		}
		for (VariableProductionOutput byproducts : recipe.getByproducts()) {
			outputs.add(byproducts.getItemStack());
		}
		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, DecomposerRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(INPUT_SLOT, true, 9, 11);

		guiISGroup.init(OUTPUT_SLOT, false, 62, 11);
		guiISGroup.init(OUTPUT_SLOT + 1, false, 62 + 18, 11);
		guiISGroup.init(OUTPUT_SLOT + 2, false, 62, 29);
		guiISGroup.init(OUTPUT_SLOT + 3, false, 62 + 18, 29);

		guiISGroup.init(OUTPUT_SLOT + 4, false, 116, 11);
		guiISGroup.init(OUTPUT_SLOT + 5, false, 116 + 18, 11);
		guiISGroup.init(OUTPUT_SLOT + 6, false, 116, 29);
		guiISGroup.init(OUTPUT_SLOT + 7, false, 116 + 18, 29);

		guiISGroup.set(ingredients);

		guiISGroup.addTooltipCallback((index, input, stack, tooltip) -> {
			if (index >= OUTPUT_SLOT && index <= OUTPUT_SLOT + 3 && !stack.isEmpty()) {
				int idx = index - OUTPUT_SLOT;
				if (idx < recipe.getOutputs().size()) {
					VariableProductionOutput output = recipe.getOutputs().get(idx);
					ItemCountRange countRange = output.getCountRange();
					if (countRange instanceof ItemCountRange.UniformRange uniform) {
						tooltip.add(new TextComponent(uniform.min() + "-" + uniform.max()).withStyle(ChatFormatting.GRAY));
					}
					else if (countRange instanceof ItemCountRange.ConstantValue constant) {
						tooltip.add(new TextComponent(constant.value() + "").withStyle(ChatFormatting.GRAY));
					}
					else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
						tooltip.add(new TextComponent("n: " + binomialRange.n() + ", p: " + binomialRange.p()).withStyle(ChatFormatting.GRAY));
					}
				}
				return;
			}

			if (index >= OUTPUT_SLOT + 4 && index <= OUTPUT_SLOT + 7 && !stack.isEmpty()) {
				int idx = index - OUTPUT_SLOT - 4;
				if (idx < recipe.getOutputs().size()) {
					VariableProductionOutput byproduct = recipe.getByproducts().get(idx);
					ItemCountRange countRange = byproduct.getCountRange();
					if (countRange instanceof ItemCountRange.UniformRange uniform) {
						tooltip.add(new TextComponent(uniform.min() + "-" + uniform.max()).withStyle(ChatFormatting.GRAY));
					}
					else if (countRange instanceof ItemCountRange.ConstantValue constant) {
						tooltip.add(new TextComponent(constant.value() + "").withStyle(ChatFormatting.GRAY));
					}
					else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
						tooltip.add(new TextComponent("n: " + binomialRange.n() + ", p: " + binomialRange.p()).withStyle(ChatFormatting.GRAY));
					}
				}
			}
		});
	}

	@Override
	public void draw(DecomposerRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int fuelCost = ticks * DecomposerBlockEntity.FUEL_COST;
			int seconds = ticks / 20;
			Font fontRenderer = Minecraft.getInstance().font;
			TranslatableComponent timeString = new TranslatableComponent("gui.jei.category.smelting.time.seconds", seconds);
			TextComponent costString = new TextComponent("" + fuelCost);
			float pY = (float) background.getHeight() - fontRenderer.lineHeight;
			fontRenderer.draw(matrixStack, timeString, (background.getWidth() - fontRenderer.width(timeString)), pY, 0xff808080);
			fontRenderer.draw(matrixStack, costString, 9, pY - fontRenderer.lineHeight, 0xff808080);
		}
	}
}
