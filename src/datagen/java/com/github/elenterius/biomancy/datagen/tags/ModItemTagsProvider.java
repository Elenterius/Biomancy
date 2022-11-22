package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

import static net.minecraft.world.item.Items.*;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	private static void addOptionalItemsTo(TagsProvider.TagAppender<Item> builder, String... itemKeys) {
		for (String itemKey : itemKeys) {
			builder.addOptional(new ResourceLocation(itemKey));
		}
	}

	private static void addOptionalTagsTo(TagsProvider.TagAppender<Item> builder, String... tagKeys) {
		for (String tagKey : tagKeys) {
			builder.addOptionalTag(new ResourceLocation(tagKey));
		}
	}

	@Override
	protected void addTags() {
		TagsProvider.TagAppender<Item> sugars = tag(ModTags.Items.SUGARS)
				.add(SUGAR, COOKIE, CAKE, HONEYCOMB, HONEY_BLOCK, HONEYCOMB_BLOCK, HONEY_BOTTLE, SWEET_BERRIES, COCOA_BEANS, APPLE);
		addOptionalItemsTo(sugars, "create:sweet_roll", "create:chocolate_glazed_berries", "create:honeyed_apple", "create:bar_of_chocolate", "createaddition:chocolate_cake");

		tag(ModTags.Items.POOR_BIOMASS)
				.addTag(ItemTags.FLOWERS).addTag(Tags.Items.SEEDS).addTag(ItemTags.LEAVES)
				.add(SWEET_BERRIES, SUGAR_CANE, KELP, DRIED_KELP, GRASS, SEAGRASS, VINE, FERN, BAMBOO)
				.addOptional(new ResourceLocation("rats:contaminated_food"));

		TagsProvider.TagAppender<Item> avgBiomass = tag(ModTags.Items.AVERAGE_BIOMASS)
				.addTag(ItemTags.SAPLINGS).addTag(Tags.Items.CROPS).addTag(Tags.Items.MUSHROOMS)
				.add(COOKIE, CACTUS, APPLE, CHORUS_FRUIT, MELON_SLICE, SPIDER_EYE, WARPED_FUNGUS,
//						ModItems.MILK_GEL.get(),
						NETHER_SPROUTS, WEEPING_VINES, TWISTING_VINES, LARGE_FERN, TALL_GRASS, WARPED_ROOTS, CRIMSON_ROOTS, CRIMSON_FUNGUS);
		addOptionalItemsTo(avgBiomass, "createfa:cheese", "createfa:mixed_egg", "createfa:fries", "rats:cheese", "rats:string_cheese", "rats:potato_kinishes");

		TagsProvider.TagAppender<Item> rawMeats = tag(ModTags.Items.RAW_MEATS)
				.add(BEEF, PORKCHOP, CHICKEN, RABBIT, MUTTON, COD, SALMON, TROPICAL_FISH, PUFFERFISH);
		addOptionalItemsTo(rawMeats, "createfa:ground_chicken", "createfa:ground_beef", "circus:clown", "rats:raw_rat", "evilcraft:flesh_humanoid", "evilcraft:flesh_werewolf");

		TagsProvider.TagAppender<Item> goodBiomass = tag(ModTags.Items.GOOD_BIOMASS)
				.add(BREAD, MUSHROOM_STEM, SUSPICIOUS_STEW, COCOA_BEANS, BAKED_POTATO, HONEYCOMB, MELON, PUMPKIN, DRIED_KELP_BLOCK, SEA_PICKLE, LILY_PAD,
						CARVED_PUMPKIN, WARPED_WART_BLOCK, NETHER_WART_BLOCK, RED_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK, SHROOMLIGHT, MUSHROOM_STEM);
		addOptionalItemsTo(goodBiomass, "create:bar_of_chocolate", "rats:blue_cheese");
		addOptionalTagsTo(goodBiomass, "forge:bread");

		TagsProvider.TagAppender<Item> cookedMeats = tag(ModTags.Items.COOKED_MEATS)
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_SALMON, COOKED_MUTTON, COOKED_COD, COOKED_RABBIT);
		addOptionalItemsTo(cookedMeats, "rats:cooked_rat", "createfa:schnitzel", "createfa:meatballs", "createfa:chicken_nuggets");

		TagsProvider.TagAppender<Item> superbBiomass = tag(ModTags.Items.SUPERB_BIOMASS)
				.add(CAKE, PUMPKIN_PIE, RABBIT_STEW, BEETROOT_SOUP, POISONOUS_POTATO, HAY_BLOCK);
		addOptionalItemsTo(superbBiomass, "create:sweet_roll", "create:chocolate_glazed_berries", "create:honeyed_apple", "createfa:cheeseburger", "createfa:hamburger",
				"rats:assorted_vegetables", "rats:rat_burger", "rats:potato_pancake", "rats:confit_byaldi", "createaddition:chocolate_cake");

		TagsProvider.TagAppender<Item> biomass = tag(ModTags.Items.BIOMASS);
		biomass.addOptionalTag(ModTags.Items.POOR_BIOMASS.location());
		biomass.addOptionalTag(ModTags.Items.AVERAGE_BIOMASS.location());
		biomass.addOptionalTag(ModTags.Items.GOOD_BIOMASS.location());
		biomass.addOptionalTag(ModTags.Items.SUPERB_BIOMASS.location());

//		tag(ItemTags.FENCES).getInternalBuilder().addTag(ModTags.Blocks.FLESHY_FENCES.getName(), BiomancyMod.MOD_ID);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
