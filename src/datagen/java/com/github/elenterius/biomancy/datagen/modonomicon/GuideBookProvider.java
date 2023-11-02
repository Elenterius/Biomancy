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
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;

public class GuideBookProvider extends AbstractBookProvider {

	public GuideBookProvider(DataGenerator generator, AbstractLangProvider lang) {
		super(generator, BiomancyMod.MOD_ID, lang);
	}

	private ResourceLocation entryId(BookLangHelper helper) {
		return modLoc(helper.category + "/" + helper.entry);
	}

	@Override
	protected void generate() {
		BookLangHelper langHelper = ModonomiconAPI.get().getLangHelper(modId);

		langHelper.book("guide_book");
		BookModel book = BookModel.create(GuideBookItem.GUIDE_BOOK_ID, langHelper.bookName())
				.withTooltip(langHelper.bookTooltip())
				.withCustomBookItem(ModItems.GUIDE_BOOK.getId()).withGenerateBookItem(false)
				.withCraftingTexture(BiomancyMod.createRL("textures/gui/modonomicon/crafting_textures.png"))
				.withBookContentTexture(BiomancyMod.createRL("textures/gui/modonomicon/book_content.png"))
				.withBookOverviewTexture(BiomancyMod.createRL("textures/gui/modonomicon/book_overview.png"));

		lang.add(book.getName(), "Biomancy Index");
		lang.add(book.getTooltip(), "A book to test Modonomicon features for Biomancy.");

		BookCategoryModel featuresCategory = makeFeaturesCategory(langHelper);

		book.withCategories(featuresCategory);

		add(book);
	}

	private BookCategoryModel makeFeaturesCategory(BookLangHelper langHelper) {
		langHelper.category("features");

		EntryLocationHelper locationHelper = ModonomiconAPI.get().getEntryLocationHelper();
		locationHelper.setMap(
				"_____________________",
				"__________e_d________",
				"_____________________",
				"__c_______p__________",
				"__s__________________",
				"_____________________"
		);

		BookEntryModel primordialCradleRecipe = makeCradleEntry(langHelper, locationHelper.get('p')).build();

		BookEntryModel spotlightTestEntry = makeSpotlightTestEntry(langHelper, locationHelper.get('s')).build();

		BookEntryModel fleshBlobEntry = makeFleshBlobEntry(langHelper, locationHelper.get('e'))
				.withParent(BookEntryParentModel.builder().withEntryId(primordialCradleRecipe.getId()).build())
				.build();

		BookEntryModel decomposerEntry = makeDecomposerEntry(langHelper, locationHelper.get('d'))
				.withParent(BookEntryParentModel.builder().withEntryId(fleshBlobEntry.getId()).build())
				.build();

		BookCategoryModel category = BookCategoryModel.create(modLoc(langHelper.category), langHelper.categoryName())
				.withIcon(ModItems.LIVING_FLESH.get())
				.withEntryTextures(BiomancyMod.createRL("textures/gui/modonomicon/entry_textures.png"))
				.withBackground(BiomancyMod.createRL("textures/gui/modonomicon/main_background.png"))
				.withEntries(primordialCradleRecipe, decomposerEntry, fleshBlobEntry)
				.withEntries(spotlightTestEntry);
		lang.add(langHelper.categoryName(), "Fleshy Constructs");

		return category;
	}

	private BookEntryModel.Builder makeSpotlightTestEntry(BookLangHelper langHelper, Vec2 location) {
		langHelper.entry("spotlight");

		langHelper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(langHelper.pageText())
				.withTitle(langHelper.pageTitle())
				.build();
		lang.add(langHelper.pageTitle(), "[PH] Spotlight Entry");
		lang.add(langHelper.pageText(), "[PH] Spotlight pages allow to show items (actually, ingredients).");

		langHelper.page("spotlight1");
		var spotlight1 = BookSpotlightPageModel.builder()
				.withTitle(langHelper.pageTitle())
				.withText(langHelper.pageText())
				.withItem(Ingredient.of(Items.APPLE))
				.build();
		lang.add(langHelper.pageTitle(), "[PH] Custom Title");
		lang.add(langHelper.pageText(), "[PH] A sample spotlight page with custom title.");

		langHelper.page("spotlight2");
		var spotlight2 = BookSpotlightPageModel.builder()
				.withText(langHelper.pageText())
				.withItem(Ingredient.of(Items.DIAMOND))
				.build();
		lang.add(langHelper.pageText(), "[PH] A sample spotlight page with automatic title.");

		var builder = BookEntryModel.builder()
				.withId(entryId(langHelper))
				.withName(langHelper.entryName())
				.withDescription(langHelper.entryDescription())
				.withIcon(Items.BEACON)
				.withLocation(location)
				.withPages(introPage, spotlight1, spotlight2);

		lang.add(langHelper.entryName(), "[PH] Spotlight Entry");
		lang.add(langHelper.entryDescription(), "[PH] An entry showcasing spotlight pages.");

		return builder;
	}

