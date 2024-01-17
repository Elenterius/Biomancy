package com.github.elenterius.biomancy.datagen.modonomicon;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.datagen.lang.AbstractLangProvider;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.GuideBookItem;
import com.klikli_dev.modonomicon.api.datagen.BookContextHelper;
import com.klikli_dev.modonomicon.api.datagen.CategoryEntryMap;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;

public class GuideBookProvider extends AbstractBookProvider {

	public GuideBookProvider(PackOutput packOutput, AbstractLangProvider lang) {
		super(packOutput, BiomancyMod.MOD_ID, lang);
	}

	private ResourceLocation entryId(BookContextHelper context) {
		return modLoc(context.categoryId() + "/" + context.entryId());
	}

	@Override
	protected void generate() {
		BookContextHelper context = new BookContextHelper(modId);

		context.book("guide_book");
		BookModel book = BookModel.create(GuideBookItem.GUIDE_BOOK_ID, context.bookName())
				.withTooltip(context.bookTooltip())
				.withCustomBookItem(ModItems.GUIDE_BOOK.getId()).withGenerateBookItem(false)
				.withCraftingTexture(BiomancyMod.createRL("textures/gui/modonomicon/crafting_textures.png"))
				.withBookContentTexture(BiomancyMod.createRL("textures/gui/modonomicon/book_content.png"))
				.withBookOverviewTexture(BiomancyMod.createRL("textures/gui/modonomicon/book_overview.png"));

		lang.add(book.getName(), "Biomancy Index");
		lang.add(book.getTooltip(), "A book to test Modonomicon features for Biomancy.");

		BookCategoryModel featuresCategory = makeFeaturesCategory(context);

		book.withCategories(featuresCategory);

		add(book);
	}

	private BookCategoryModel makeFeaturesCategory(BookContextHelper context) {
		context.category("features");

		CategoryEntryMap locationHelper = new CategoryEntryMap();
		locationHelper.setMap(
				"_____________________",
				"__________e_d________",
				"_____________________",
				"__c_______p__________",
				"__s__________________",
				"_____________________"
		);

		BookEntryModel primordialCradleRecipe = makeCradleEntry(context, locationHelper.get('p'));

		BookEntryModel spotlightTestEntry = makeSpotlightTestEntry(context, locationHelper.get('s'));

		BookEntryModel fleshBlobEntry = makeFleshBlobEntry(context, locationHelper.get('e'))
				.withParent(BookEntryParentModel.create(primordialCradleRecipe.getId()));

		BookEntryModel decomposerEntry = makeDecomposerEntry(context, locationHelper.get('d'))
				.withParent(BookEntryParentModel.create(fleshBlobEntry.getId()));

		BookCategoryModel category = BookCategoryModel.create(modLoc(context.categoryId()), context.categoryName())
				.withIcon(ModItems.LIVING_FLESH.get())
				.withEntryTextures(BiomancyMod.createRL("textures/gui/modonomicon/entry_textures.png"))
				.withBackground(BiomancyMod.createRL("textures/gui/modonomicon/main_background.png"))
				.withEntries(primordialCradleRecipe, decomposerEntry, fleshBlobEntry)
				.withEntries(spotlightTestEntry);
		lang.add(context.categoryName(), "Fleshy Constructs");

		return category;
	}

	private BookEntryModel makeSpotlightTestEntry(BookContextHelper context, Vec2 location) {
		context.entry("spotlight");

		context.page("intro");
		BookTextPageModel introPage = BookTextPageModel.builder()
				.withText(context.pageText())
				.withTitle(context.pageTitle())
				.build();
		lang.add(context.pageTitle(), "[PH] Spotlight Entry");
		lang.add(context.pageText(), "[PH] Spotlight pages allow to show items (actually, ingredients).");

		context.page("spotlight1");
		BookSpotlightPageModel spotlight1 = BookSpotlightPageModel.builder()
				.withTitle(context.pageTitle())
				.withText(context.pageText())
				.withItem(Ingredient.of(Items.APPLE))
				.build();
		lang.add(context.pageTitle(), "[PH] Custom Title");
		lang.add(context.pageText(), "[PH] A sample spotlight page with custom title.");

		context.page("spotlight2");
		BookSpotlightPageModel spotlight2 = BookSpotlightPageModel.builder()
				.withText(context.pageText())
				.withItem(Ingredient.of(Items.DIAMOND))
				.build();
		lang.add(context.pageText(), "[PH] A sample spotlight page with automatic title.");

		BookEntryModel entryModel = BookEntryModel.create(entryId(context), context.entryName())
				.withDescription(context.entryDescription())
				.withIcon(Items.BEACON)
				.withLocation(location)
				.withPages(introPage, spotlight1, spotlight2);

		lang.add(context.entryName(), "[PH] Spotlight Entry");
		lang.add(context.entryDescription(), "[PH] An entry showcasing spotlight pages.");

		return entryModel;
	}

