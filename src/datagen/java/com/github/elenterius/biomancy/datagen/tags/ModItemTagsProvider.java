package com.github.elenterius.biomancy.datagen.tags;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.Items.*;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTagLookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	protected EnhancedTagAppender<Item> createTag(TagKey<Item> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ITEMS);
	}

	private static TagKey<Item> forgeTag(String path) {
		return ItemTags.create(new ResourceLocation("forge", path));
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
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
				.add(ACItemRegistry.TRILOCARIS_TAIL.get())
				.addOptional("createfa:ground_chicken", "createfa:ground_beef")
				.addOptional("rats:raw_rat")
				.addOptional("circus:clown")
				.addOptional("evilcraft:flesh_humanoid", "evilcraft:flesh_werewolf")
				.addOptionalTag("forge:raw_fishes")
				.addOptionalTag("forge:raw_bacon", "forge:raw_beef", "forge:raw_chicken", "forge:raw_pork", "forge:raw_mutton");

		createTag(ModItemTags.COOKED_MEATS)
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_SALMON, COOKED_MUTTON, COOKED_COD, COOKED_RABBIT)
				.add(AMItemRegistry.COOKED_MOOSE_RIBS.get())
				.add(ACItemRegistry.COOKED_TRILOCARIS_TAIL.get(), ACBlockRegistry.COOKED_DINOSAUR_CHOP.get().asItem())
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
				.add(NAME_TAG, BUNDLE)
				.addTag(ItemTags.MUSIC_DISCS)
				.add(ELYTRA)
				.addTag(Tags.Items.ARMORS, Tags.Items.TOOLS)
				.addTag(Tags.Items.ORES_NETHERITE_SCRAP, Tags.Items.INGOTS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE)
				.addTag(forgeTag("shulker_boxes"));
	}

	private void addMinecraftTags() {
		//		tag(ItemTags.FENCES).getInternalBuilder().addTag(ModTags.Blocks.FLESHY_FENCES.getName(), BiomancyMod.MOD_ID);

		createTag(ItemTags.DOORS)
				.add(ModItems.FLESH_DOOR.get(), ModItems.FULL_FLESH_DOOR.get());

		createTag(ItemTags.TRAPDOORS)
				.add(ModItems.FLESH_IRIS_DOOR.get());
	}

	private void addForgeTags() {
		//		tag(ModItemTags.FORGE_TOOLS_KNIVES);

		TagKey<Item> clawsTag = forgeTag("tools/claws");
		createTag(clawsTag)
				.add(ModItems.RAVENOUS_CLAWS.get());

		TagKey<Item> swordsTag = forgeTag("tools/swords");
		createTag(swordsTag)
				.add(ModItems.DESPOIL_SICKLE.get());
		createTag(ItemTags.SWORDS)
				.add(ModItems.DESPOIL_SICKLE.get());

		createTag(Tags.Items.TOOLS_SHIELDS)
				.add(ModItems.THORN_SHIELD.get());

		createTag(Tags.Items.TOOLS)
				.addTag(clawsTag)
				.addTag(swordsTag)
				.add(ModItems.INJECTOR.get(), ModItems.ESSENCE_EXTRACTOR.get());

		createTag(Tags.Items.CHESTS).add(ModItems.FLESHKIN_CHEST.get());

		EnhancedTagAppender<Item> shulkerBoxes = createTag(forgeTag("shulker_boxes"));
		for (Item item : ForgeRegistries.ITEMS) {
			if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {
				shulkerBoxes.add(item);
			}
		}
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
