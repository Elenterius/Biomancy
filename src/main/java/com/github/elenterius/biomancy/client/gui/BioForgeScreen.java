package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.recipe.BioForgeCategory;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.recipe.IngredientQuantity;
import com.github.elenterius.biomancy.world.block.entity.BioForgeBlockEntity;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BioForgeScreen extends AbstractContainerScreen<BioForgeMenu> {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/menu_bio_forge.png");
	private static final ResourceLocation BACKGROUND_LEFT_TEXTURE = BiomancyMod.createRL("textures/gui/menu_recipe_selector.png");

	public static final int GRID_SIZE = 4 * 5;
	public static final int X_OFFSET = 176;

	record RecipeSelection(@Nullable BioForgeRecipe recipe, int tab, int index) {
		@Nullable
		public ResourceLocation getRecipeId() {
			return recipe != null ? recipe.getId() : null;
		}
	}

	private List<BioForgeRecipe> recipes = List.of();
	private final List<BioForgeCategory> tabs = BioForgeCategory.getCategories().stream().toList();
	private boolean setupComplete = false;

	private RecipeSelection recipeSelection = new RecipeSelection(null, -1, -1);
	private int startIndex = 0;
	private int activeTab = 0;

	private boolean scrolling = false;
	private float scrollPct = 0;

	public BioForgeScreen(BioForgeMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		imageHeight = 219;
		imageWidth = 176 * 2;
	}

	private int ticks;

	@Override
	protected void containerTick() {
		if (!setupComplete) setupComplete = setupRecipes();

		if (minecraft == null || minecraft.level == null) return;

		if (ticks++ % 8 == 0) {
			ResourceLocation currentRecipeId = recipeSelection.getRecipeId();
			ResourceLocation newRecipeId = menu.getSelectedRecipeId();
			if (currentRecipeId != newRecipeId) {
				if (newRecipeId == null) {
					recipeSelection = new RecipeSelection(null, -1, -1);
					return;
				}

				if (!newRecipeId.equals(currentRecipeId)) {

					BioForgeRecipe newRecipe = BioForgeBlockEntity.RECIPE_TYPE.getRecipeById(minecraft.level, newRecipeId).orElse(null);
					if (newRecipe == null) {
						recipeSelection = new RecipeSelection(null, -1, -1);
						return;
					}

					int tabIndex = getTabIndex(newRecipe.getCategory());
					int selectionIndex = -1;
					if (tabIndex == activeTab) {
						for (int i = 0; i < recipes.size(); i++) {
							if (recipes.get(i) == newRecipe) {
								selectionIndex = i;
								break;
							}
						}
					}
					recipeSelection = new RecipeSelection(newRecipe, tabIndex, selectionIndex);
					return;
				}
			}

			fixSelectionIndex();
		}
	}

	private void fixSelectionIndex() {
		if (recipeSelection.tab == activeTab && recipeSelection.recipe != null && recipeSelection.index == -1) {
			BioForgeRecipe recipe = recipeSelection.recipe;
			for (int i = 0; i < recipes.size(); i++) {
				if (recipes.get(i) == recipe) {
					recipeSelection = new RecipeSelection(recipe, activeTab, i);
					break;
				}
			}
		}
	}

	private int getTabIndex(BioForgeCategory category) {
		for (int tabIndex = 0; tabIndex < tabs.size(); tabIndex++) {
			if (tabs.get(tabIndex) == category) {
				return tabIndex;
			}
		}
		return 0;
	}

	private boolean setupRecipes() {
		if (minecraft != null && minecraft.level != null) {
			RecipeManager recipeManager = minecraft.level.getRecipeManager();
			final BioForgeCategory category = tabs.get(activeTab);
			recipes = recipeManager.byType(ModRecipes.BIO_FORGING_RECIPE_TYPE).values().stream().map(BioForgeRecipe.class::cast).filter(recipe -> recipe.getCategory() == category).toList();
			return true;
		}
		return false;
	}

	private void setSelectedRecipe(int idx) {
		int selectionIndex = startIndex + idx;
		BioForgeRecipe recipe = recipes.get(selectionIndex);
		recipeSelection = new RecipeSelection(recipe, activeTab, selectionIndex);

		if (minecraft != null && minecraft.gameMode != null) {
			ModNetworkHandler.sendBioForgeRecipeToServer(menu.containerId, recipe);
		}
	}

	private void setActiveTab(int idx) {
		activeTab = idx;
		resetScrollbar();
		setupRecipes();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		scrolling = GuiUtil.isInRect(leftPos + 157, topPos + 22, 6, 91, mouseX, mouseY);

		if (!recipes.isEmpty()) {
			for (int idx = 0; idx < GRID_SIZE && idx + startIndex < recipes.size(); idx++) {
				int pX = leftPos + 27 + (24 + 1) * (idx % 5);
				int pY = topPos + 18 + (24 + 1) * (idx / 5);
				if (GuiUtil.isInRect(pX, pY, 25, 25, mouseX, mouseY)) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1f));
					setSelectedRecipe(idx);
					return true;
				}
			}
		}

		for (int idx = 0; idx < tabs.size(); idx++) {
			int pX = leftPos - 19;
			int pY = topPos + 4 + 22 * idx;
			if (idx != activeTab && GuiUtil.isInRect(pX, pY, 19, 20, mouseX, mouseY)) {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
				setActiveTab(idx);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (scrolling) {
			int pY = topPos + 22;
			scrollPct = Mth.clamp((float) ((mouseY - pY) / 91d), 0f, 1f);

			int hiddenRows = Mth.ceil((recipes.size() - GRID_SIZE) / 5f);
			startIndex = hiddenRows > 0 ? Mth.floor(scrollPct * hiddenRows) * 5 : 0;
			return true;
		}

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (!recipes.isEmpty()) {
			int hiddenRows = Mth.ceil((recipes.size() - GRID_SIZE) / 5f);
			if (hiddenRows > 0) {
				float stepSize = 1f / hiddenRows;
				scrollPct = Mth.clamp((float) (scrollPct - delta * stepSize), 0f, 1f);
//				int maxIndex = Math.max(recipes.size() - GRID_SIZE, 0);
//				startIndex = Mth.clamp((int) (startIndex - delta * 5), 0, maxIndex);
				startIndex = Mth.floor(scrollPct * hiddenRows) * 5;
			}
			else {
				resetScrollbar();
			}
		}

		return true;
	}

	private void resetScrollbar() {
		scrolling = false;
		startIndex = 0;
		scrollPct = 0;
	}

	private void drawScrollbar(PoseStack poseStack) {
		drawScrollThumb(poseStack, scrollPct);
	}

	private void drawScrollThumb(PoseStack poseStack, float scrollPct) {
		int y = (int) (scrollPct * (91 - 14));
		blit(poseStack, leftPos + 157, topPos + 22 + y, 221, 0, 6, 14);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		int posX = imageWidth / 2 - font.width(title) / 2;
		font.draw(poseStack, title, posX, -12, 0xFFFFFF);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTick);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		blit(poseStack, leftPos + X_OFFSET, topPos, 0, 0, X_OFFSET, imageHeight);

		drawProgressBar(poseStack, menu.getCraftingProgressNormalized());
		drawFuelBar(poseStack, menu.getFuelAmountNormalized());

		RenderSystem.setShaderTexture(0, BACKGROUND_LEFT_TEXTURE);
		blit(poseStack, leftPos, topPos, 0, 0, X_OFFSET, imageHeight);
		drawScrollbar(poseStack);
		drawTabs(poseStack);
		drawRecipes(poseStack);
		drawGhostResult(poseStack);
	}

	private void drawGhostResult(PoseStack poseStack) {
		if (recipeSelection.recipe != null && menu.isOutputEmpty()) {
			ItemStack stack = recipeSelection.recipe.getResultItem();
			int pX = leftPos + 80 + X_OFFSET;
			int pY = topPos + 19;
			itemRenderer.renderAndDecorateFakeItem(stack, pX, pY);
			RenderSystem.depthFunc(516);
			GuiComponent.fill(poseStack, pX, pY, pX + 16, pY + 16, 0x30_ff_ff_ff);
			RenderSystem.depthFunc(515);
//			if (i == 0) {
//				itemRenderer.renderGuiItemDecorations(font, stack, j, k);
//			}
		}
	}

	private void drawTabs(PoseStack poseStack) {
		for (int i = 0; i < tabs.size(); i++) {
			drawTab(poseStack, i, tabs.get(i).icon());
		}
	}

	private void drawRecipes(PoseStack poseStack) {
		if (recipes.isEmpty()) return;

		for (int i = 0; i < GRID_SIZE && startIndex + i < recipes.size(); i++) {
			BioForgeRecipe recipe = recipes.get(startIndex + i);
			drawRecipeTile(poseStack, i, true, recipe.getResultItem());
		}

		if (recipeSelection.recipe != null) {
			if (recipeSelection.tab == activeTab && recipeSelection.index >= startIndex && recipeSelection.index < startIndex + GRID_SIZE) {
				drawSelectedTileHighlight(poseStack, recipeSelection.index - startIndex);
			}
			drawRecipeIngredients(poseStack, recipeSelection.recipe);
		}
	}

	private void drawRecipeIngredients(PoseStack poseStack, BioForgeRecipe recipe) {
		int pX = leftPos + 8 + 16;
		int pY = topPos + 134 + 8;

		Ingredient reactant = recipe.getReactant();
		if (reactant.isEmpty()) {
			drawIngredientQuantity(poseStack, ItemStack.EMPTY, 0, 0, pX, pY);
		}
		else {
			drawIngredientQuantity(poseStack, reactant.getItems()[0], 1, 1, pX, pY);
		}

		int i = 1;
		for (IngredientQuantity ingredientQuantity : recipe.getIngredientQuantities()) {
			drawIngredientQuantity(poseStack, ingredientQuantity.ingredient().getItems()[0], ingredientQuantity.count(), ingredientQuantity.count(), pX + 70 * (i / 3), pY + (18 + 3) * (i % 3));
			i++;
		}
	}

	private void drawIngredientQuantity(PoseStack poseStack, ItemStack stack, int currentCount, int requiredCount, int pX, int pY) {
		boolean flag = currentCount < requiredCount;
		RenderSystem.setShaderTexture(0, BACKGROUND_LEFT_TEXTURE);
		blit(poseStack, pX - 1, pY - 1, flag ? 224 : 203, 72, 18, 18);
		itemRenderer.renderGuiItem(stack, pX, pY);
		font.draw(poseStack, currentCount + "/" + requiredCount, pX + 18 + 2, pY + 18 + 1 - font.lineHeight, flag ? 0xff5555 : 0xFFFFFF);
	}

	private void drawRecipeTile(PoseStack poseStack, int idx, boolean green, ItemStack stack) {
		RenderSystem.setShaderTexture(0, BACKGROUND_LEFT_TEXTURE);
		int pX = leftPos + 27 + (24 + 1) * (idx % 5);
		int pY = topPos + 18 + (24 + 1) * (idx / 5);
		blit(poseStack, pX, pY, 177, green ? 51 : 26, 24, 24);
		itemRenderer.renderAndDecorateItem(stack, pX + 4, pY + 4);
	}

	private void drawSelectedTileHighlight(PoseStack poseStack, int idx) {
		int pX = leftPos + 27 + (24 + 1) * (idx % 5);
		int pY = topPos + 18 + (24 + 1) * (idx / 5);
		RenderSystem.setShaderTexture(0, BACKGROUND_LEFT_TEXTURE);
		blit(poseStack, pX, pY, 203, 26, 24, 24);
	}

	private void drawTab(PoseStack poseStack, int idx, ItemStack stack) {
		RenderSystem.setShaderTexture(0, BACKGROUND_LEFT_TEXTURE);

		if (idx == activeTab) {
			int w = 22;
			int h = 22;
			int pX = leftPos - w;
			int pY = topPos + 4 + 22 * idx - 1;
			blit(poseStack, pX, pY, 198, 0, w, h);
			itemRenderer.renderGuiItem(stack, pX + 3, pY + 3);
		}
		else {
			int w = 19;
			int h = 20;
			int pX = leftPos - w;
			int pY = topPos + 4 + 22 * idx;
			blit(poseStack, pX, pY, 177, 0, w, h);
			itemRenderer.renderGuiItem(stack, pX + 2, pY + 2);
		}
	}

	private void drawProgressBar(PoseStack poseStack, float craftingPct) {
		int vHeight = (int) (craftingPct * 22) + (craftingPct > 0 ? 1 : 0);
		blit(poseStack, leftPos + 86 + X_OFFSET, topPos + 41 + 22 - vHeight, 195, 22 - vHeight, 4, vHeight);
	}

	private void drawFuelBar(PoseStack poseStack, float fuelPct) {
		//fuel blob
		int vHeight = (int) (fuelPct * 18) + (fuelPct > 0 ? 1 : 0);
		blit(poseStack, leftPos + 41 + X_OFFSET, topPos + 31 + 18 - vHeight, 176, 18 - vHeight, 18, vHeight);
		//glass highlight
		blit(poseStack, leftPos + 44 + X_OFFSET, topPos + 34, 177, 20, 12, 13);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			if (GuiUtil.isInRect(leftPos + 41 + X_OFFSET, topPos + 52 - 20, 17, 17, mouseX, mouseY)) {
				drawFuelTooltip(poseStack, mouseX, mouseY);
				return;
			}

			if (!recipes.isEmpty()) {
				//recipe grid
				for (int idx = 0; idx < GRID_SIZE && idx + startIndex < recipes.size(); idx++) {
					int pX = leftPos + 27 + (24 + 1) * (idx % 5);
					int pY = topPos + 18 + (24 + 1) * (idx / 5);
					if (GuiUtil.isInRect(pX, pY, 25, 25, mouseX, mouseY)) {
						drawRecipeTooltip(poseStack, mouseX, mouseY, idx);
						return;
					}
				}

				//selected recipe ingredients
				if (recipeSelection.recipe != null) {
					for (int i = 0; i < recipeSelection.recipe.getIngredientQuantities().size() + 1; i++) {
						int pX = leftPos + 8 + 16 + 70 * (i / 3);
						int pY = topPos + 134 + 8 + (18 + 3) * (i % 3);
						if (GuiUtil.isInRect(pX, pY, 18, 18, mouseX, mouseY)) {
							if (i == 0) {
								Ingredient reactant = recipeSelection.recipe.getReactant();
								if (!reactant.isEmpty()) renderTooltip(poseStack, reactant.getItems()[0], mouseX, mouseY);
								return;
							}

							renderTooltip(poseStack, recipeSelection.recipe.getIngredientQuantities().get(i - 1).ingredient().getItems()[0], mouseX, mouseY);
						}
					}
				}
			}

		}

		super.renderTooltip(poseStack, mouseX, mouseY);
	}

	private void drawRecipeTooltip(PoseStack poseStack, int mouseX, int mouseY, int idx) {
		renderTooltip(poseStack, recipes.get(startIndex + idx).getResultItem(), mouseX, mouseY);
	}

	private void drawFuelTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		int maxFuel = BioForgeBlockEntity.MAX_FUEL;
		int fuelAmount = menu.getFuelAmount();
		int totalFuelCost = menu.getTotalFuelCost();
		GuiUtil.drawFuelTooltip(this, poseStack, mouseX, mouseY, maxFuel, fuelAmount, totalFuelCost);
	}

}
