package com.github.elenterius.biomancy.datagen.tags;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.item.Items.*;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	private static TagKey<Item> forgeTag(String path) {
		return ItemTags.create(new ResourceLocation("forge", path));
	}

	@Override
	protected void addTags() {
		addBiomancyTags();
		addMinecraftTags();
		addForgeTags();
	}

	private void addBiomancyTags() {

		createTag(ModItemTags.SUGARS)
				.add(SUGAR, COOKIE, CAKE, HONEYCOMB, HONEY_BLOCK, HONEYCOMB_BLOCK, HONEY_BOTTLE, SWEET_BERRIES, COCOA_BEANS, APPLE)
				.addOptional("create:sweet_roll", "create:chocolate_glazed_berries", "create:honeyed_apple", "create:bar_of_chocolate")
				.addOptional("createaddition:chocolate_cake");

		createTag(ModItemTags.RAW_MEATS)
				.add(BEEF, PORKCHOP, CHICKEN, RABBIT, MUTTON, COD, SALMON, TROPICAL_FISH, PUFFERFISH)
				.add(AMItemRegistry.MOOSE_RIBS.get(), AMItemRegistry.KANGAROO_MEAT.get(), AMItemRegistry.RAW_CATFISH.get(), AMItemRegistry.BLOBFISH.get(), AMItemRegistry.MAGGOT.get())
				.addOptional("createfa:ground_chicken", "createfa:ground_beef")
				.addOptional("rats:raw_rat")
				.addOptional("circus:clown")
				.addOptional("evilcraft:flesh_humanoid", "evilcraft:flesh_werewolf")
				.addOptionalTag("forge:raw_fishes")
				.addOptionalTag("forge:raw_bacon", "forge:raw_beef", "forge:raw_chicken", "forge:raw_pork", "forge:raw_mutton");

		createTag(ModItemTags.COOKED_MEATS)
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_SALMON, COOKED_MUTTON, COOKED_COD, COOKED_RABBIT)
				.add(AMItemRegistry.COOKED_MOOSE_RIBS.get())
				.addOptional("createfa:schnitzel", "createfa:meatballs", "createfa:chicken_nuggets")
				.addOptional("rats:cooked_rat");

		createTag(ModItemTags.CLAWS)
				.add(ModItems.MOB_CLAW.get())
				.add(AMItemRegistry.DROPBEAR_CLAW.get());

		createTag(ModItemTags.FANGS)
				.add(ModItems.MOB_FANG.get())
				.add(AMItemRegistry.BONE_SERPENT_TOOTH.get());

		createTag(ModItemTags.CANNOT_BE_EATEN_BY_CRADLE)
				.add(DRAGON_EGG, SPAWNER, HEART_OF_THE_SEA)
				.add(NAME_TAG)
				.addTag(ItemTags.MUSIC_DISCS)
				.add(ELYTRA)
				.addTag(Tags.Items.ARMORS, Tags.Items.TOOLS)
				.add(SHULKER_BOX)
				.addTag(Tags.Items.ORES_NETHERITE_SCRAP, Tags.Items.INGOTS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE);
	}

	private void addMinecraftTags() {
		//		tag(ItemTags.FENCES).getInternalBuilder().addTag(ModTags.Blocks.FLESHY_FENCES.getName(), BiomancyMod.MOD_ID);
	}

	private void addForgeTags() {
		//		tag(ModItemTags.FORGE_TOOLS_KNIVES);

		TagKey<Item> clawsTag = forgeTag("tools/claws");
		tag(clawsTag)
				.add(ModItems.RAVENOUS_CLAWS.get());

		tag(Tags.Items.TOOLS_SWORDS)
				.add(ModItems.DESPOIL_SICKLE.get(), ModItems.TOXICUS.get());

		tag(Tags.Items.TOOLS)
				.addTag(clawsTag)
				.add(ModItems.INJECTOR.get(), ModItems.BIO_EXTRACTOR.get());
	}

	protected EnhancedTagAppender<Item> createTag(TagKey<Item> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ITEMS);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
