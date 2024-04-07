package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.component.CustomEditBox;
import com.github.elenterius.biomancy.client.gui.tooltip.ScreenNutrientFuelConsumer;
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
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BioForgeScreen extends AbstractContainerScreen<BioForgeMenu> implements ScreenTooltipStyleProvider, ScreenNutrientFuelConsumer {

	private static final ResourceLocation BACKGROUND_TEXTURE = BiomancyMod.createRL("textures/gui/menu_bio_forge.png");
	private final TabsHelper tabsHelper = new TabsHelper();
	private final IngredientsHelper ingredientsHelper = new IngredientsHelper();
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
		if (searchInput.mouseClicked(mouseX, mouseY, button)) {
			searchInput.setFocused(true);
			return true;
		}
		else {
			searchInput.setFocused(false);
		}

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

		if (tabsHelper.clickMouse(mouseX, mouseY)) return true;

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

		if (minecraft.options.keyChat.matches(keyCode, scanCode) && !searchInput.isFocused()) {
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

	private float time;

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (!Screen.hasControlDown()) {
			time += partialTick;
		}

		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		blit(guiGraphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		renderFuelBar(guiGraphics);
		tabsHelper.render(guiGraphics, mouseX, mouseY, partialTick);
		ingredientsHelper.render(guiGraphics, mouseX, mouseY, partialTick);
		renderRecipeResult(guiGraphics);
		renderRecipeSelectionGrid(guiGraphics);

		searchInput.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	private void renderRecipeResult(GuiGraphics guiGraphics) {
		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		if (selectedRecipe != null && menu.isResultEmpty()) {
			ItemStack stack = selectedRecipe.getResultItem(minecraft.level.registryAccess());
			int x = leftPos + 194 + 2;
			int y = topPos + 33 + 2;
			GuiRenderUtil.drawGhostItem(guiGraphics, x, y, stack);
			guiGraphics.renderItemDecorations(font, stack, x, y);
		}
	}

	private void renderRecipeSelectionGrid(GuiGraphics guiGraphics) {
		if (!recipeBook.hasRecipesOnPage()) return;

		//TODO: refactor - move into the loop below
		if (recipeBook.hasSelectedRecipe() && recipeBook.isSelectedRecipeVisible()) {
			int gridIndex = recipeBook.getGridIndexOfSelectedRecipe();
			BioForgeRecipe recipe = recipeBook.getRecipeByGrid(gridIndex);
			boolean isCraftable = recipeBook.getRecipeCollectionByGrid(gridIndex).isCraftable(recipe);
			renderTileSelection(guiGraphics, gridIndex, isCraftable);
		}

		int maxRecipes = recipeBook.getMaxRecipesOnGrid();
		for (int gridIndex = 0; gridIndex < maxRecipes; gridIndex++) {
			BioForgeRecipe recipe = recipeBook.getRecipeByGrid(gridIndex);
			boolean isCraftable = recipeBook.getRecipeCollectionByGrid(gridIndex).isCraftable(recipe);
			renderRecipeTile(guiGraphics, gridIndex, isCraftable, recipe.getResultItem(minecraft.level.registryAccess()));
		}

		renderPagination(guiGraphics);
	}

	private void renderPagination(GuiGraphics guiGraphics) {
		if (recipeBook.getMaxPages() < 2) return;

		int x = leftPos + 60 + 1;
		int y = topPos + 211 - font.lineHeight * 2 - 2;
		int currentPage = recipeBook.getCurrentPage();

		if (currentPage > 1) blit(guiGraphics, x - 22 - 8 - 1, y, 298, 58, 8, 13);
		if (currentPage < recipeBook.getMaxPages()) blit(guiGraphics, x + 22, y, 334, 58, 8, 13);
		String text = "%d/%d".formatted(currentPage, recipeBook.getMaxPages());
		guiGraphics.drawString(font, text, (int) (x - font.width(text) / 2f), y + 3, ColorStyles.TEXT_ACCENT_FORGE);
	}

	private void renderRecipeTile(GuiGraphics guiGraphics, int idx, boolean isCraftable, ItemStack stack) {
		int x = leftPos + 12 + (20 + 5) * (idx % BioForgeScreenController.COLS);
		int y = topPos + 36 + (20 + 5) * (idx / BioForgeScreenController.COLS);
		renderRecipeTile(guiGraphics, x, y, stack, !isCraftable);
	}

	private void renderRecipeTile(GuiGraphics guiGraphics, int x, int y, ItemStack stack, boolean redOverlay) {
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

	private void renderTileSelection(GuiGraphics guiGraphics, int idx, boolean isValid) {
		int x = leftPos + 12 + 25 * (idx % BioForgeScreenController.COLS) - 1;
		int y = topPos + 36 + 25 * (idx / BioForgeScreenController.COLS) - 1;
		blit(guiGraphics, x, y, isValid ? 295 : 321, 2, 24, 24);
	}

	private void renderFuelBar(GuiGraphics guiGraphics) {
		float fuelPct = menu.getFuelAmountNormalized();
		int vHeight = (int) (fuelPct * 36) + (fuelPct > 0 ? 1 : 0);
		blit(guiGraphics, leftPos + 144, topPos + 13 + 36 - vHeight, 353, 9 + 36 - vHeight, 5, vHeight);
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			if (tabsHelper.renderTooltip(guiGraphics, mouseX, mouseY)) return;
			if (renderFuelTooltip(guiGraphics, mouseX, mouseY)) return;
			if (renderRecipeSelectionGridTooltip(guiGraphics, mouseX, mouseY)) return;
			if (ingredientsHelper.renderTooltip(guiGraphics, mouseX, mouseY)) return;
		}
		super.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	private boolean renderRecipeSelectionGridTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (recipeBook.hasRecipesOnPage()) {
			int maxIndex = recipeBook.getMaxRecipesOnGrid();

			int minX = leftPos + 13;
			int minY = topPos + 37;
			int maxX = leftPos + 13 + 25 * BioForgeScreenController.COLS - 5;
			int maxY = topPos + 37 + 25 * Mth.ceil(maxIndex / (float) BioForgeScreenController.COLS) - 5;
			if (!GuiUtil.isInRectAB(minX, minY, maxX, maxY, mouseX, mouseY)) return false;

			for (int index = 0; index < maxIndex; index++) {
				int x = leftPos + 13 + 25 * (index % BioForgeScreenController.COLS);
				int y = topPos + 37 + 25 * (index / BioForgeScreenController.COLS);
				if (GuiUtil.isInRect(x, y, 20, 20, mouseX, mouseY)) {
					guiGraphics.renderTooltip(font, recipeBook.getRecipeByGrid(index).getResultItem(minecraft.level.registryAccess()), mouseX, mouseY);
					return true;
				}
			}
		}
		return false;
	}


	private boolean renderFuelTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (!GuiUtil.isInRect(leftPos + 144, topPos + 13, 5, 36, mouseX, mouseY)) return false;

		int maxFuel = menu.getMaxFuelAmount();
		int fuelAmount = menu.getFuelAmount();

		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		int totalFuelCost = selectedRecipe != null ? selectedRecipe.getCraftingCostNutrients() : 0;

		GuiRenderUtil.drawFuelTooltip(font, guiGraphics, mouseX, mouseY, maxFuel, fuelAmount, totalFuelCost);
		return true;
	}

	private final class IngredientsHelper implements Renderable {

		private static final int X_OFFSET = 141 + 3;
		private static final int Y_OFFSET = 82 + 3;

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			BioForgeRecipe recipe = recipeBook.getSelectedRecipe();
			if (recipe == null) return;

			int x = leftPos + X_OFFSET;
			int y = topPos + Y_OFFSET;

			List<IngredientStack> ingredients = recipe.getIngredientQuantities();
			for (int i = 0; i < ingredients.size(); i++) {
				IngredientStack ingredientStack = ingredients.get(i);
				ItemStack[] items = ingredientStack.getItems();
				ItemStack itemStack = items[Mth.floor(time / 30f) % items.length];

				boolean isSufficientCount = recipeBook.hasSufficientIngredientCount(ingredientStack);

				renderItemWithQuantity(guiGraphics, itemStack, isSufficientCount, ingredientStack.count(), x + 26 * i, y);
			}
		}

		private void renderItemWithQuantity(GuiGraphics guiGraphics, ItemStack stack, boolean isSufficientCount, int requiredCount, int x, int y) {
			blit(guiGraphics, x - 3, y - 3, isSufficientCount ? 330 : 354, 74, 22, 22);
			guiGraphics.renderItem(stack, x, y);
			String text = "x" + requiredCount;
			guiGraphics.drawString(font, text, x + 16 + 4 - font.width(text), y + 16 + 4 + 1, isSufficientCount ? ColorStyles.TEXT_SUCCESS : ColorStyles.TEXT_ERROR);
		}

		private boolean renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
			BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
			if (selectedRecipe == null) return false;

			List<IngredientStack> ingredients = selectedRecipe.getIngredientQuantities();
			for (int i = 0; i < ingredients.size(); i++) {
				int x = leftPos + X_OFFSET + 26 * i;
				int y = topPos + Y_OFFSET;

				if (GuiUtil.isInRect(x, y, 20, 20, mouseX, mouseY)) {
					ItemStack[] items = ingredients.get(i).ingredient().getItems();
					ItemStack itemStack = items[Mth.floor(time / 30f) % items.length];

					guiGraphics.renderTooltip(font, itemStack, mouseX, mouseY);
					return true;
				}
			}
			return false;
		}

	}

	private final class TabsHelper implements Renderable {
		private static final int WIDTH = 24;
		private static final int HEIGHT = 32;
		private static final int Y_OFFSET = 7;

		private boolean clickMouse(double mouseX, double mouseY) {
			for (int i = 0; i < recipeBook.getTabCount(); i++) {
				int pX = leftPos - WIDTH;
				int pY = topPos + Y_OFFSET + HEIGHT * i;
				if (!recipeBook.isActiveTab(i) && GuiUtil.isInRect(pX, pY + 3, WIDTH, HEIGHT - 3, mouseX, mouseY)) {
					ClientSoundUtil.playUISound(ModSoundEvents.UI_BUTTON_CLICK);
					recipeBook.setActiveTab(i);
					return true;
				}
			}

			return false;
		}

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			for (int index = 0; index < recipeBook.getTabCount(); index++) {
				renderTab(guiGraphics, index, recipeBook.isActiveTab(index), recipeBook.getTab(index).getIcon());
			}
		}

		private void renderTab(GuiGraphics guiGraphics, int index, boolean isActive, ItemStack stack) {
			int x = leftPos - WIDTH + 2;
			int y = topPos + Y_OFFSET + HEIGHT * index;
			blit(guiGraphics, x, y, 297, isActive ? 114 : 78, WIDTH, HEIGHT);
			guiGraphics.renderItem(stack, x + 5, y + 8);
		}

		private boolean renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
			int minX = leftPos - WIDTH + 2;
			int minY = topPos + Y_OFFSET;
			int maxX = leftPos;
			int maxY = topPos + Y_OFFSET + HEIGHT * recipeBook.getTabCount();

			if (!GuiUtil.isInRectAB(minX, minY, maxX, maxY, mouseX, mouseY)) return false;

			for (int index = 0; index < recipeBook.getTabCount(); index++) {
				int x = leftPos - WIDTH + 2;
				int y = topPos + Y_OFFSET + HEIGHT * index;
				if (GuiUtil.isInRect(x, y, WIDTH, HEIGHT, mouseX, mouseY)) {
					guiGraphics.renderTooltip(font, ComponentUtil.translatable(recipeBook.getTab(index).translationKey()), mouseX, mouseY);
					return true;
				}
			}

			return false;
		}

	}

}
