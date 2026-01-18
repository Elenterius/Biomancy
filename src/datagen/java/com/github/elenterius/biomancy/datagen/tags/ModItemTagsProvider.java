package com.github.elenterius.biomancy.datagen.tags;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
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
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.Items.*;

public class ModItemTagsProvider extends ItemTagsProvider {

	public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTagLookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	private static TagKey<Item> forgeTag(String path) {
		return ItemTags.create(new ResourceLocation("forge", path));
	}

	private static TagKey<Item> conventionalTag(String path) {
		return ItemTags.create(new ResourceLocation("c", path));
	}

	private static TagKey<Item> biomancyTag(String path) {
		return ItemTags.create(BiomancyMod.rl(path));
	}

	protected EnhancedTagAppender<Item> createTag(TagKey<Item> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ITEMS);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		addBiomancyTags();
		addMinecraftTags();
		addForgeTags();
		addConventionalTags();
	}

	private void addBiomancyTags() {
		class FarmersDelightItems extends vectorwing.farmersdelight.common.registry.ModItems {} //alias workaround

		createTag(ModItemTags.SUGARS)
				.addTag(conventionalTag("foods/cookie"), conventionalTag("foods/candy"))
				.add(SUGAR, CAKE, HONEYCOMB, HONEY_BLOCK, HONEYCOMB_BLOCK, HONEY_BOTTLE, SWEET_BERRIES, COCOA_BEANS, APPLE)
				.add(
						FarmersDelightItems.HOT_COCOA.get(),
						FarmersDelightItems.GLOW_BERRY_CUSTARD.get(), FarmersDelightItems.MELON_JUICE.get(),
						FarmersDelightItems.CAKE_SLICE.get(), FarmersDelightItems.APPLE_PIE_SLICE.get(), FarmersDelightItems.CHOCOLATE_PIE_SLICE.get(), FarmersDelightItems.SWEET_BERRY_CHEESECAKE_SLICE.get()
				)
				.addOptional("create:sweet_roll", "create:chocolate_glazed_berries", "create:honeyed_apple", "create:bar_of_chocolate")
				.addOptional("createaddition:chocolate_cake");

		createTag(ModItemTags.FRESH_RAW_MEATS)
				.addTag(conventionalTag("foods/raw_meat"))
				.remove(ROTTEN_FLESH);

		createTag(ModItemTags.COOKED_MEATS)
				.addTag(conventionalTag("foods/cooked_meat"));

		createTag(ModItemTags.CANNOT_BE_EATEN_BY_CRADLE)
				.add(DRAGON_EGG, SPAWNER, HEART_OF_THE_SEA)
				.add(NAME_TAG, BUNDLE)
				.addTag(ItemTags.MUSIC_DISCS)
				.add(ELYTRA)
				.addTag(Tags.Items.ARMORS, Tags.Items.TOOLS)
				.addTag(Tags.Items.ORES_NETHERITE_SCRAP, Tags.Items.INGOTS_NETHERITE, Tags.Items.STORAGE_BLOCKS_NETHERITE)
				.addTag(forgeTag("shulker_boxes"));

		createTag(ModItemTags.CANNOT_BE_DIGESTED_IN_ACID)
				.add(ModItems.NUTRIENT_PASTE.get())
				.add(ModItems.NUTRIENT_BAR.get())
				.add(ModItems.LIVING_FLESH.get());

		Set<Item> advancedTypes = Set.of(
				ModItems.ACID_GRENADE.get(),
				ModItems.DECAY_GRENADE.get(),
				ModItems.VOLATILE_GRENADE.get(),
				ModItems.TOXIN_GRENADE.get(),

				ModItems.FERTILIZER.get(),
				ModItems.AGEING_SERUM.get(),
				ModItems.CLEANSING_SERUM.get(),
				ModItems.ENLARGEMENT_SERUM.get(),
				ModItems.FRENZY_SERUM.get(),
				ModItems.REJUVENATION_SERUM.get(),
				ModItems.SHRINKING_SERUM.get(),
				ModItems.POTION_SERUM.get(),
				ModItems.BREEDING_STIMULANT.get(),
				ModItems.INSOMNIA_CURE.get(),
				ModItems.ABSORPTION_BOOST.get(),

				ModItems.FLESH_SPIKE.get(),
				ModItems.ONEWAY_MEMBRANE.get(),
				ModItems.IMPERMEABLE_MEMBRANE.get(),
				ModItems.IMPERMEABLE_MEMBRANE_PANE.get(),
				ModItems.BABY_PERMEABLE_MEMBRANE.get(),
				ModItems.BABY_PERMEABLE_MEMBRANE_PANE.get(),
				ModItems.ADULT_PERMEABLE_MEMBRANE.get(),
				ModItems.ADULT_PERMEABLE_MEMBRANE_PANE.get(),
				ModItems.UNDEAD_PERMEABLE_MEMBRANE.get(),
				ModItems.UNDEAD_PERMEABLE_MEMBRANE_PANE.get(),

				ModItems.CHISELED_FLESH_BLOCK.get(),
				ModItems.FLESH_PILLAR.get(),
				ModItems.ORNATE_FLESH_BLOCK.get(),
				ModItems.ORNATE_FLESH_SLAB.get(),
				ModItems.TUBULAR_FLESH_BLOCK.get(),

				ModItems.INJECTOR.get(),
				ModItems.ESSENCE_EXTRACTOR.get(),
				ModItems.ESSENCE.get(),
				ModItems.MAW_HOPPER.get(),
				ModItems.TONGUE.get(),
				ModItems.MODULAR_LARYNX.get(),
				ModItems.JUMP_PAD.get(),
				ModItems.CHRYSALIS.get()
		);
		Set<Item> specialTypes = Set.of(
				ModItems.TAB_ICON.get(),
				ModItems.DEV_ARM_CANNON.get(),
				ModItems.DEV_GUIDE_BOOK.get(),

				ModItems.BIO_FORGE.get(),
				ModItems.BIO_LAB.get(),
				ModItems.DECOMPOSER.get(),
				ModItems.DIGESTER.get(),

				ModItems.FLESHKIN_CHEST.get(),
				ModItems.FLESHKIN_PRESSURE_PLATE.get(),
				ModItems.BIOMETRIC_MEMBRANE.get(),

				ModItems.RAVENOUS_CLAWS.get(),
				ModItems.IMPALER.get(),
				ModItems.THORN_SHIELD.get(),
				ModItems.CAUSTIC_GUNBLADE.get(),

				ModItems.ACOLYTE_ARMOR_HELMET.get(),
				ModItems.ACOLYTE_ARMOR_CHESTPLATE.get(),
				ModItems.ACOLYTE_ARMOR_LEGGINGS.get(),
				ModItems.ACOLYTE_ARMOR_BOOTS.get(),
				ModItems.WARRIOR_ARMOR_HELMET.get(),
				ModItems.WARRIOR_ARMOR_CHESTPLATE.get(),
				ModItems.WARRIOR_ARMOR_LEGGINGS.get(),
				ModItems.WARRIOR_ARMOR_BOOTS.get()
		);
		Set<Item> primordialTypes = Set.of(
				ModItems.DESPOIL_SICKLE.get(),
				ModItems.LIVING_FLESH.get(),
				ModItems.CREATOR_MIX.get(),

				ModItems.PRIMORDIAL_FLESH_BLOB_SPAWN_EGG.get(),
				ModItems.PRIMORDIAL_HUNGRY_FLESH_BLOB_SPAWN_EGG.get(),

				ModItems.PRIMORDIAL_CRADLE.get(),
				ModItems.PRIMORDIAL_CORE.get(),
				ModItems.PRIMAL_BLOOM.get(),
				ModItems.BLOOMBERRY.get(),
				ModItems.BLOOMLIGHT.get(),
				ModItems.PRIMORDIAL_BIO_LANTERN.get(),
				ModItems.PRIMAL_ORIFICE.get(),
				ModItems.PRIMAL_PERMEABLE_MEMBRANE.get(),
				ModItems.PRIMAL_PERMEABLE_MEMBRANE_PANE.get(),

				ModItems.PRIMAL_FLESH_BLOCK.get(),
				ModItems.PRIMAL_FLESH_WALL.get(),
				ModItems.PRIMAL_FLESH_STAIRS.get(),
				ModItems.PRIMAL_FLESH_SLAB.get(),
				ModItems.POROUS_PRIMAL_FLESH_BLOCK.get(),
				ModItems.POROUS_PRIMAL_FLESH_WALL.get(),
				ModItems.POROUS_PRIMAL_FLESH_STAIRS.get(),
				ModItems.POROUS_PRIMAL_FLESH_SLAB.get(),
				ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(),
				ModItems.SMOOTH_PRIMAL_FLESH_WALL.get(),
				ModItems.SMOOTH_PRIMAL_FLESH_STAIRS.get(),
				ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get(),
				ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get(),
				ModItems.FIBROUS_PRIMAL_FLESH_WALL.get(),
				ModItems.FIBROUS_PRIMAL_FLESH_STAIRS.get(),
				ModItems.FIBROUS_PRIMAL_FLESH_SLAB.get()
		);

		Set<Item> types = new HashSet<>();
		types.addAll(advancedTypes);
		types.addAll(specialTypes);
		types.addAll(primordialTypes);

		createTag(biomancyTag("normal_type")).add(ModItems.stream().filter(item -> !types.contains(item)).sorted(Comparator.comparing(Item::getDescriptionId)));
		createTag(biomancyTag("advanced_type")).add(advancedTypes.stream().sorted(Comparator.comparing(Item::getDescriptionId)));
		createTag(biomancyTag("special_type")).add(specialTypes.stream().sorted(Comparator.comparing(Item::getDescriptionId)));
		createTag(biomancyTag("primordial_type")).add(primordialTypes.stream().sorted(Comparator.comparing(Item::getDescriptionId)));
	}

	private void addMinecraftTags() {
		//		tag(ItemTags.FENCES).getInternalBuilder().addTag(ModTags.Blocks.FLESHY_FENCES.getName(), BiomancyMod.MOD_ID);

		createTag(ItemTags.DOORS)
				.add(ModItems.FLESH_DOOR.get(), ModItems.FULL_FLESH_DOOR.get());

		createTag(ItemTags.TRAPDOORS)
				.add(ModItems.FLESH_IRIS_DOOR.get());
	}

	private void addForgeTags() {
		createTag(Tags.Items.STRING)
				.add(ModItems.MOB_SINEW.get());

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

	/// uses conventional tags introduced in neo-forge/fabric for minecraft 1.21+
	///
	/// @see <a href="https://github.com/neoforged/NeoForge/tree/1.21.x/src/generated/resources/data/c/tags/item">NeoForge Repo</a>
	private void addConventionalTags() {
		class FarmersDelightItems extends vectorwing.farmersdelight.common.registry.ModItems {} //alias workaround
		class AlexsDelightItems extends com.ncpbails.alexsdelight.item.ModItems {} //alias workaround

		createTag(ModItemTags.C_WITHER_BONES)
				.addOptionalTag("forge:bones/wither");

		createTag(ModItemTags.C_CLAWS)
				.addOptionalTag("forge:claws")
				.add(ModItems.MOB_CLAW.get())
				.add(AMItemRegistry.DROPBEAR_CLAW.get())
				.add(IafItemRegistry.HIPPOGRYPH_TALON.get());

		createTag(ModItemTags.C_FANGS)
				.addOptionalTag("forge:fangs")
				.add(ModItems.MOB_FANG.get())
				.add(AMItemRegistry.BONE_SERPENT_TOOTH.get())
				.add(IafItemRegistry.SERPENT_FANG.get(), IafItemRegistry.HYDRA_FANG.get());

		createTag(conventionalTag("foods/candy"))
				.addOptionalTag("c:foods/candies", "forge:candies", "forge:candy");

		createTag(conventionalTag("foods/cookie"))
				.addOptionalTag("c:foods/cookies", "forge:cookies", "forge:cookie")
				.add(COOKIE)
				.add(FarmersDelightItems.SWEET_BERRY_COOKIE.get(), FarmersDelightItems.HONEY_COOKIE.get());

		createTag(ModItemTags.C_RAW_PORK)
				.addTag(forgeTag("raw_pork"))
				.add(FarmersDelightItems.HAM.get());

		createTag(conventionalTag("foods/raw_meat"))
				.addOptionalTag("c:foods/raw_meats", "forge:raw_meats", "forge:raw_meat")
				.addOptionalTag("forge:raw_bacon", "forge:raw_beef", "forge:raw_chicken", "forge:raw_pork", "forge:raw_mutton")
				.addTag(ModItemTags.C_RAW_PORK)
				.add(BEEF, PORKCHOP, CHICKEN, RABBIT, MUTTON)
				.add(FarmersDelightItems.HAM.get())
				.add(AMItemRegistry.MOOSE_RIBS.get(), AMItemRegistry.KANGAROO_MEAT.get(), AMItemRegistry.MAGGOT.get())
				.add(
						AlexsDelightItems.RAW_BISON.get(), AlexsDelightItems.BISON_MINCE.get(), AlexsDelightItems.KANGAROO_SHANK.get(), AlexsDelightItems.LOOSE_MOOSE_RIB.get(),
						AlexsDelightItems.RAW_BUNFUNGUS.get(), AlexsDelightItems.RAW_BUNFUNGUS_DRUMSTICK.get()
				)
				.add(ACBlockRegistry.DINOSAUR_CHOP.get().asItem())
				.add(IafItemRegistry.ICE_DRAGON_FLESH.get(), IafItemRegistry.FIRE_DRAGON_FLESH.get(), IafItemRegistry.LIGHTNING_DRAGON_FLESH.get())
				.addOptional("createfa:ground_chicken", "createfa:ground_beef")
				.addOptional("rats:raw_rat")
				.addOptional("circus:clown")
				.addOptional("evilcraft:flesh_humanoid", "evilcraft:flesh_werewolf")
		;

		createTag(conventionalTag("foods/cooked_meat"))
				.addOptionalTag("c:foods/cooked_meats", "forge:cooked_meat", "forge:cooked_meats")
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_RABBIT, COOKED_MUTTON)
				.add(FarmersDelightItems.SMOKED_HAM.get())
				.add(AMItemRegistry.COOKED_MOOSE_RIBS.get(), AMItemRegistry.COOKED_KANGAROO_MEAT.get())
				.add(
						AlexsDelightItems.COOKED_BISON.get(), AlexsDelightItems.BISON_PATTY.get(), AlexsDelightItems.COOKED_KANGAROO_SHANK.get(),
						AlexsDelightItems.COOKED_LOOSE_MOOSE_RIB.get(), AlexsDelightItems.COOKED_BUNFUNGUS.get(),
						AlexsDelightItems.COOKED_BUNFUNGUS_DRUMSTICK.get(), AlexsDelightItems.COOKED_CENTIPEDE_LEG.get()
				)
				.add(ACBlockRegistry.COOKED_DINOSAUR_CHOP.get().asItem())
				.addOptional("createfa:schnitzel", "createfa:meatballs", "createfa:chicken_nuggets")
				.addOptional("rats:cooked_rat")
		;

		createTag(conventionalTag("foods/raw_fish"))
				.addOptionalTag("c:foods/raw_fishes", "forge:raw_fishes", "forge:raw_fish")
				.add(COD, SALMON, TROPICAL_FISH, PUFFERFISH)
				.add(
						AMItemRegistry.FLYING_FISH.get(), AMItemRegistry.RAW_CATFISH.get(), AMItemRegistry.BLOBFISH.get(),
						AMItemRegistry.LOBSTER_TAIL.get()
				)
				.add(
						ACItemRegistry.TRILOCARIS_TAIL.get(), ACItemRegistry.LANTERNFISH.get(), ACItemRegistry.TRIPODFISH.get(),
						ACItemRegistry.RADGILL.get(),
						ACItemRegistry.DEEP_SEA_SUSHI_ROLL.get()
				)
				.add(AlexsDelightItems.RAW_CATFISH_SLICE.get())
		;

		createTag(conventionalTag("foods/cooked_fish"))
				.addOptionalTag("c:foods/cooked_fishes", "forge:cooked_fishes", "forge:cooked_fish")
				.add(COOKED_COD, COOKED_SALMON)
				.add(AMItemRegistry.COOKED_CATFISH.get(), AMItemRegistry.COOKED_LOBSTER_TAIL.get())
				.add(AlexsDelightItems.COOKED_CATFISH_SLICE.get())
				.add(
						ACItemRegistry.COOKED_TRILOCARIS_TAIL.get(), ACItemRegistry.COOKED_LANTERNFISH.get(), ACItemRegistry.COOKED_TRIPODFISH.get(),
						ACItemRegistry.COOKED_RADGILL.get()
				)
		;
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
