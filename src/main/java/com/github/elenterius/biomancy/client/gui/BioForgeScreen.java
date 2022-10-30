package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.component.CustomEditBox;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.recipe.IngredientStack;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class BioForgeScreen extends AbstractContainerScreen<BioForgeMenu> {

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

		searchInput = new CustomEditBox(Objects.requireNonNull(minecraft).font, leftPos + 26, topPos + 16, 78, 14, new TranslatableComponent("itemGroup.search"));
		searchInput.setMaxLength(50);
		searchInput.setTextHint(new TranslatableComponent("gui.recipebook.search_hint").withStyle(Style.EMPTY.withItalic(true).withColor(ColorStyles.TEXT_ACCENT_FORGE_DARK)));
		searchInput.setTextColor(ColorStyles.TEXT_ACCENT_FORGE);

		recipeBook = new BioForgeScreenController(minecraft, menu);

		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		SoundUtil.playUISound(ModSoundEvents.UI_MENU_OPEN);
	}

	@Override
	public void removed() {
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
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
					SoundUtil.playUISound(ModSoundEvents.UI_BIO_FORGE_SELECT_RECIPE);
					recipeBook.setSelectedRecipe(i);
					return true;
				}
			}

			if (recipeBook.getMaxPages() > 1) {
				int x = leftPos + 60 + 1;
				int y = topPos + 211 - font.lineHeight * 2 - 2;
				if (recipeBook.hasPrevPage() && GuiUtil.isInRect(x - 22 - 8 - 1, y, 8, 13, mouseX, mouseY)) {
					SoundUtil.playUISound(ModSoundEvents.UI_BUTTON_CLICK);
					recipeBook.goToPrevPage();
					return true;
				}
				if (recipeBook.hasNextPage() && GuiUtil.isInRect(x + 22, y, 8, 13, mouseX, mouseY)) {
					SoundUtil.playUISound(ModSoundEvents.UI_BUTTON_CLICK);
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
				SoundUtil.playUISound(ModSoundEvents.UI_BUTTON_CLICK);
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
			searchInput.setFocus(true);
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

	@Override
	public void blit(PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
		blit(poseStack, x, y, getBlitOffset(), uOffset, vOffset, uWidth, vHeight, 512, 256);
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		//don't draw any labels
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
		blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		drawFuelBar(poseStack);
		drawTabs(poseStack);
		drawRecipeIngredients(poseStack);
		drawGhostResult(poseStack);
		drawRecipes(poseStack);

		searchInput.render(poseStack, mouseX, mouseY, partialTick);
	}

	private void drawGhostResult(PoseStack poseStack) {
		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		if (selectedRecipe != null && menu.isResultEmpty()) {
			ItemStack stack = selectedRecipe.getResultItem();
			int x = leftPos + 194 + 2;
			int y = topPos + 33 + 2;
			GuiRenderUtil.drawGhostItem(itemRenderer, poseStack, x, y, stack);
			itemRenderer.renderGuiItemDecorations(font, stack, x, y);
			RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		}
	}

	private void drawTabs(PoseStack poseStack) {
		for (int index = 0; index < recipeBook.getTabCount(); index++) {
			drawTab(poseStack, index, recipeBook.isActiveTab(index), recipeBook.getTabIcon(index));
		}
	}

	private void drawRecipes(PoseStack poseStack) {
		if (!recipeBook.hasRecipesOnPage()) return;

		if (recipeBook.hasSelectedRecipe() && recipeBook.isSelectedRecipeVisible()) {
			int gridIndex = recipeBook.getGridIndexOfSelectedRecipe();
			BioForgeRecipe recipe = recipeBook.getRecipeByGrid(gridIndex);
			boolean isCraftable = recipeBook.getRecipeCollectionByGrid(gridIndex).isCraftable(recipe);
			drawTileSelection(poseStack, gridIndex, isCraftable);
		}

		int maxRecipes = recipeBook.getMaxRecipesOnGrid();
		for (int i = 0; i < maxRecipes; i++) {
			BioForgeRecipe recipe = recipeBook.getRecipeByGrid(i);
			boolean isCraftable = recipeBook.getRecipeCollectionByGrid(i).isCraftable(recipe);
			drawRecipeTile(poseStack, i, isCraftable, recipe.getResultItem());
		}

		drawPagination(poseStack);
	}

	private void drawPagination(PoseStack poseStack) {
		if (recipeBook.getMaxPages() < 2) return;

		int x = leftPos + 60 + 1;
		int y = topPos + 211 - font.lineHeight * 2 - 2;
		int currentPage = recipeBook.getCurrentPage();

		if (currentPage > 1) blit(poseStack, x - 22 - 8 - 1, y, 298, 58, 8, 13);
		if (currentPage < recipeBook.getMaxPages()) blit(poseStack, x + 22, y, 334, 58, 8, 13);
		String text = "%d/%d".formatted(currentPage, recipeBook.getMaxPages());
		font.draw(poseStack, text, x - font.width(text) / 2f, y + 3f, ColorStyles.TEXT_ACCENT_FORGE);
	}

	private void drawRecipeIngredients(PoseStack poseStack) {
		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		if (selectedRecipe != null) {
			drawRecipeIngredients(poseStack, selectedRecipe);
		}
	}

	private void drawRecipeIngredients(PoseStack poseStack, BioForgeRecipe recipe) {
		int x = leftPos + 141 + 3;
		int y = topPos + 82 + 3;

		List<IngredientStack> ingredients = recipe.getIngredientQuantities();
		for (int i = 0, size = ingredients.size(); i < size; i++) {
			IngredientStack ingredientStack = ingredients.get(i);
			ItemStack itemStack = ingredientStack.ingredient().getItems()[0];
			drawIngredientQuantity(poseStack, itemStack, recipeBook.getTotalItemCountInPlayerInv(itemStack), ingredientStack.count(), x + 26 * i, y);
		}
	}

	private void drawIngredientQuantity(PoseStack poseStack, ItemStack stack, int currentCount, int requiredCount, int x, int y) {
		boolean insufficient = currentCount < requiredCount;
		blit(poseStack, x - 3, y - 3, insufficient ? 354 : 330, 74, 22, 22);
		itemRenderer.renderGuiItem(stack, x, y);
		String text = "x" + requiredCount;
		font.draw(poseStack, text, x + 16 + 4f - font.width(text), y + 16 + 4f + 1, insufficient ? ColorStyles.TEXT_ERROR : ColorStyles.TEXT_SUCCESS);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
	}

	private void drawRecipeTile(PoseStack poseStack, int idx, boolean isCraftable, ItemStack stack) {
		int x = leftPos + 12 + (20 + 5) * (idx % BioForgeScreenController.COLS);
		int y = topPos + 36 + (20 + 5) * (idx / BioForgeScreenController.COLS);
		drawRecipeTile(poseStack, x, y, stack, !isCraftable);
	}

	private void drawRecipeTile(PoseStack poseStack, int x, int y, ItemStack stack, boolean redOverlay) {
		blit(poseStack, x, y, 295, 30, 22, 22);

		if (redOverlay) {
			blit(poseStack, x, y, 323, 30, 22, 22);
			itemRenderer.renderAndDecorateItem(stack, x + 3, y + 3);
			RenderSystem.depthFunc(GL11.GL_GREATER);
			fill(poseStack, x, y, x + 22, y + 22, 0x40_00_00_00 | ColorStyles.TEXT_ERROR); // 0x40... | color -> prepend alpha to rgb color
			RenderSystem.depthFunc(GL11.GL_LEQUAL);
		} else {
			itemRenderer.renderAndDecorateItem(stack, x + 3, y + 3);
		}
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
	}

	private void drawTileSelection(PoseStack poseStack, int idx, boolean isValid) {
		int x = leftPos + 12 + 25 * (idx % BioForgeScreenController.COLS) - 1;
		int y = topPos + 36 + 25 * (idx / BioForgeScreenController.COLS) - 1;
		blit(poseStack, x, y, isValid ? 295 : 321, 2, 24, 24);
	}

	private void drawTab(PoseStack poseStack, int idx, boolean isActive, ItemStack stack) {
		int w = 24;
		int h = 32;
		int x = leftPos - w + 2;
		int y = topPos + 32 + h * idx;
		blit(poseStack, x, y, 297, isActive ? 114 : 78, w, h);
		itemRenderer.renderGuiItem(stack, x + 5, y + 8);
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
	}

	private void drawFuelBar(PoseStack poseStack) {
		float fuelPct = menu.getFuelAmountNormalized();
		int vHeight = (int) (fuelPct * 36) + (fuelPct > 0 ? 1 : 0);
		blit(poseStack, leftPos + 144, topPos + 13 + 36 - vHeight, 353, 9 + 36 - vHeight, 5, vHeight);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		if (menu.getCarried().isEmpty()) {
			if (drawFuelTooltip(poseStack, mouseX, mouseY)) return;
			if (drawRecipeTooltip(poseStack, mouseX, mouseY)) return;
			if (drawIngredientsTooltip(poseStack, mouseX, mouseY)) return;
		}
		super.renderTooltip(poseStack, mouseX, mouseY);
	}

	private boolean drawRecipeTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		if (recipeBook.hasRecipesOnPage()) {
			int recipes = recipeBook.getMaxRecipesOnGrid();
			for (int idx = 0; idx < recipes; idx++) {
				int x = leftPos + 13 + 25 * (idx % BioForgeScreenController.COLS);
				int y = topPos + 37 + 25 * (idx / BioForgeScreenController.COLS);
				if (GuiUtil.isInRect(x, y, 20, 20, mouseX, mouseY)) {
					renderTooltip(poseStack, recipeBook.getRecipeByGrid(idx).getResultItem(), mouseX, mouseY);
					return true;
				}
			}
		}
		return false;
	}

	private boolean drawIngredientsTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		BioForgeRecipe selectedRecipe = recipeBook.getSelectedRecipe();
		if (selectedRecipe == null) return false;

		for (int i = 0; i < selectedRecipe.getIngredientQuantities().size(); i++) {
			int x = leftPos + 141 + 3 + 26 * i;
			int y = topPos + 82 + 3;
			if (GuiUtil.isInRect(x, y, 20, 20, mouseX, mouseY)) {
				renderTooltip(poseStack, selectedRecipe.getIngredientQuantities().get(i).ingredient().getItems()[0], mouseX, mouseY);
				return true;
			}
		}
		return false;
	}

	private boolean drawFuelTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		if (!GuiUtil.isInRect(leftPos + 144, topPos + 13, 5, 36, mouseX, mouseY)) return false;

		int maxFuel = menu.getMaxFuelAmount();
		int fuelAmount = menu.getFuelAmount();
		int totalFuelCost = recipeBook.hasSelectedRecipe() ? 1 : 0;
		GuiRenderUtil.drawFuelTooltip(this, poseStack, mouseX, mouseY, maxFuel, fuelAmount, totalFuelCost);
		return true;
	}

}