	private BookEntryModel makeFleshBlobEntry(BookContextHelper context, Vec2 location) {
		context.entry("mob");

		context.page("living_flesh_spotlight");
		BookSpotlightPageModel livingFleshSpotlight = BookSpotlightPageModel.builder()
				.withText(context.pageText())
				.withItem(Ingredient.of(ModItems.LIVING_FLESH.get()))
				.build();
		lang.add(context.pageText(), """
				Living Flesh is the remains of a Flesh Blob after is has been killed.
				\\
				It's most definitely alive, although it lacks any real intelligence or selfish will.
				\\
				++That isn't necessarily a bad thing though...++""");

		context.page("flesh_blob");
		BookEntityPageModel fleshBlobPage = BookEntityPageModel.builder()
				.withEntityId(ModEntityTypes.FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();

		context.page("flesh_blob_page");
		BookTextPageModel fleshBlobText = BookTextPageModel.builder()
				.withText(context.pageText())
				.build();
		lang.add(context.pageText(), """
				A regular Flesh Blob is formed with just typical raw meat and healing agents.
				\\
				Has no redeeming qualities, but makes for a good house pet.""");
		context.page("hungry_flesh_blob");
		BookEntityPageModel hungryFleshBlobPage = BookEntityPageModel.builder()
				.withEntityId(ModEntityTypes.HUNGRY_FLESH_BLOB.getId().toString())
				.withScale(1f)
				.build();

		context.page("hungry_flesh_blob_text");
		BookTextPageModel hungryFleshBlobText = BookTextPageModel.builder()
				.withText(context.pageText())
				.build();
		lang.add(context.pageText(), """
				A Hungry Flesh Blob is formed by adding a few Sharp Fangs into the cradle with some raw meat and a healing agent.
				\\
				Maybe don't try petting this one...""");

		BookEntryModel entryModel = BookEntryModel.create(entryId(context), context.entryName())
				.withDescription(context.entryDescription())
				.withIcon(ModItems.LIVING_FLESH.get())
				.withLocation(location)
				.withPages(
						fleshBlobPage, fleshBlobText,
						hungryFleshBlobPage, hungryFleshBlobText,
						livingFleshSpotlight
				);
		lang.add(context.entryName(), "Flesh Blobs");
		lang.add(context.entryDescription(), "Bouncy lil guys");

		return entryModel;
	}

	private BookEntryModel makeDecomposerEntry(BookContextHelper context, Vec2 location) {
		context.entry("decomposer");

		context.page("spotlight");
		BookSpotlightPageModel introPage = BookSpotlightPageModel.builder()
				.withText(context.pageText())
				.withItem(Ingredient.of(ModItems.DECOMPOSER.get()))
				.build();
		lang.add(context.pageText(), "By giving a Living Flesh some more meat, a few Sharp Fangs, and a Bile Gland, you make a creature that will chew up items and give you useful components for the Bio-Forge");

		context.page("crafting");
		BookCraftingRecipePageModel crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("biomancy:decomposer")
				.build();

		BookEntryModel entryModel = BookEntryModel.create(entryId(context), context.entryName())
				.withDescription(context.entryDescription())
				.withIcon(ModItems.DECOMPOSER.get())
				.withLocation(location)
				.withPages(introPage, crafting);
		lang.add(context.entryName(), "Decomposer");
		lang.add(context.entryDescription(), "Munch, munch!");

		return entryModel;
	}

	private BookEntryModel makeCradleEntry(BookContextHelper context, Vec2 location) {
		context.entry("primordial_cradle");

		context.page("intro");
		BookTextPageModel introPage = BookTextPageModel.builder()
				.withText(context.pageText())
				.withTitle(context.pageTitle())
				.build();
		lang.add(context.pageTitle(), "The Primordial Cradle");
		lang.add(context.pageText(), "By filling the cradle with raw flesh and a healing agent (Instant Health Potions, Healing Additive, or Regenerative Fluid) you gain the ability to form new living beings");

		context.page("crafting");
		BookCraftingRecipePageModel crafting = BookCraftingRecipePageModel.builder()
				.withRecipeId1("biomancy:primordial_core")
				.withRecipeId2("biomancy:primordial_cradle")
				.withText(context.pageText())
				.build();
		lang.add(context.pageText(), "Primordial Cradle Recipe");

		BookEntryModel entryModel = BookEntryModel.create(entryId(context), context.entryName())
				.withDescription(context.entryDescription())
				.withIcon(ModItems.PRIMORDIAL_CRADLE.get())
				.withLocation(location)
				.withPages(introPage, crafting);
		lang.add(context.entryName(), "The Primordial Cradle");
		lang.add(context.entryDescription(), "The Fun Begins");

		return entryModel;
	}

}
