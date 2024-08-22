package com.github.elenterius.biomancy.client.gui;

import com.github.elenterius.biomancy.BiomancyConfig;
import com.github.elenterius.biomancy.crafting.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.crafting.recipe.IngredientStack;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.client.ModRecipeBookCategories;
import com.github.elenterius.biomancy.integration.BioForgeCompat;
import com.github.elenterius.biomancy.menu.BioForgeMenu;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.github.elenterius.biomancy.mixin.accessor.RecipeCollectionAccessor;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.*;

class BioForgeScreenController {

	public static final int ROWS = 6;
	public static final int COLS = 4;
	public static final int GRID_SIZE = COLS * ROWS;
	private static final Comparator<BioForgeTab> CATEGORY_COMPARATOR = (a, b) -> {
		if (a.sortPriority() == b.sortPriority()) return a.enumId().compareTo(b.enumId());
		return b.sortPriority() - a.sortPriority();
	};
	private static RecipeSelection recipeSelection = RecipeSelection.EMPTY; //volatile client cache
	private final StackedContents itemCounter;
	private final List<BioForgeTab> tabs;
	private final Minecraft minecraft;
	private final BioForgeMenu menu;
	private int maxPages = 0;
	private int activeTab = 0;
	private int startIndex = 0;
	private int playerInvChanges;
	private String currentSearchString = "";
	private List<RecipeCollection> shownRecipes = List.of();
	private int crossoverGridIndex = 0;

	public BioForgeScreenController(Minecraft minecraft, BioForgeMenu menu) {
		this.minecraft = minecraft;
		this.menu = menu;

		tabs = ModBioForgeTabs.REGISTRY.get().getValues().stream().sorted(CATEGORY_COMPARATOR).toList();

		playerInvChanges = getPlayer().getInventory().getTimesChanged();
		itemCounter = new StackedContents();
		getPlayer().getInventory().fillStackedContents(itemCounter);

		//restore selected recipe from volatile client cache
		if (recipeSelection != RecipeSelection.EMPTY && menu.getSelectedRecipe() == null && recipeSelection.recipe != null) {
			ModNetworkHandler.sendBioForgeRecipeToServer(menu.containerId, recipeSelection.recipe);
		}

		updateAndSearchRecipes();
	}

	private ClientLevel getLevel() {
		return Objects.requireNonNull(minecraft.level);
	}

	private LocalPlayer getPlayer() {
		return Objects.requireNonNull(minecraft.player);
	}

	public void resetPagination() {
		startIndex = 0;
	}

	/**
	 * pages begin at index 1
	 *
	 * @return page number
	 */
	public int getCurrentPage() {
		return startIndex / GRID_SIZE + 1;
	}

	public int getMaxPages() {
		return maxPages;
	}

	public void goToNextPage() {
		if (hasNextPage()) {
			startIndex += GRID_SIZE;
		}
	}

	public boolean hasNextPage() {
		return getCurrentPage() < maxPages;
	}

	public boolean hasPrevPage() {
		return getCurrentPage() > 1;
	}

	public void goToPrevPage() {
		if (hasPrevPage()) {
			startIndex -= GRID_SIZE;
		}
	}

	public boolean hasRecipesOnPage() {
		return !shownRecipes.isEmpty();
	}

	public List<BioForgeRecipe> getOrderedRecipes(RecipeCollection recipeCollection) {
		List<Recipe<?>> list = recipeCollection.getDisplayRecipes(true);
		if (!getPlayer().getRecipeBook().isFiltering(ModRecipeBookTypes.BIO_FORGE)) {
			list.addAll(recipeCollection.getDisplayRecipes(false));
		}
		return list.stream().map(BioForgeRecipe.class::cast).toList();
	}

	private BioForgeRecipe getRecipe(int index) {
		if (index >= shownRecipes.size()) throw new IndexOutOfBoundsException(index);
		return getOrderedRecipes(shownRecipes.get(index)).get(0);
		// we disregard all other recipes in the RecipeCollection
		// RecipeCollections for the bio-forge should only contain 1 recipe
	}

	public RecipeCollection getRecipeCollectionByGrid(int gridIndex) {
		if (gridIndex >= GRID_SIZE) throw new IndexOutOfBoundsException(gridIndex);
		return shownRecipes.get(startIndex + gridIndex);
	}

	public BioForgeRecipe getRecipeByGrid(int gridIndex) {
		if (gridIndex >= GRID_SIZE) throw new IndexOutOfBoundsException(gridIndex);
		return getRecipe(startIndex + gridIndex);
	}

	public int getTotalItemCountInPlayerInv(ItemStack stack) {
		int index = StackedContents.getStackingIndex(stack);
		return itemCounter.contents.get(index);
	}

	public boolean hasSufficientIngredientCount(IngredientStack ingredientStack) {
		return ingredientStack.hasSufficientCount(itemCounter);
	}

	public int getMaxRecipesOnGrid() {
		return Math.min(GRID_SIZE, shownRecipes.size() - startIndex);
	}

	//TODO: refactor
	public boolean isSelectedRecipeVisible() {
		int maxIndex = startIndex + GRID_SIZE;
		if (recipeSelection.tab == activeTab) {
			return recipeSelection.index >= startIndex && recipeSelection.index < maxIndex && getGridIndexOfSelectedRecipe() < getMaxRecipesOnGrid();
		}

		if (recipeSelection.recipe != null && (activeTab == 0 || getCurrentCategory() == recipeSelection.recipe.getTab())) {
			int maxRecipes = getMaxRecipesOnGrid();
			for (int gridIndex = 0; gridIndex < maxRecipes; gridIndex++) {
				if (getRecipe(startIndex + gridIndex).isRecipeEqual(recipeSelection.recipe)) {
					crossoverGridIndex = startIndex + gridIndex;
					return true;
				}
			}
		}
		crossoverGridIndex = 0;

		return false;
	}

