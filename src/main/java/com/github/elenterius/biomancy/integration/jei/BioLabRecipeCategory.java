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

public class BioLabRecipeCategory implements IRecipeCategory<BioLabRecipe> {

	public static final ResourceLocation ID = BiomancyMod.createRL("jei_" + ModRecipes.BIO_BREWING_RECIPE_TYPE.getId());

	private final IDrawable background;
	private final IDrawable icon;

	public BioLabRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModItems.GLASS_VIAL.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/bio_lab_jei_gui.png"), 0, 0, 116, 54).setTextureSize(116, 54).addPadding(0, 4, 0, 0).build();
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
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}

	private static final int OUTPUT_SLOT = 0;
	private static final int INPUT_SLOT = 1;

	@Override
	public void setRecipe(IRecipeLayout layout, BioLabRecipe recipe, IIngredients ingredients) {
		layout.setShapeless();

		IGuiItemStackGroup guiISGroup = layout.getItemStacks();
		guiISGroup.init(OUTPUT_SLOT, false, 94, 18);

		int index = INPUT_SLOT;
		for (int x = 0; x < 3; x++) {
			guiISGroup.init(index++, true, x * 18, 9);
		}
		for (int x = 0; x < 2; x++) {
			guiISGroup.init(index++, true, x * 18, 9 + 18);
		}

		guiISGroup.set(ingredients);
	}

	@Override
	public void draw(BioLabRecipe recipe, PoseStack poseStack, double mouseX, double mouseY) {
		int ticks = recipe.getCraftingTime();
		if (ticks > 0) {
			int seconds = ticks / 20;
			int mutagenCost = ticks * BioLabBlockEntity.FUEL_COST;
			Component timeText = new TranslatableComponent("gui.jei.category.smelting.time.seconds", seconds);
			Component costText = new TextComponent("+" + mutagenCost + " ").append(new TranslatableComponent("tooltip.biomancy.bile"));
			Font fontRenderer = Minecraft.getInstance().font;
			fontRenderer.draw(poseStack, timeText, (float) background.getWidth() - fontRenderer.width(timeText), 42, 0xff808080);
			fontRenderer.draw(poseStack, costText, 0, 48, 0xff808080);
		}
	}
}