	private BookEntryModel.Builder makeFleshBlobEntry(BookLangHelper langHelper, Vec2 location) {
		langHelper.entry("mob");

		langHelper.page("living_flesh_spotlight");
		var livingFleshSpotlight = BookSpotlightPageModel.builder()
				.withText(langHelper.pageText())
				.withItem(Ingredient.of(ModItems.LIVING_FLESH.get()))
				.build();
		lang.add(langHelper.pageText(), """
				Living Flesh is the remains of a Flesh Blob after is has been killed.
				\\
				It's most definitely alive, although it lacks any real intelligence or selfish will.
				\\
				++That isn't necessarily a bad thing though...++""");

		langHelper.page("flesh_blob");
		BookEntityPageModel fleshBlobPage = BookEntityPageModel.builder()
				.withEntityId(ModEntityTypes.FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();

		langHelper.page("flesh_blob_page");
		BookTextPageModel fleshBlobText = BookTextPageModel.builder()
				.withText(langHelper.pageText())
				.build();
		lang.add(langHelper.pageText(), """
				A regular Flesh Blob is formed with just typical raw meat and healing agents.
				\\
				Has no redeeming qualities, but makes for a good house pet.""");
		langHelper.page("hungry_flesh_blob");
		BookEntityPageModel hungryFleshBlobPage = BookEntityPageModel.builder()
				.withEntityId(ModEntityTypes.HUNGRY_FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();

		langHelper.page("hungry_flesh_blob_text");
		BookTextPageModel hungryFleshBlobText = BookTextPageModel.builder()
				.withText(langHelper.pageText())
				.build();
		lang.add(langHelper.pageText(), """
				A Hungry Flesh Blob is formed by adding a few Sharp Fangs into the cradle with some raw meat and a healing agent.
				\\
				Maybe don't try petting this one...""");

		var builder = BookEntryModel.builder()
				.withId(entryId(langHelper))
				.withName(langHelper.entryName())
				.withDescription(langHelper.entryDescription())
				.withIcon(ModItems.LIVING_FLESH.get())
				.withLocation(location)
				.withPages(
						fleshBlobPage, fleshBlobText,
						hungryFleshBlobPage, hungryFleshBlobText,
						livingFleshSpotlight
				);
		lang.add(langHelper.entryName(), "Flesh Blobs");
		lang.add(langHelper.entryDescription(), "Bouncy lil guys");

		return builder;
	}

	private BookEntryModel.Builder makeDecomposerEntry(BookLangHelper langHelper, Vec2 location) {
		langHelper.entry("decomposer");

		langHelper.page("spotlight");
		var introPage = BookSpotlightPageModel.builder()
				.withText(langHelper.pageText())
				.withItem(Ingredient.of(ModItems.DECOMPOSER.get()))
				.build();
		lang.add(langHelper.pageText(), "By giving a Living Flesh some more meat, a few Sharp Fangs, and a Bile Gland, you make a creature that will chew up items and give you useful components for the Bio-Forge");

		langHelper.page("crafting");
		var crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("biomancy:decomposer")
				.build();

		var builder = BookEntryModel.builder()
				.withId(entryId(langHelper))
				.withName(langHelper.entryName())
				.withDescription(langHelper.entryDescription())
				.withIcon(ModItems.DECOMPOSER.get())
				.withLocation(location)
				.withPages(introPage, crafting);
		lang.add(langHelper.entryName(), "Decomposer");
		lang.add(langHelper.entryDescription(), "Munch, munch!");

		return builder;
	}

	private BookEntryModel.Builder makeCradleEntry(BookLangHelper langHelper, Vec2 location) {
		langHelper.entry("primordial_cradle");

		langHelper.page("intro");
		var introPage = BookTextPageModel.builder()
				.withText(langHelper.pageText())
				.withTitle(langHelper.pageTitle())
				.build();
		lang.add(langHelper.pageTitle(), "The Primordial Cradle");
		lang.add(langHelper.pageText(), "By filling the cradle with raw flesh and a healing agent (Instant Health Potions, Healing Additive, or Regenerative Fluid) you gain the ability to form new living beings");

		langHelper.page("crafting");
		var crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("biomancy:primordial_core")
				.withRecipeId2("biomancy:primordial_cradle")
				.withText(langHelper.pageText())
				.build();
		lang.add(langHelper.pageText(), "Primordial Cradle Recipe");

		var builder = BookEntryModel.builder()
				.withId(entryId(langHelper))
				.withName(langHelper.entryName())
				.withDescription(langHelper.entryDescription())
				.withIcon(ModItems.PRIMORDIAL_CRADLE.get())
				.withLocation(location)
				.withPages(introPage, crafting);
		lang.add(langHelper.entryName(), "The Primordial Cradle");
		lang.add(langHelper.entryDescription(), "The Fun Begins");

		return builder;
	}

}
