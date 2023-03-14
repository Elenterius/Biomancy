package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.ItemCountRange;
import com.github.elenterius.biomancy.recipe.VariableProductionOutput;
import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import com.github.elenterius.biomancy.world.block.decomposer.DecomposerBlockEntity;
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
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DecomposerRecipeCategory implements IRecipeCategory<DecomposerRecipe> {

	public static final RecipeType<DecomposerRecipe> RECIPE_TYPE = new RecipeType<>(ModRecipes.DECOMPOSING_RECIPE_TYPE.getId(), DecomposerRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	public DecomposerRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DECOMPOSER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/decomposer_recipe.png"), 0, 0, 128, 64).setTextureSize(128, 64).build();
	}

	//TODO: Change this code.
	/*@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}*/

	/*@Override
	public Class<? extends DecomposerRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}*/

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
	public void draw(DecomposerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
		Font fontRenderer = Minecraft.getInstance().font;

		int ticks = recipe.getCraftingTime();
		int seconds = ticks > 0 ? ticks / 20 : 0;
		MutableComponent timeString = ComponentUtil.translatable("gui.jei.category.smelting.time.seconds", seconds);
		fontRenderer.draw(poseStack, timeString, 16, 59f - fontRenderer.lineHeight, 0xff808080);

		int fuelCost = NutrientFuelUtil.getFuelCost(DecomposerBlockEntity.BASE_COST, ticks);
		MutableComponent costString = ComponentUtil.literal("-" + fuelCost);
		fontRenderer.draw(poseStack, costString, 16, 43f - fontRenderer.lineHeight, 0xff808080);

		int x = 68;
		List<VariableProductionOutput> outputs = recipe.getOutputs();
		drawOutputAmount(fontRenderer, poseStack, x, 26, outputs, 0);
		drawOutputAmount(fontRenderer, poseStack, x, 55, outputs, 1);
		drawOutputAmount(fontRenderer, poseStack, x + 30, 26, outputs, 2);
		drawOutputAmount(fontRenderer, poseStack, x + 30, 55, outputs, 3);
		drawOutputAmount(fontRenderer, poseStack, x + 30 * 2, 26, outputs, 4);
		drawOutputAmount(fontRenderer, poseStack, x + 30 * 2, 55, outputs, 5);
	}

	private void drawOutputAmount(Font fontRenderer, PoseStack poseStack, int x, int y, List<VariableProductionOutput> outputs, int index) {
		assert index >= 0;
		assert index < DecomposerRecipe.MAX_OUTPUTS;

		if (index < outputs.size()) {
			VariableProductionOutput output = outputs.get(index);
			if (output.getItemStack().isEmpty()) return;

			poseStack.pushPose();
			poseStack.translate(x, y, 0);
			poseStack.scale(0.75f, 0.75f, 1f);
			poseStack.translate(-x, -y, 0);

			ItemCountRange countRange = output.getCountRange();
			if (countRange instanceof ItemCountRange.UniformRange uniform) {
				MutableComponent component = ComponentUtil.literal("%d-%d".formatted(Math.max(uniform.min(), 0), uniform.max()));
				fontRenderer.draw(poseStack, component, x - fontRenderer.width(component), y, 0xff808080);
			}
			else if (countRange instanceof ItemCountRange.ConstantValue constant) {
				MutableComponent component = ComponentUtil.literal("" + constant.value());
				fontRenderer.draw(poseStack, component, x - fontRenderer.width(component), y, 0xff808080);
			}
			else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
				MutableComponent component = ComponentUtil.literal("n: %d, p: %s".formatted(binomialRange.n(), binomialRange.p()));
				fontRenderer.draw(poseStack, component, x - fontRenderer.width(component), y, 0xff808080);
			}

			poseStack.popPose();
		}
	}

}
