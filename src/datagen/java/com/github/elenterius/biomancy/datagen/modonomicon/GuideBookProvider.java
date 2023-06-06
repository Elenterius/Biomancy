package com.github.elenterius.biomancy.datagen.modonomicon;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.lang.AbstractLangProvider;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.GuideBookItem;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import com.klikli_dev.modonomicon.api.datagen.BookLangHelper;
import com.klikli_dev.modonomicon.api.datagen.EntryLocationHelper;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class GuideBookProvider extends AbstractBookProvider {

	public GuideBookProvider(DataGenerator generator, AbstractLangProvider lang) {
		super(generator, BiomancyMod.MOD_ID, lang);
	}

	private ResourceLocation entryId(BookLangHelper helper) {
		return modLoc(helper.category + "/" + helper.entry);
	}

	@Override
	protected void generate() {

		BookLangHelper helper = ModonomiconAPI.get().getLangHelper(modId);
		helper.book("guide_book");

		BookCategoryModel featuresCategory = makeFeaturesCategory(helper);

		BookModel demoBook = BookModel.create(GuideBookItem.GUIDE_BOOK_ID, helper.bookName())
				.withTooltip(helper.bookTooltip())
				.withCustomBookItem(ModItems.GUIDE_BOOK.getId()).withGenerateBookItem(false)
				.withCategories(featuresCategory);

		lang.add(demoBook.getName(), "[PH] Biomancy Index");
		lang.add(demoBook.getTooltip(), "[PH] A book to test Modonomicon features for Biomancy.");

		add(demoBook);
	}

	private BookCategoryModel makeFeaturesCategory(BookLangHelper helper) {
		helper.category("features");

		EntryLocationHelper entryHelper = ModonomiconAPI.get().getEntryLocationHelper();
		entryHelper.setMap(
				"_____________________",
				"__m______________d___",
				"__________r__________",
				"__c__________________",
				"__________2___3___i__",
				"__s_____e____________"
		);

		var multiBlockEntry = makeMultiBlockEntry(helper, entryHelper, 'm');

		var recipeEntry = makeRecipeEntry(helper, entryHelper, 'c');

		var spotlightEntry = makeSpotlightEntry(helper, entryHelper, 's')
				.withParent(BookEntryParentModel.builder().withEntryId(recipeEntry.getId()).build())
				.build();

		var entityEntry = makeEntityEntry(helper, entryHelper, 'd');

		BookCategoryModel categoryModel = BookCategoryModel.create(modLoc(helper.category), helper.categoryName())
				.withIcon("minecraft:nether_star") //the icon for the category. In this case we simply use an existing item.
				.withEntries(multiBlockEntry)
				.withEntries(recipeEntry)
				.withEntries(spotlightEntry)
				.withEntries(entityEntry);
		lang.add(helper.categoryName(), "[PH] Features Category");

		return categoryModel;
	}

	private BookEntryModel makeRecipeEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("recipe");

		helper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(helper.pageText())
				.withTitle(helper.pageTitle())
				.build();
		lang.add(helper.pageTitle(), "[PH] Recipe Entry");
		lang.add(helper.pageText(), "[PH] Recipe pages allow to show recipes in the book.");

		helper.page("crafting");
		var crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("minecraft:crafting_table")
				.withRecipeId2("minecraft:oak_planks")
				.withText(helper.pageText())
				.withTitle2("test.test.test")
				.build();
		lang.add(helper.pageText(), "[PH] A sample recipe page.");
		lang.add("test.test.test", "[PH] Book of Binding: Afrit (Bound)");


		helper.page("smelting");
		var smelting = BookSmeltingRecipePageModel.builder()
				.withRecipeId1("minecraft:charcoal")
				.withRecipeId2("minecraft:cooked_beef")
				.build();
		lang.add(helper.pageText(), "[PH] A smelting recipe page with one recipe and some text.");

		helper.page("blasting");
		var blasting = BookBlastingRecipePageModel.builder()
				.withRecipeId2("biomancy:glass_pane_from_blasting")
				.build();

		BookEntryModel entryModel = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:crafting_table")
				.withLocation(entryHelper.get('c'))
				.withPages(introPage, crafting, smelting, blasting)
				.build();
		lang.add(helper.entryName(), "[PH] Recipe Entry");
		lang.add(helper.entryDescription(), "[PH] An entry showcasing recipe pages.");

		return entryModel;
	}

	private BookEntryModel.Builder makeSpotlightEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("spotlight");

		helper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(helper.pageText())
				.withTitle(helper.pageTitle())
				.build();
		lang.add(helper.pageTitle(), "[PH] Spotlight Entry");
		lang.add(helper.pageText(), "[PH] Spotlight pages allow to show items (actually, ingredients).");

		helper.page("spotlight1");
		var spotlight1 = BookSpotlightPageModel.builder()
				.withTitle(helper.pageTitle())
				.withText(helper.pageText())
				.withItem(Ingredient.of(Items.APPLE))
				.build();
		lang.add(helper.pageTitle(), "[PH] Custom Title");
		lang.add(helper.pageText(), "[PH] A sample spotlight page with custom title.");

		helper.page("spotlight2");
		var spotlight2 = BookSpotlightPageModel.builder()
				.withText(helper.pageText())
				.withItem(Ingredient.of(Items.DIAMOND))
				.build();
		lang.add(helper.pageText(), "[PH] A sample spotlight page with automatic title.");

		BookEntryModel.Builder builder = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:beacon")
				.withLocation(entryHelper.get(location))
				.withPages(introPage, spotlight1, spotlight2);
		lang.add(helper.entryName(), "[PH] Spotlight Entry");
		lang.add(helper.entryDescription(), "[PH] An entry showcasing spotlight pages.");
		return builder;
	}

	private BookEntryModel makeEntityEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("entity");

		helper.page("intro");
		BookTextPageModel introPage = BookTextPageModel.builder()
				.withText(helper.pageText())
				.withTitle(helper.pageTitle())
				.build();
		lang.add(helper.pageTitle(), "[PH] Entity Entry");
		lang.add(helper.pageText(), "[PH] Entity pages allow to show entities.");

		helper.page("flesh_blob");
		BookEntityPageModel fleshBlobPage = BookEntityPageModel.builder()
				.withEntityName(helper.pageTitle())
				.withEntityId(ModEntityTypes.FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();
		lang.add(helper.pageTitle(), "[PH] Flesh Blob");

		helper.page("hungry_flesh_blob");
		BookEntityPageModel hungryFleshBlobPage = BookEntityPageModel.builder()
				.withText(helper.pageText())
				.withEntityId(ModEntityTypes.HUNGRY_FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();
		lang.add(helper.pageText(), "[PH] A sample entity page with automatic title.");

		BookEntryModel entryModel = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:spider_eye")
				.withLocation(entryHelper.get(location))
				.withPages(introPage, fleshBlobPage, hungryFleshBlobPage)
				.build();
		lang.add(helper.entryName(), "[PH] Entity Entry");
		lang.add(helper.entryDescription(), "[PH] An entry showcasing entity pages.");

		return entryModel;
	}

	private BookEntryModel makeMultiBlockEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("multiblock");

		helper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(helper.pageText())
				.withTitle(helper.pageTitle())
				.build();
		lang.add(helper.pageTitle(), "[PH] Multi-block Page");
		lang.add(helper.pageText(), "[PH] Multi-block pages allow to preview multi-blocks both in the book and in the world.");

		helper.page("preview");
		var multiBlockPreviewPage = BookMultiblockPageModel.builder()
				.withMultiblockId("modonomicon:blockentity") //sample multi-block from modonomicon
				.withMultiblockName("multiblocks.modonomicon.blockentity") //and the lang key for its name
				.withText(helper.pageText())
				.build();
		lang.add("multiblocks.modonomicon.blockentity", "[PH] Blockentity Multi-Block.");
		lang.add(helper.pageText(), "[PH] A sample multi-block.");

		BookEntryModel entryModel = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:furnace") //we use furnace as icon
				.withLocation(entryHelper.get(location)) //and we place it at the location we defined earlier in the entry helper mapping
				.withPages(introPage, multiBlockPreviewPage) //finally we add our pages to the entry
				.build();
		lang.add(helper.entryName(), "[PH] Multi-block Entry");
		lang.add(helper.entryDescription(), "[PH] An entry showcasing a multi-block.");

		return entryModel;
	}

}
