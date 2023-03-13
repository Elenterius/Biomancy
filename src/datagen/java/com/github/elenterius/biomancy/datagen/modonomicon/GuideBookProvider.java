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
				.withCategories(featuresCategory)
				.withCraftingTexture(BiomancyMod.createRL("textures/gui/crafting_textures.png"))
				.withBookContentTexture(BiomancyMod.createRL("textures/gui/book_content.png"))
				.withBookOverviewTexture(BiomancyMod.createRL("textures/gui/book_overview.png"));

		lang.add(demoBook.getName(), "Biomancy Index");
		lang.add(demoBook.getTooltip(), "A book to test Modonomicon features for Biomancy.");

		add(demoBook);
	}

	private BookCategoryModel makeFeaturesCategory(BookLangHelper helper) {
		helper.category("features");

		EntryLocationHelper entryHelper = ModonomiconAPI.get().getEntryLocationHelper();
		entryHelper.setMap(
				"_____________________",
				"__p__d__s____________",
				"__________r__________",
				"__c__________________",
				"__________2___3___i__",
				"________e____________"
		);

		var primordialCradleRecipe = makeCradleEntry(helper, entryHelper, 'p');

		var recipeEntry = makeRecipeEntry(helper, entryHelper, 'c');

		var spotlightEntry = makeSpotlightEntry(helper, entryHelper, 's');

		var entityEntry = makeEntityEntry(helper, entryHelper, 'd')
				.withParent(BookEntryParentModel.builder().withEntryId(primordialCradleRecipe.getId()).build())
				.build();

		BookCategoryModel categoryModel = BookCategoryModel.create(modLoc(helper.category), helper.categoryName())
				.withIcon("biomancy:living_flesh")
				.withEntries(primordialCradleRecipe)
				.withEntries(recipeEntry)
				.withEntries(spotlightEntry)
				.withEntries(entityEntry)
				.withEntryTextures(BiomancyMod.createRL("textures/gui/entry_textures.png"))
				.withBackground(BiomancyMod.createRL("textures/gui/category1_background.png"));
		lang.add(helper.categoryName(), "Fleshy Constructs");

		return categoryModel;
	}

	private BookEntryModel makeRecipeEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("recipe");

		helper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(helper.pageText())
				.withTitle(helper.pageTitle())
				.build();
		lang.add(helper.pageTitle(), "Recipe Entry");
		lang.add(helper.pageText(), "Recipe pages allow to show recipes in the book.");

		helper.page("crafting");
		var crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("minecraft:crafting_table")
				.withRecipeId2("minecraft:oak_planks")
				.withText(helper.pageText())
				.withTitle2("test.test.test")
				.build();
		lang.add(helper.pageText(), "A sample recipe page.");
		lang.add("test.test.test", "Book of Binding: Afrit (Bound)");


		helper.page("smelting");
		var smelting = BookSmeltingRecipePageModel.builder()
				.withRecipeId1("minecraft:charcoal")
				.withRecipeId2("minecraft:cooked_beef")
				.build();
		lang.add(helper.pageText(), "A smelting recipe page with one recipe and some text.");

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
		lang.add(helper.entryName(), "Recipe Entry");
		lang.add(helper.entryDescription(), "An entry showcasing recipe pages.");

		return entryModel;
	}

	private BookEntryModel.Builder makeSpotlightEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("spotlight");

		helper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(helper.pageText())
				.withTitle(helper.pageTitle())
				.build();
		lang.add(helper.pageTitle(), "Spotlight Entry");
		lang.add(helper.pageText(), "Spotlight pages allow to show items (actually, ingredients).");

		helper.page("spotlight1");
		var spotlight1 = BookSpotlightPageModel.builder()
				.withTitle(helper.pageTitle())
				.withText(helper.pageText())
				.withItem(Ingredient.of(Items.APPLE))
				.build();
		lang.add(helper.pageTitle(), "Custom Title");
		lang.add(helper.pageText(), "A sample spotlight page with custom title.");

		helper.page("spotlight2");
		var spotlight2 = BookSpotlightPageModel.builder()
				.withText(helper.pageText())
				.withItem(Ingredient.of(Items.DIAMOND))
				.build();
		lang.add(helper.pageText(), "A sample spotlight page with automatic title.");

		BookEntryModel.Builder builder = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:beacon")
				.withLocation(entryHelper.get(location))
				.withPages(introPage, spotlight1, spotlight2);
		lang.add(helper.entryName(), "Spotlight Entry");
		lang.add(helper.entryDescription(), "An entry showcasing spotlight pages.");
		return builder;
	}

	private BookEntryModel makeEntityEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("entity");

		helper.page("living_flesh_spotlight");
		var livingFleshSpotlight = BookSpotlightPageModel.builder()
				.withText(helper.pageText())
				.withItem(Ingredient.of(Items.BEEF))
				.build();
		lang.add(helper.pageText(), "Living Flesh is the remains of a Flesh Blob after is has been killed\\It's most definitely alive, although it lacks any real intelligence or selfish will\\++That may not be a bad thing though...++");

		helper.page("flesh_blob");
		BookEntityPageModel fleshBlobPage = BookEntityPageModel.builder()
				.withEntityName(helper.pageTitle())
				.withEntityId(ModEntityTypes.FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();
		lang.add(helper.pageTitle(), "Flesh Blob");

		helper.page("hungry_flesh_blob");
		BookEntityPageModel hungryFleshBlobPage = BookEntityPageModel.builder()
				.withText(helper.pageText())
				.withEntityId(ModEntityTypes.HUNGRY_FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();
		lang.add(helper.pageText(), "A sample entity page with automatic title.");

		BookEntryModel entryModel = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:spider_eye")
				.withLocation(entryHelper.get(location))
				.withPages(livingFleshSpotlight, fleshBlobPage, hungryFleshBlobPage)
				.build();
		lang.add(helper.entryName(), "Entity Entry");
		lang.add(helper.entryDescription(), "An entry showcasing entity pages.");

		return entryModel;
	}

	private BookEntryModel makeCradleEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("primordialcradleentry");

		helper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(helper.pageText())
				.withTitle(helper.pageTitle())
				.build();
		lang.add(helper.pageTitle(), "The Primordial Cradle");
		lang.add(helper.pageText(), "By filling the cradle with raw flesh and a healing agent (Instant Health Potions or Regenerative Fluid) you gain the ability to form new living beings");

		helper.page("crafting");
		var crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("biomancy:primordial_cradle")
				.withRecipeId2("biomancy:primordial_living_oculus")
				.withText(helper.pageText())
				.build();
		lang.add(helper.pageText(), "Primordial Cradle Recipe");

		BookEntryModel entryModel = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("biomancy:primordial_cradle") //we use primordial cradle as icon
				.withLocation(entryHelper.get(location)) //and we place it at the location we defined earlier in the entry helper mapping
				.withPages(introPage, crafting) //finally we add our pages to the entry
				.build();
		lang.add(helper.entryName(), "The Primordial Cradle");
		lang.add(helper.entryDescription(), "The Fun Begins");

		return entryModel;
	}

}