	public int getGridIndexOfSelectedRecipe() {
		if (recipeSelection.tab == activeTab) {
			return recipeSelection.index - startIndex;
		}

		if (recipeSelection.recipe != null && (activeTab == 0 || getCurrentCategory() == recipeSelection.recipe.getTab())) {
			return crossoverGridIndex - startIndex;
		}

		return 0;
	}

	public final boolean hasSelectedRecipe() {
		return getSelectedRecipe() != null;
	}

	@Nullable
	public BioForgeRecipe getSelectedRecipe() {
		return recipeSelection.recipe;
	}

	void setSelectedRecipe(int gridIndex) {
		if (gridIndex < 0) {
			recipeSelection = RecipeSelection.EMPTY;
			return;
		}

		int recipeIndex = startIndex + gridIndex;
		BioForgeRecipe recipe = getRecipe(recipeIndex);
		recipeSelection = new RecipeSelection(recipe, activeTab, recipeIndex);

		ModNetworkHandler.sendBioForgeRecipeToServer(menu.containerId, recipe);
	}

	public BioForgeTab getCurrentCategory() {
		return tabs.get(activeTab);
	}

	public int getTabIndex(BioForgeTab category) {
		for (int tabIndex = 0; tabIndex < tabs.size(); tabIndex++) {
			if (tabs.get(tabIndex) == category) return tabIndex;
		}
		return 0;
	}

	public void tick() {
		trackPlayerInvChanges();
	}

	public void trackPlayerInvChanges() {
		if (playerInvChanges != getPlayer().getInventory().getTimesChanged()) {
			countPlayerInvItems();
			playerInvChanges = getPlayer().getInventory().getTimesChanged();
		}
	}

	private void countPlayerInvItems() {
		itemCounter.clear();
		getPlayer().getInventory().fillStackedContents(itemCounter);
		updateAndSearchRecipes();
	}

	public void updateSearchString(String searchString) {
		searchString = searchString.toLowerCase(Locale.ROOT);
		if (searchString.equals(currentSearchString)) return;
		updateAndSearchRecipes();
		currentSearchString = searchString;
	}

	private static void canCraftRecipe(RecipeCollection recipeCollection, StackedContents handler, RecipeBook book, boolean isCreativePlayer) {
		RecipeCollectionAccessor accessor = (RecipeCollectionAccessor) recipeCollection;
		Set<Recipe<?>> fitDimensions = accessor.biomancy$getFitDimensions();
		Set<Recipe<?>> craftable = accessor.biomancy$getCraftable();

		for (Recipe<?> recipe : recipeCollection.getRecipes()) {
			boolean isRecipeKnown = isCreativePlayer || book.contains(recipe);
			boolean canCraftRecipe = recipe.canCraftInDimensions(0, 0) && isRecipeKnown;

			if (canCraftRecipe) {
				fitDimensions.add(recipe);
			}
			else {
				fitDimensions.remove(recipe);
			}

			if (canCraftRecipe && handler.canCraft(recipe, null)) {
				craftable.add(recipe);
			}
			else {
				craftable.remove(recipe);
			}
		}
	}

	private void updateAndSearchRecipes() {
		LocalPlayer player = getPlayer();
		boolean isCreativePlayer = player.isCreative() || !BiomancyConfig.SERVER.doBioForgeRecipeProgression.get() || BioForgeCompat.isRecipeCollectionOverwriteEnabled();

		ClientRecipeBook recipeBook = player.getRecipeBook();
		List<RecipeCollection> recipesForCategory = recipeBook.getCollection(ModRecipeBookCategories.getRecipeBookCategories(tabs.get(activeTab)));
		recipesForCategory.forEach(recipeCollection -> canCraftRecipe(recipeCollection, itemCounter, recipeBook, isCreativePlayer));

		List<RecipeCollection> recipes = Lists.newArrayList(recipesForCategory);
		if (!isCreativePlayer) recipes.removeIf(recipeCollection -> !recipeCollection.hasKnownRecipes());
		recipes.removeIf(recipeCollection -> !recipeCollection.hasFitting());

		if (!currentSearchString.isEmpty()) {
			ObjectSet<RecipeCollection> searchResult = new ObjectLinkedOpenHashSet<>(minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(currentSearchString));
			recipes.removeIf(recipeCollection -> !searchResult.contains(recipeCollection));
		}

		if (recipeBook.isFiltering(ModRecipeBookTypes.BIO_FORGE)) {
			recipes.removeIf(recipeCollection -> !recipeCollection.hasCraftable());
		}

		setShownRecipes(recipes);
	}

	private void setShownRecipes(List<RecipeCollection> recipes) {
		shownRecipes = recipes;

		maxPages = Mth.ceil(recipes.size() / (float) GRID_SIZE);
		if (maxPages <= getCurrentPage()) {
			resetPagination();
		}
	}

	public int getTabCount() {
		return tabs.size();
	}

	public BioForgeTab getTab(int tabIndex) {
		return tabs.get(tabIndex);
	}

	public int getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(int tabIndex) {
		activeTab = tabIndex;
		resetPagination();
		updateAndSearchRecipes();
	}

	public boolean isActiveTab(int tabIndex) {
		return tabIndex == activeTab;
	}

	public void onRecipeBookUpdated() {
		updateAndSearchRecipes();
	}

	record RecipeSelection(@Nullable BioForgeRecipe recipe, int tab, int index) {
		public static RecipeSelection EMPTY = new RecipeSelection(null, -1, -1);

		@Nullable
		public ResourceLocation getRecipeId() {
			return recipe != null ? recipe.getId() : null;
		}
	}

}
