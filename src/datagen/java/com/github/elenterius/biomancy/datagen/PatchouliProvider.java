package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import xyz.brassgoggledcoders.patchouliprovider.BookBuilder;
import xyz.brassgoggledcoders.patchouliprovider.CategoryBuilder;
import xyz.brassgoggledcoders.patchouliprovider.EntryBuilder;
import xyz.brassgoggledcoders.patchouliprovider.PatchouliBookProvider;
import xyz.brassgoggledcoders.patchouliprovider.page.SpotlightPageBuilder;
import xyz.brassgoggledcoders.patchouliprovider.page.TextPageBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.github.elenterius.biomancy.init.ModItems.*;

public class PatchouliProvider extends PatchouliBookProvider {

	public PatchouliProvider(DataGenerator gen) {
		super(gen, BiomancyMod.MOD_ID, "en_us");
	}

	protected PatchouliProvider(DataGenerator gen, String locale) {
		super(gen, BiomancyMod.MOD_ID, locale);
	}

	@Override
	protected void addBooks(Consumer<BookBuilder> consumer) {
		BookBuilder book = createBookBuilder("guide_book", "guide_book.biomancy.name", "guide_book.biomancy.landing_text")
				.setVersion("1")
				.setCreativeTab("biomancy").setAdvancementsTab("biomancy")
				.setBookTexture("patchouli:textures/gui/book_gray.png").setModel("patchouli:book_gray")
				.setShowProgress(false)
				.addMacro("\n", "$(br)");

		addIntroCategory(book);
		addFleshbornCategory(book);
		addItemCategory(book);
		addCraftingRecipeCategory(book);
		addReagentCategory(book);
		addEvolutionPoolCategory(book);

		consumer.accept(book);
	}

	private void addIntroCategory(BookBuilder book) {
		book.addCategory("intro", "Getting Started", "Put raw meats, bone meal and maybe some rotten flesh in a cauldron. This will create a meaty soup in the cauldron. Pour a healing potion in it and wait for it to heal and grow into a living meat cube that hops away. Kill the flesh \"slime\" before it devours anyone and grows further.", "biomancy:flesh_block")
				.setSortNum(-1);
	}

	private void addFleshbornCategory(BookBuilder book) {
		CategoryBuilder category = book.addCategory("fleshborn", "Fleshborn", "There are three categories of fleshborn:\n1. Fleshborn that are symbiotic organisms (i.e. both of you benefit together; e.g. heal each other)\n2. Fleshborn that are parasitic organisms (e.g. consume your vitality regardless of your current health situation \u2192 death)\n3. Fleshborn that are exploited organisms (e.g. they do your bidding without compensation)", "biomancy:fleshborn_war_axe");

		EntryBuilder entry = category.addEntry("living_tools", "Living Tools", "biomancy:fleshborn_shovel");
		addMultiPageWrappingTextPagesFor(entry, true, "Living tools known under the common name of fleshborn are in part, a product of the experimentation with parasitic organisms found in pig and cow flesh. A fleshborn tool is a living organism fused with biometal, that was born to fulfill a very specific purpose.\nFleshborn items are generally regarded to be inferior to living beings, having no intelligence or soul, but they have been known to have a higher than average level of longevity and resilience. While they are not considered to be sapient, some tools do seem to possess life-like characteristics.");

		entry = category.addEntry("biometal", "Biometal", "biomancy:biometal").setSortNum(-1);
		addMultiPageWrappingTextPagesFor(entry, true, "In Essence, biometal is a strange semi-living organic metal that looks like flesh and metal twisted together, forming a very strong and flexible metal. Being able to withstand high stress and heat it is one of the most difficult materials to destroy. Once injured it regenerates itself by consuming nutrients from its surroundings. Usually, biometal will have no visual wear and tear, except if it was starved from nutrients intentionally or by accident.\nBiometal has been shown to exhibit properties that could provide an ideal medium for certain biological applications and seems to be a promising candidate for the creation of synthetic life, as it is a very strong, flexible, and resistant material.\nBiometal can be used to create items with unusual and interesting mechanical properties. Although biometal is very tough, it is not really made of metal, so its properties can be altered in ways that would not be possible with other materials. The most common method for creating biometals is by the addition of metal elements to flesh suspended in mutagenic bile in an evolution pool.");
	}

