package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.component.CustomEditBox;
import com.github.elenterius.biomancy.client.gui.tooltip.ScreenTooltipStyleProvider;
import com.github.elenterius.biomancy.client.util.ClientSoundUtil;
import com.github.elenterius.biomancy.client.util.GuiRenderUtil;
import com.github.elenterius.biomancy.client.util.GuiUtil;
import com.github.elenterius.biomancy.crafting.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.crafting.recipe.IngredientStack;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.menu.BioForgeMenu;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BioForgeScreen extends AbstractContainerScreen<BioForgeMenu> implements ScreenTooltipStyleProvider {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/menu_bio_forge.png");
	private BioForgeScreenController recipeBook;
	private CustomEditBox searchInput;
	private boolean ignoreTextInput;

	public BioForgeScreen(BioForgeMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		imageWidth = 292;
		imageHeight = 219;
	}

	@Override
	protected void init() {
		super.init();

		searchInput = new CustomEditBox(Objects.requireNonNull(minecraft).font, leftPos + 26, topPos + 16, 78, 14, ComponentUtil.translatable("itemGroup.search"));
		searchInput.setMaxLength(50);
		searchInput.setTextHint(ComponentUtil.translatable("gui.recipebook.search_hint").withStyle(Style.EMPTY.withItalic(true).withColor(ColorStyles.TEXT_ACCENT_FORGE_DARK)));
		searchInput.setTextColor(ColorStyles.TEXT_ACCENT_FORGE);

		recipeBook = new BioForgeScreenController(minecraft, menu);

		//		minecraft.keyboardHandler.setSendRepeatsToGui(true);
	}

	@Override
	public void removed() {
		//		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	public void onRecipeBookUpdated() {
		recipeBook.onRecipeBookUpdated();
	}

	@Override
	protected void containerTick() {
		searchInput.tick();
		recipeBook.tick();
	}

	@Override
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
		super.slotClicked(slot, slotId, mouseButton, type);
		//if (slot != null) {
		//	recipeBook.trackPlayerInvChanges();
		//}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (searchInput.mouseClicked(mouseX, mouseY, button)) return true;

		if (recipeBook.hasRecipesOnPage()) {
			int recipes = recipeBook.getMaxRecipesOnGrid();
			for (int i = 0; i < recipes; i++) {
				int pX = leftPos + 13 + (20 + 5 - 2) * (i % BioForgeScreenController.COLS);
				int pY = topPos + 37 + (20 + 5 - 2) * (i / BioForgeScreenController.COLS);
				if (GuiUtil.isInRect(pX, pY, 24, 24, mouseX, mouseY)) {
					ClientSoundUtil.playUISound(ModSoundEvents.UI_BIO_FORGE_SELECT_RECIPE);
					recipeBook.setSelectedRecipe(i);
					return true;
				}
			}

			if (recipeBook.getMaxPages() > 1) {
				int x = leftPos + 60 + 1;
				int y = topPos + 211 - font.lineHeight * 2 - 2;
				if (recipeBook.hasPrevPage() && GuiUtil.isInRect(x - 22 - 8 - 1, y, 8, 13, mouseX, mouseY)) {
					ClientSoundUtil.playUISound(ModSoundEvents.UI_BUTTON_CLICK);
					recipeBook.goToPrevPage();
					return true;
				}
				if (recipeBook.hasNextPage() && GuiUtil.isInRect(x + 22, y, 8, 13, mouseX, mouseY)) {
					ClientSoundUtil.playUISound(ModSoundEvents.UI_BUTTON_CLICK);
					recipeBook.goToNextPage();
					return true;
				}
			}
		}

		for (int i = 0; i < recipeBook.getTabCount(); i++) {
			int w = 24;
			int h = 32;
			int pX = leftPos - w;
			int pY = topPos + 32 + h * i;
			if (!recipeBook.isActiveTab(i) && GuiUtil.isInRect(pX, pY + 3, w, h - 3, mouseX, mouseY)) {
				ClientSoundUtil.playUISound(ModSoundEvents.UI_BUTTON_CLICK);
				recipeBook.setActiveTab(i);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		ignoreTextInput = false;

		if (searchInput.keyPressed(keyCode, scanCode, modifiers)) {
			recipeBook.updateSearchString(searchInput.getValue().toLowerCase(Locale.ROOT));
			return true;
		}

		if (searchInput.isFocused() && searchInput.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
			return true;
		}

		if (Objects.requireNonNull(minecraft).options.keyChat.matches(keyCode, scanCode) && !searchInput.isFocused()) {
			ignoreTextInput = true;
			searchInput.setFocused(true);
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		ignoreTextInput = false;
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (ignoreTextInput) return false;

		if (searchInput.charTyped(codePoint, modifiers)) {
			recipeBook.updateSearchString(searchInput.getValue().toLowerCase(Locale.ROOT));
			return true;
		}

		return super.charTyped(codePoint, modifiers);
	}

	public void blit(GuiGraphics guiGraphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
		guiGraphics.blit(BACKGROUND_TEXTURE, x, y, 0, uOffset, vOffset, uWidth, vHeight, 512, 256);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		//don't draw any labels
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		blit(guiGraphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		drawFuelBar(guiGraphics);
		drawTabs(guiGraphics);
		drawRecipeIngredients(guiGraphics);
		drawGhostResult(guiGraphics);
		drawRecipes(guiGraphics);

		searchInput.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	private void drawGhostResult(GuiGraphics guiGraphics) {
		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		if (selectedRecipe != null && menu.isResultEmpty()) {
			ItemStack stack = selectedRecipe.getResultItem(null);
			int x = leftPos + 194 + 2;
			int y = topPos + 33 + 2;
			GuiRenderUtil.drawGhostItem(guiGraphics, x, y, stack);
			guiGraphics.renderItemDecorations(font, stack, x, y);
			RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		}
	}

	private void drawTabs(GuiGraphics guiGraphics) {
		for (int index = 0; index < recipeBook.getTabCount(); index++) {
			drawTab(guiGraphics, index, recipeBook.isActiveTab(index), recipeBook.getTabIcon(index));
		}
	}

	private void drawRecipes(GuiGraphics guiGraphics) {
		if (!recipeBook.hasRecipesOnPage()) return;

		//TODO: refactor - move into the loop below
		if (recipeBook.hasSelectedRecipe() && recipeBook.isSelectedRecipeVisible()) {
			int gridIndex = recipeBook.getGridIndexOfSelectedRecipe();
			BioForgeRecipe recipe = recipeBook.getRecipeByGrid(gridIndex);
			boolean isCraftable = recipeBook.getRecipeCollectionByGrid(gridIndex).isCraftable(recipe);
			drawTileSelection(guiGraphics, gridIndex, isCraftable);
		}

		int maxRecipes = recipeBook.getMaxRecipesOnGrid();
		for (int gridIndex = 0; gridIndex < maxRecipes; gridIndex++) {
			BioForgeRecipe recipe = recipeBook.getRecipeByGrid(gridIndex);
			boolean isCraftable = recipeBook.getRecipeCollectionByGrid(gridIndex).isCraftable(recipe);
			drawRecipeTile(guiGraphics, gridIndex, isCraftable, recipe.getResultItem(null));
		}

		drawPagination(guiGraphics);
	}

	private void drawPagination(GuiGraphics guiGraphics) {
		if (recipeBook.getMaxPages() < 2) return;

		int x = leftPos + 60 + 1;
		int y = topPos + 211 - font.lineHeight * 2 - 2;
		int currentPage = recipeBook.getCurrentPage();

		if (currentPage > 1) blit(guiGraphics, x - 22 - 8 - 1, y, 298, 58, 8, 13);
		if (currentPage < recipeBook.getMaxPages()) blit(guiGraphics, x + 22, y, 334, 58, 8, 13);
		String text = "%d/%d".formatted(currentPage, recipeBook.getMaxPages());
		guiGraphics.drawString(font, text, (int) (x - font.width(text) / 2f), y + 3, ColorStyles.TEXT_ACCENT_FORGE);
	}

	private void drawRecipeIngredients(GuiGraphics guiGraphics) {
		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		if (selectedRecipe != null) {
			drawRecipeIngredients(guiGraphics, selectedRecipe);
		}
	}

	private void drawRecipeIngredients(GuiGraphics guiGraphics, BioForgeRecipe recipe) {
		int x = leftPos + 141 + 3;
		int y = topPos + 82 + 3;

		List<IngredientStack> ingredients = recipe.getIngredientQuantities();
		for (int i = 0, size = ingredients.size(); i < size; i++) {
			IngredientStack ingredientStack = ingredients.get(i);
			ItemStack itemStack = ingredientStack.ingredient().getItems()[0];
			drawIngredientQuantity(guiGraphics, itemStack, recipeBook.getTotalItemCountInPlayerInv(itemStack), ingredientStack.count(), x + 26 * i, y);
		}
	}

	private void drawIngredientQuantity(GuiGraphics guiGraphics, ItemStack stack, int currentCount, int requiredCount, int x, int y) {
		boolean insufficient = currentCount < requiredCount;
		blit(guiGraphics, x - 3, y - 3, insufficient ? 354 : 330, 74, 22, 22);
		guiGraphics.renderItem(stack, x, y);
		String text = "x" + requiredCount;
		guiGraphics.drawString(font, text, x + 16 + 4 - font.width(text), y + 16 + 4 + 1, insufficient ? ColorStyles.TEXT_ERROR : ColorStyles.TEXT_SUCCESS);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
	}

	private void drawRecipeTile(GuiGraphics guiGraphics, int idx, boolean isCraftable, ItemStack stack) {
		int x = leftPos + 12 + (20 + 5) * (idx % BioForgeScreenController.COLS);
		int y = topPos + 36 + (20 + 5) * (idx / BioForgeScreenController.COLS);
		drawRecipeTile(guiGraphics, x, y, stack, !isCraftable);
	}

	private void drawRecipeTile(GuiGraphics guiGraphics, int x, int y, ItemStack stack, boolean redOverlay) {
		blit(guiGraphics, x, y, 295, 30, 22, 22);

		if (redOverlay) {
			blit(guiGraphics, x, y, 323, 30, 22, 22);
			guiGraphics.renderItem(stack, x + 3, y + 3);
			RenderSystem.depthFunc(GL11.GL_GREATER);
			guiGraphics.fill(x, y, x + 22, y + 22, 0x40_00_00_00 | ColorStyles.TEXT_ERROR); // 0x40... | color -> prepend alpha to rgb color
			RenderSystem.depthFunc(GL11.GL_LEQUAL);
		}
		else {
			guiGraphics.renderItem(stack, x + 3, y + 3);
		}
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
	}

	private void drawTileSelection(GuiGraphics guiGraphics, int idx, boolean isValid) {
		int x = leftPos + 12 + 25 * (idx % BioForgeScreenController.COLS) - 1;
		int y = topPos + 36 + 25 * (idx / BioForgeScreenController.COLS) - 1;
		blit(guiGraphics, x, y, isValid ? 295 : 321, 2, 24, 24);
	}

	private void drawTab(GuiGraphics guiGraphics, int idx, boolean isActive, ItemStack stack) {
		int w = 24;
		int h = 32;
		int x = leftPos - w + 2;
		int y = topPos + 32 + h * idx;
		blit(guiGraphics, x, y, 297, isActive ? 114 : 78, w, h);
		guiGraphics.renderItem(stack, x + 5, y + 8);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
	}

	private void drawFuelBar(GuiGraphics guiGraphics) {
		float fuelPct = menu.getFuelAmountNormalized();
		int vHeight = (int) (fuelPct * 36) + (fuelPct > 0 ? 1 : 0);
		blit(guiGraphics, leftPos + 144, topPos + 13 + 36 - vHeight, 353, 9 + 36 - vHeight, 5, vHeight);
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			if (drawFuelTooltip(guiGraphics, mouseX, mouseY)) return;
			if (drawRecipeTooltip(guiGraphics, mouseX, mouseY)) return;
			if (drawIngredientsTooltip(guiGraphics, mouseX, mouseY)) return;
		}
		super.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	private boolean drawRecipeTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (recipeBook.hasRecipesOnPage()) {
			int recipes = recipeBook.getMaxRecipesOnGrid();
			for (int idx = 0; idx < recipes; idx++) {
				int x = leftPos + 13 + 25 * (idx % BioForgeScreenController.COLS);
				int y = topPos + 37 + 25 * (idx / BioForgeScreenController.COLS);
				if (GuiUtil.isInRect(x, y, 20, 20, mouseX, mouseY)) {
					guiGraphics.renderTooltip(font, recipeBook.getRecipeByGrid(idx).getResultItem(null), mouseX, mouseY);
					return true;
				}
			}
		}
		return false;
	}

	private boolean drawIngredientsTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		if (selectedRecipe == null) return false;

		for (int i = 0; i < selectedRecipe.getIngredientQuantities().size(); i++) {
			int x = leftPos + 141 + 3 + 26 * i;
			int y = topPos + 82 + 3;
			if (GuiUtil.isInRect(x, y, 20, 20, mouseX, mouseY)) {
				guiGraphics.renderTooltip(font, selectedRecipe.getIngredientQuantities().get(i).ingredient().getItems()[0], mouseX, mouseY);
				return true;
			}
		}
		return false;
	}

	private boolean drawFuelTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (!GuiUtil.isInRect(leftPos + 144, topPos + 13, 5, 36, mouseX, mouseY)) return false;

		int maxFuel = menu.getMaxFuelAmount();
		int fuelAmount = menu.getFuelAmount();

		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		int totalFuelCost = selectedRecipe != null ? selectedRecipe.getCraftingCostNutrients() : 0;

		GuiRenderUtil.drawFuelTooltip(font, guiGraphics, mouseX, mouseY, maxFuel, fuelAmount, totalFuelCost);
		return true;
	}

}
