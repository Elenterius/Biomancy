package com.github.elenterius.biomancy.datagen.tags;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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

		createTag(ModTags.Items.POOR_BIOMASS);
		createTag(ModTags.Items.AVERAGE_BIOMASS);
		createTag(ModTags.Items.GOOD_BIOMASS);
		createTag(ModTags.Items.SUPERB_BIOMASS);
		createTag(ModTags.Items.BIOMASS)
				.addTag(ModTags.Items.POOR_BIOMASS, ModTags.Items.AVERAGE_BIOMASS, ModTags.Items.GOOD_BIOMASS, ModTags.Items.SUPERB_BIOMASS);

		createTag(ModTags.Items.RAW_MEATS)
				.add(BEEF, PORKCHOP, CHICKEN, RABBIT, MUTTON, COD, SALMON, TROPICAL_FISH, PUFFERFISH)
				.add(AMItemRegistry.MOOSE_RIBS.get(), AMItemRegistry.KANGAROO_MEAT.get(), AMItemRegistry.RAW_CATFISH.get(), AMItemRegistry.BLOBFISH.get(), AMItemRegistry.MAGGOT.get())
				.addOptional("createfa:ground_chicken", "createfa:ground_beef")
				.addOptional("rats:raw_rat")
				.addOptional("circus:clown")
				.addOptional("evilcraft:flesh_humanoid", "evilcraft:flesh_werewolf")
				.addOptionalTag("forge:raw_fishes")
				.addOptionalTag("forge:raw_bacon", "forge:raw_beef", "forge:raw_chicken", "forge:raw_pork", "forge:raw_mutton");

		createTag(ModTags.Items.COOKED_MEATS)
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_SALMON, COOKED_MUTTON, COOKED_COD, COOKED_RABBIT)
				.add(AMItemRegistry.COOKED_MOOSE_RIBS.get())
				.addOptional("createfa:schnitzel", "createfa:meatballs", "createfa:chicken_nuggets")
				.addOptional("rats:cooked_rat");
	}

	private void addMinecraftTags() {
		//		tag(ItemTags.FENCES).getInternalBuilder().addTag(ModTags.Blocks.FLESHY_FENCES.getName(), BiomancyMod.MOD_ID);
	}

	private void addForgeTags() {
		tag(ModTags.Items.FORGE_TOOLS_KNIVES).add(ModItems.BONE_CLEAVER.get());
	}

	protected EnhancedTagAppender<Item> createTag(TagKey<Item> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ITEMS);
	}

	private void analyze(EnhancedTagAppender<Item> tagAppender, String name) {
		List<FoodProperties> foodProperties = tagAppender.getInternalBuilder().build().stream()
				.filter(tagEntry -> !tagEntry.isTag())
				.map(tagEntry -> tagAppender.forgeRegistry().getValue(tagEntry.getId()))
				.filter(Objects::nonNull)
				.filter(Item::isEdible)
				.map(item -> item.getFoodProperties(new ItemStack(item), null))
				.toList();

		if (foodProperties.isEmpty()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("Biomass Stats"), () -> "Could not analyze Food Properties of tag " + name);
			return;
		}

		long averageNutrition = foodProperties.stream().mapToLong(FoodProperties::getNutrition).sum() / foodProperties.size();
		double averageSaturationModifier = foodProperties.stream().mapToDouble(FoodProperties::getSaturationModifier).sum() / foodProperties.size();

		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("Biomass Stats"), () -> "%s Averages%n Nutrition: %d%n Saturation Modifier: %f".formatted(name, averageNutrition, averageSaturationModifier));
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