	private void addItemCategory(BookBuilder book) {
		CategoryBuilder category = book.addCategory("items", "Items", "Contains item descriptions and instruction on how to use them.", "biomancy:oculus");
		category.addEntry("normal_tools", "Common Tools", "biomancy:injection_device")
				.addSimpleTextPage("These things are cold and dead...")
				.addSpotlightPage(BONE_SWORD.get()).setText("A sharpened bone that can be used as a sword.").build()
				.addCustomPage(entry -> createSpotlightPage(INJECTION_DEVICE.get(), entry));

		EntryBuilder entry1 = category.addEntry("fleshborn_items", "Fleshborn Items", "biomancy:oculus_key")
				.addSimpleTextPage("Semi-living Tools made of flesh.");
		addSpotlightPagesFor(entry1, SINGLE_ITEM_BAG_ITEM, SMALL_ENTITY_BAG_ITEM, LARGE_ENTITY_BAG_ITEM, OCULUS_KEY);

		EntryBuilder entry2 = category.addEntry("fleshborn_tools", "Fleshborn Tools", "biomancy:fleshborn_pickaxe");
		entry2.addSimpleTextPage("Tools made of lesser biometal, which attune to the block they harvest. The more blocks of the same type are broken the faster the harvesting becomes. Mining a different block disrupts the chain.");
		addSpotlightPagesFor(entry2, FLESHBORN_PICKAXE, FLESHBORN_AXE, FLESHBORN_SHOVEL);

		EntryBuilder entry3 = category.addEntry("weapons", "Semi-Living Weapons", "biomancy:long_range_claw")
				.addSimpleTextPage("Semi-living Weapons made of biometal.");
		addSpotlightPagesFor(entry3, LEECH_CLAW, LONG_RANGE_CLAW, FLESHBORN_WAR_AXE, FLESHBORN_GUAN_DAO);
	}

	private void addCraftingRecipeCategory(BookBuilder book) {
		CategoryBuilder category = book.addCategory("crafting_recipes", "Crafting Recipes", "These Chapters contain recipes for crafting.", "minecraft:crafting_table");

		category.addEntry("leather_from_skin", "Leather from Skin", "minecraft:leather")
				.addSimpleTextPage("Chunks of skin can be sown together into stitched skin, which in turn can be smoked into leather.")
				.addCraftingPage(new ResourceLocation("biomancy", "leather_from_smoking"));

		EntryBuilder entry = category.addEntry("workbench_recipes", "Workbench Recipes", "minecraft:crafting_table");
		addRecipePagesFor(entry,
				new ResourceLocation("biomancy", "lens"),
				new ResourceLocation("biomancy", "oculus"),
				new ResourceLocation("biomancy", "sharp_bone"),
				new ResourceLocation("biomancy", "flesh_lump"),
				new ResourceLocation("biomancy", "chewer"),
				new ResourceLocation("biomancy", "digester"),
				new ResourceLocation("biomancy", "decomposer"),
				new ResourceLocation("biomancy", "solidifier"),
				new ResourceLocation("biomancy", "scent_diffuser"),
				new ResourceLocation("biomancy", "voice_box"));
	}

	private void addReagentCategory(BookBuilder book) {
		book.addCategory("reagents", "Reagents", "These Chapters describe the application of various exotic substances.", "biomancy:reagent")
				.addEntry("overview", "Overview", "biomancy:mutagenic_bile")
				.addSimpleTextPage("Reagents are exotic substances that affect Mobs & Blocks and are injected using a injection device.").build()
				.addEntry("cloning", "Cloning", "biomancy:injection_device")
				.addSimpleTextPage("Grow entities using flesh blobs and blood samples.");
	}

	private void addEvolutionPoolCategory(BookBuilder book) {
		CategoryBuilder category = book.addCategory("evolution_pool", "Inducing Mutations", "These Chapters describe how to use mutagenic bile to induce the mutation of things into semi-living things made of flesh.", "biomancy:mutagenic_bile");
		EntryBuilder entry = category.addEntry("overview", "Overview", "biomancy:mutagenic_bile");
		addMultiPageWrappingTextPagesFor(entry, true, "An evolution pool is a fleshy basin filled with mutagenic bile to the brim. Adding flesh lumps and other materials to the bile activates the evolution machine.\nThe mutagenic processes, commonly called mutagenesis, will cause them to fuse together into living amalgamations of fleshy horror.\nThe resulting creations of mutagen and flesh can be even further transformed by taking them back into the pool and fusing them with other materials, becoming a breeding ground for the most hideous creations imaginable.");

		//TODO: multi-block page builder
	}

