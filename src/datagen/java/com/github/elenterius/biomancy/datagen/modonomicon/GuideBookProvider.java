package com.github.elenterius.biomancy.datagen.modonomicon;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.translations.AbstractTranslationProvider;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.world.item.GuideBookItem;
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

	public GuideBookProvider(DataGenerator generator, AbstractTranslationProvider lang) {
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

		BookModel demoBook = BookModel.builder()
				.withId(GuideBookItem.GUIDE_BOOK_ID)
				.withName(helper.bookName())
				.withTooltip(helper.bookTooltip())
				.withCustomBookItem(ModItems.GUIDE_BOOK.getId()).withGenerateBookItem(false)
				.withCategories(featuresCategory)
				.withCraftingTexture(BiomancyMod.createRL("textures/gui/crafting_textures.png"))
				.withBookContentTexture(BiomancyMod.createRL("textures/gui/book_content.png"))
				.withBookOverviewTexture(BiomancyMod.createRL("textures/gui/book_overview.png"))
				.build();

		lang.add(demoBook.getName(), "Biomancy Index");
		lang.add(demoBook.getTooltip(), "A book to test Modonomicon features for Biomancy.");

		add(demoBook);
	}

	private BookCategoryModel makeFeaturesCategory(BookLangHelper helper) {
		helper.category("features");

		EntryLocationHelper entryHelper = ModonomiconAPI.get().getEntryLocationHelper();
		entryHelper.setMap(
				"_____________________",
				"__________e_d________",
				"_____________________",
				"__c_______p__________",
				"__s__________________",
				"_____________________"
		);

		var primordialCradleRecipe = makeCradleEntry(helper, entryHelper, 'p');

		var decomposerEntry = makeDecomposerEntry(helper, entryHelper, 'd')
				.withParent(BookEntryParentModel.builder().withEntryId().build())
				.build();

		var spotlightEntry = makeSpotlightEntry(helper, entryHelper, 's');

		var entityEntry = makeEntityEntry(helper, entryHelper, 'e')
				.withParent(BookEntryParentModel.builder().withEntryId(primordialCradleRecipe.getId()).build())
				.build();


		BookCategoryModel categoryModel = BookCategoryModel.builder()
				.withId(modLoc(helper.category)) //the id of the category, as stored in the lang helper. modLoc() prepends the mod id.
				.withName(helper.categoryName()) //the name of the category. The lang helper gives us the correct translation key.
				.withIcon("biomancy:living_flesh")
				.withEntries(primordialCradleRecipe)
				.withEntries(decomposerEntry)
				.withEntries(spotlightEntry)
				.withEntries(entityEntry)
				.withEntryTextures(BiomancyMod.createRL("textures/gui/entry_textures.png"))
				.withBackground(BiomancyMod.createRL("textures/gui/category1_background.png"))
				.build();
		lang.add(helper.categoryName(), "Fleshy Constructs");

		return categoryModel;
	}

	private BookEntryModel.Builder makeDecomposerEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("decomposer");

		helper.page("spotlight");
		var introPage = BookSpotlightPageModel.builder()
				.withText(helper.pageText())
				.withItem(Ingredient.of(ModItems.DECOMPOSER.get()))
				.build();
		lang.add(helper.pageText(), """
				By giving a Living Flesh some more meat, a few Sharp Fangs, and a Bile Gland, you make a creature that will chew up items and give you useful components for the Bio-Forge""");

		helper.page("crafting");
		var crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("biomancy:decomposer")
				.build();

		var entryModel = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:crafting_table")
				.withLocation(entryHelper.get('c'))
				.withPages(introPage, crafting);
		lang.add(helper.entryName(), "Recipe Entry");
		lang.add(helper.entryDescription(), "An entry showcasing recipe pages.");

		return entryModel;
	}

	private BookEntryModel makeSpotlightEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
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

		var builder = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("minecraft:beacon")
				.withLocation(entryHelper.get(location))
				.withPages(introPage, spotlight1, spotlight2)
				.build();
		lang.add(helper.entryName(), "Spotlight Entry");
		lang.add(helper.entryDescription(), "An entry showcasing spotlight pages.");
		return builder;
	}

	private BookEntryModel.Builder makeEntityEntry(BookLangHelper helper, EntryLocationHelper entryHelper, char location) {
		helper.entry("entity");

		helper.page("living_flesh_spotlight");
		var livingFleshSpotlight = BookSpotlightPageModel.builder()
				.withText(helper.pageText())
				.withItem(Ingredient.of(ModItems.LIVING_FLESH.get()))
				.build();
		lang.add(helper.pageText(), """
        Living Flesh is the remains of a Flesh Blob after is has been killed
        \\\\
        It's most definitely alive, although it lacks any real intelligence or selfish will
        \\\\
        ++That may not be a bad thing though...++""");

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

		var entryModel = BookEntryModel.builder()
				.withId(entryId(helper))
				.withName(helper.entryName())
				.withDescription(helper.entryDescription())
				.withIcon("biomancy:living_flesh")
				.withLocation(entryHelper.get(location))
				.withPages(livingFleshSpotlight, fleshBlobPage, hungryFleshBlobPage);
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
