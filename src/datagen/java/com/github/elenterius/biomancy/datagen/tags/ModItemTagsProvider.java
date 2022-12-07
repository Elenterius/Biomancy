package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.item.Items.*;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		addBiomancyTags();
		addMinecraftTags();
		addForgeTags();
	}

	private void addBiomancyTags() {
		createTag(ModTags.Items.SUGARS)
				.add(SUGAR, COOKIE, CAKE, HONEYCOMB, HONEY_BLOCK, HONEYCOMB_BLOCK, HONEY_BOTTLE, SWEET_BERRIES, COCOA_BEANS, APPLE)
				.addOptional("create:sweet_roll", "create:chocolate_glazed_berries", "create:honeyed_apple", "create:bar_of_chocolate")
				.addOptional("createaddition:chocolate_cake");

		createTag(ModTags.Items.POOR_BIOMASS)
				.addTag(ItemTags.FLOWERS, Tags.Items.SEEDS, ItemTags.LEAVES)
				.add(SWEET_BERRIES, SUGAR_CANE, KELP, DRIED_KELP, GRASS, SEAGRASS, VINE, FERN, BAMBOO)
				.addOptional("rats:contaminated_food");

		createTag(ModTags.Items.AVERAGE_BIOMASS)
				.addTag(ItemTags.SAPLINGS, Tags.Items.CROPS, Tags.Items.MUSHROOMS)
				.add(COOKIE, CACTUS, APPLE, CHORUS_FRUIT, MELON_SLICE, SPIDER_EYE, WARPED_FUNGUS, NETHER_SPROUTS, WEEPING_VINES,
						TWISTING_VINES, LARGE_FERN, TALL_GRASS, WARPED_ROOTS, CRIMSON_ROOTS, CRIMSON_FUNGUS)
				.addOptional("createfa:cheese", "createfa:mixed_egg", "createfa:fries")
				.addOptional("rats:cheese", "rats:string_cheese", "rats:potato_kinishes");

		createTag(ModTags.Items.RAW_MEATS)
				.add(BEEF, PORKCHOP, CHICKEN, RABBIT, MUTTON, COD, SALMON, TROPICAL_FISH, PUFFERFISH)
				.addOptional("createfa:ground_chicken", "createfa:ground_beef")
				.addOptional("rats:raw_rat")
				.addOptional("circus:clown")
				.addOptional("evilcraft:flesh_humanoid", "evilcraft:flesh_werewolf")
				.addOptionalTag("forge:raw_fishes")
				.addOptionalTag("forge:raw_bacon", "forge:raw_beef", "forge:raw_chicken", "forge:raw_pork", "forge:raw_mutton");

		createTag(ModTags.Items.GOOD_BIOMASS)
				.add(BREAD, MUSHROOM_STEM, SUSPICIOUS_STEW, COCOA_BEANS, BAKED_POTATO, HONEYCOMB, MELON, PUMPKIN, DRIED_KELP_BLOCK, SEA_PICKLE, LILY_PAD, CARVED_PUMPKIN,
						WARPED_WART_BLOCK, NETHER_WART_BLOCK, RED_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK, SHROOMLIGHT, MUSHROOM_STEM)
				.addOptional("create:bar_of_chocolate")
				.addOptional("rats:blue_cheese")
				.addOptionalTag("forge:bread");

		createTag(ModTags.Items.COOKED_MEATS)
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_SALMON, COOKED_MUTTON, COOKED_COD, COOKED_RABBIT)
				.addOptional("createfa:schnitzel", "createfa:meatballs", "createfa:chicken_nuggets")
				.addOptional("rats:cooked_rat");

		createTag(ModTags.Items.SUPERB_BIOMASS)
				.add(CAKE, PUMPKIN_PIE, RABBIT_STEW, BEETROOT_SOUP, POISONOUS_POTATO, HAY_BLOCK)
				.addOptional("create:sweet_roll", "create:chocolate_glazed_berries", "create:honeyed_apple")
				.addOptional("createfa:cheeseburger", "createfa:hamburger")
				.addOptional("createaddition:chocolate_cake")
				.addOptional("rats:assorted_vegetables", "rats:rat_burger", "rats:potato_pancake", "rats:confit_byaldi");

		createTag(ModTags.Items.BIOMASS)
				.addTag(ModTags.Items.POOR_BIOMASS, ModTags.Items.AVERAGE_BIOMASS, ModTags.Items.GOOD_BIOMASS, ModTags.Items.SUPERB_BIOMASS);
	}

	private void addMinecraftTags() {
		//		tag(ItemTags.FENCES).getInternalBuilder().addTag(ModTags.Blocks.FLESHY_FENCES.getName(), BiomancyMod.MOD_ID);
	}

	private void addForgeTags() {
		tag(ModTags.Items.FORGE_TOOLS_KNIVES).add(ModItems.BONE_CLEAVER.get());
	}

	protected EnhancedTagAppender<Item> createTag(TagKey<Item> tag) {
		return new EnhancedTagAppender<>(tag(tag));
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