	private void addRecipePagesFor(EntryBuilder builder, ResourceLocation... recipeIds) {
		int remainder = recipeIds.length % 2;
		for (int i = 0; i < recipeIds.length - remainder; i += 2) {
			builder.addCraftingPage(recipeIds[i]).setRecipe2(recipeIds[i + 1]);
		}
		if (remainder > 0)
			builder.addCraftingPage(recipeIds[recipeIds.length - 1]);
	}

	private void addSpotlightPagesFor(EntryBuilder builder, RegistryObject<?>... items) {
		for (RegistryObject<?> item : items) {
			IForgeRegistryEntry<?> o = item.get();
			builder.addPage(createSpotlightPage((Item) o, builder));
		}
	}

	private SpotlightPageBuilder createSpotlightPage(Item item, EntryBuilder builder) {
		String tooltip = Util.makeDescriptionId("tooltip", ForgeRegistries.ITEMS.getKey(item));
		return new SpotlightPageBuilder(new ItemStack(item), builder).setText(tooltip, true);
	}

	private void addMultiPageWrappingTextPagesFor(EntryBuilder entryBuilder, boolean hasChapterTitle, String text) {
		int maxLineLength = 31;
		int maxLines = hasChapterTitle ? 13 : 16; //13-14, 16-17

		List<String> wrappedTextLines = wrapTextLines(maxLineLength, text);
		if (wrappedTextLines.size() <= maxLines - 1) {
			entryBuilder.addPage(createTextPage(entryBuilder, text));
			return;
		}

		int lineCount = 0;
		StringBuilder stringBuilder = new StringBuilder();
		for (String textLine : wrappedTextLines) {
			if (stringBuilder.lastIndexOf("\n") != stringBuilder.length() - 1) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(textLine);

			if (lineCount >= maxLines - 1) {
				entryBuilder.addPage(createTextPage(entryBuilder, stringBuilder.toString()));
				stringBuilder = new StringBuilder();
				lineCount = 0;
				maxLines = 16;
				continue;
			}
			lineCount++;
		}

		//add remaining text body
		if (stringBuilder.length() > 0) {
			entryBuilder.addPage(createTextPage(entryBuilder, stringBuilder.toString()));
		}
	}

	private TextPageBuilder createTextPage(EntryBuilder builder, String text) {
		return new TextPageBuilder(text, builder);
	}

	private List<String> wrapTextLines(int maxLineLength, String text) {
		List<String> wrappedText = new ArrayList<>();

		String[] splits = text.split(" ");
		StringBuilder lineBuilder = new StringBuilder();
		for (String word : splits) {
			if (word.contains("\n")) {
				String[] words = word.split("\n");

				//try to attach the first word to the previous text line
				if (lineBuilder.length() + 1 + words[0].length() <= maxLineLength) {
					lineBuilder.append(lineBuilder.length() == 0 ? "" : " ").append(words[0]).append("\n");
					wrappedText.add(lineBuilder.toString());
				}
				else {
					wrappedText.add(lineBuilder.toString());
					wrappedText.add(words[0] + "\n");
				}

				int lastIdx = words.length - 1;
				//each word is on a new line
				for (int i = 1; i < lastIdx; i++) {
					wrappedText.add(words[i] + "\n");
				}

				lineBuilder = new StringBuilder(words[lastIdx]); //make the last word the start of a new line builder
				continue;
			}

			if (lineBuilder.length() + 1 + word.length() <= maxLineLength) {
				lineBuilder.append(lineBuilder.length() == 0 ? "" : " ").append(word);
			}
			else {
				wrappedText.add(lineBuilder.toString());
				lineBuilder = new StringBuilder(word);
			}
		}
		wrappedText.add(lineBuilder.toString());

		return wrappedText;
	}

}
