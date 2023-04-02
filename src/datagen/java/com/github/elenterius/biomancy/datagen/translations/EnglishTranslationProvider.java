package com.github.elenterius.biomancy.datagen.translations;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.init.client.ClientSetupHandler;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.world.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.world.item.ICustomTooltip;
import com.github.elenterius.biomancy.world.item.MaykerBannerPatternItem;
import com.github.elenterius.biomancy.world.item.SerumItem;
import com.github.elenterius.biomancy.world.item.state.LivingToolState;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class EnglishTranslationProvider extends AbstractTranslationProvider {

	public static final Marker LOG_MARKER = MarkerManager.getMarker("EnglishTranslationProvider");
	private static final String EMPTY_STRING = "";

	private List<Item> itemsToTranslate = List.of();
	private List<Block> blocksToTranslate = List.of();
	private List<Serum> serumsToTranslate = List.of();

	public EnglishTranslationProvider(DataGenerator gen) {
		super(gen, BiomancyMod.MOD_ID, "en_us");
	}

	@Override
	public void run(CachedOutput cache) throws IOException {
		itemsToTranslate = new ArrayList<>(ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(item -> !(item instanceof BlockItem)).toList());
		blocksToTranslate = new ArrayList<>(ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList());
		serumsToTranslate = new ArrayList<>(ModSerums.SERUMS.getEntries().stream().map(RegistryObject::get).toList());

		super.run(cache);

		if (!itemsToTranslate.isEmpty()) {
			for (Item item : itemsToTranslate) {
				BiomancyMod.LOGGER.warn(LOG_MARKER, () -> "Missing translation for item '%s'".formatted(item));
			}
			throw new IllegalStateException("Missing translation for %d items".formatted(itemsToTranslate.size()));
		}

		if (!blocksToTranslate.isEmpty()) {
			for (Block block : blocksToTranslate) {
				BiomancyMod.LOGGER.warn(LOG_MARKER, () -> "Missing translation for block '%s'".formatted(block));
			}
			throw new IllegalStateException("Missing translation for %d blocks".formatted(blocksToTranslate.size()));
		}

		if (!serumsToTranslate.isEmpty()) {
			for (Serum serum : serumsToTranslate) {
				BiomancyMod.LOGGER.warn(LOG_MARKER, () -> "Missing translation for serum '%s'".formatted(serum));
			}
			throw new IllegalStateException("Missing translation for %d serums".formatted(serumsToTranslate.size()));
		}
	}

	private void addBannerPatternItem(RegistryObject<MaykerBannerPatternItem> supplier, String name, String description) {
		MaykerBannerPatternItem item = supplier.get();
		add(item.getDescriptionId(), name);
		add(item.getDisplayName(), description);
		itemsToTranslate.remove(item);
	}

	private <T extends Serum> void addSerum(Supplier<T> supplier, String name) {
		add(supplier.get(), name);
	}

	private void add(Serum serum, String name) {
		add(serum.getTranslationKey(), name);
		serumsToTranslate.remove(serum);
	}

	private void addHudMessage(String id, String text) {
		add("msg.biomancy." + id, text);
	}

	private void addTooltip(String id, String text) {
		add("tooltip.biomancy." + id, text);
	}

	private <T extends BaseProjectile> void addDeathMessage(Supplier<EntityType<T>> supplier, String directCause, String indirectCause) {
		ResourceLocation resourceLocation = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(supplier.get()));
		String msgId = resourceLocation.toString().replace(":", ".");
		add("death.attack." + msgId, directCause);
		add("death.attack." + msgId + ".item", indirectCause);
	}

	private <T extends Item> void addItem(Supplier<T> supplier, String name, String tooltip) {
		T item = supplier.get();
		add(item.getDescriptionId(), name);

		if (item instanceof ICustomTooltip iTooltip) {
			add(iTooltip.getTooltipKey(new ItemStack(item)), tooltip);
		}
		else {
			add(TextComponentUtil.getItemTooltipKey(item), tooltip);
		}

		itemsToTranslate.remove(item);
	}

	private <T extends SerumItem> void addSerumItem(Supplier<T> supplier, String serumName, String tooltip) {
		T item = supplier.get();
		ItemStack stack = new ItemStack(item);

		add(item.getSerum(stack), serumName);

		add(item.getDescriptionId(stack), serumName);
		add(item.getTooltipKey(stack), tooltip);
		itemsToTranslate.remove(item);
	}

	@Override
	public void add(Item item, String name) {
		add(item.getDescriptionId(), name);

		itemsToTranslate.remove(item);
	}

	@Override
	public void add(ItemStack stack, String name) {
		add(stack.getDescriptionId(), name);

		itemsToTranslate.remove(stack.getItem());
	}

	private <T extends Block> void addBlock(Supplier<T> supplier, String name, String tooltip) {
		T block = supplier.get();
		add(block.getDescriptionId(), name);
		add(TextComponentUtil.getItemTooltipKey(block), tooltip);

		blocksToTranslate.remove(block);
	}

	@Override
	public void add(Block block, String name) {
		add(block.getDescriptionId(), name);
		blocksToTranslate.remove(block);
	}

	@Override
	protected void addTranslations() {
		add(BiomancyMod.CREATIVE_TAB.getDisplayName(), "Biomancy 2");
		add(ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.getCategory(), "Biomancy 2 Mod");
		add(ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.getName(), "Default Item Action");

		addItemTranslations();
		addBlockTranslations();
		addEntityTranslations();
		addEnchantmentTranslations();
		addStatusEffectTranslations();
		addSerumTranslations();
		addDamageTranslations();

		addTooltip("empty", "Empty");
		addTooltip("contains", "Contains: %1$s");
		addTooltip("nutrients_fuel", "Nutrients");
		addTooltip("nutrients_consumes", "Consumes %1$s u");
		addTooltip("consumption", "Consumption");
		addTooltip("bile_fuel", "Bile");
		addTooltip("contains_unique_dna", "Contains Unique Genetic Sequences");
		addTooltip("press_button_to", "Press %1$s to %2$s");

		addTooltip("owner", "Owner: %1$s");

		addTooltip("slots", "Slots");
		addTooltip("drops_from", "Drops from");
		addTooltip("and_more", "and more...");

		addTooltip("action.show_info", "show info");
		addTooltip("action.self_inject", "inject yourself");
		addTooltip("action.self_extract", "extract from yourself");
		addTooltip("action.open_inventory", "open its inventory");
		addTooltip("action.activate", "activate");
		addTooltip("action.deactivate", "deactivate");
		addTooltip("action.reload", "reload");
		addTooltip("action.cycle", "cycle");

		addTooltip("fire_rate", "Fire Rate");
		addTooltip("accuracy", "Accuracy");
		addTooltip("ammo", "Ammo");
		addTooltip("reload_time", "Reload Time");
		addTooltip("projectile_damage", "Projectile Damage");

		add(LivingToolState.getTooltipTranslationKey(), "The Tool is %1$s");
		add(LivingToolState.DORMANT.getTranslationKey(), "Dormant");
		add(LivingToolState.AWAKE.getTranslationKey(), "Awake");
		add(LivingToolState.EXALTED.getTranslationKey(), "Exalted");

		addHudMessage("not_sleepy", "You don't feel sleepy...");
		addHudMessage("set_behavior_command", "%1$s will now execute the %2$s command");
		addHudMessage("not_enough_ammo", "Not enough Ammo");
		addHudMessage("not_enough_nutrients", "Not enough Nutrients");

		add(ClientTextUtil.getCtrlKey(), "ctrl");
		add(ClientTextUtil.getAltKey(), "alt");
		add(ClientTextUtil.getShiftKey(), "shift");
		add(ClientTextUtil.getRightMouseKey(), "right mouse");

		add("jei.biomancy.recipe.bio_lab", "Bio-Lab Recipes");
		add("jei.biomancy.recipe.decomposer", "Decomposer Recipes");
		add("jei.biomancy.recipe.digester", "Digester Recipes");
		add("jei.biomancy.recipe.bio_forge", "Bio-Forge Recipes");

		add("jer.biomancy.requiresDespoilOrBoneCleaver", "Requires Despoil Enchantment or Bone Cleaver (#forge:tools/knives)");
		add("jer.biomancy.requiresBoneCleaver", "Requires Bone Cleaver");

		addSoundTranslations();
		addBannerPatternTranslations();
	}

	private void addSoundTranslations() {
		addSound(ModSoundEvents.UI_BUTTON_CLICK, "Click Fleshy Button");
		addSound(ModSoundEvents.UI_MENU_OPEN, "Open Fleshy Menu");
		addSound(ModSoundEvents.UI_RADIAL_MENU_OPEN, "Open Radial Menu");

		addSound(ModSoundEvents.INJECTOR_INJECT, "Injecting Serum");
		addSound(ModSoundEvents.INJECTOR_FAIL, "Injection Failed");
		addSound(ModSoundEvents.MARROW_DRINK, "Slurping Bone Marrow");

		addSound(ModSoundEvents.FLESH_BLOCK_HIT, "Hit Fleshy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_PLACE, "Place Fleshy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_STEP, "Step on Fleshy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_BREAK, "Break Fleshy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_FALL, "Fall on Fleshy Block");

		addSound(ModSoundEvents.FLESH_DOOR_OPEN, "Open Fleshy Door");
		addSound(ModSoundEvents.FLESH_DOOR_CLOSE, "Close Fleshy Door");

		addSound(ModSoundEvents.FLESHKIN_CHEST_OPEN, "Open Fleshy Chest");
		addSound(ModSoundEvents.FLESHKIN_CHEST_CLOSE, "Close Fleshy Chest");
		addSound(ModSoundEvents.FLESHKIN_CHEST_BITE_ATTACK, "Fleshy Chest Bite Attack");
		addSound(ModSoundEvents.FLESHKIN_CHEST_NO, "Fleshy Chest says No");

		addSound(ModSoundEvents.CREATOR_SPAWN_MOB, "Primordial Cradle Spawns a Mob");
		addSound(ModSoundEvents.CREATOR_BECAME_FULL, "Primordial Cradle became full");
		addSound(ModSoundEvents.CREATOR_EAT, "Primordial Cradle Eating");
		addSound(ModSoundEvents.CREATOR_NO, "Primordial Cradle says No");

		addSound(ModSoundEvents.UI_STORAGE_SAC_OPEN, "Open Menu of Storage Sac");
		addSound(ModSoundEvents.UI_BIO_FORGE_OPEN, "Open Menu of Bio-Forge");
		addSound(ModSoundEvents.UI_BIO_FORGE_SELECT_RECIPE, "Select Recipe in Bio-Forge");
		addSound(ModSoundEvents.UI_BIO_FORGE_TAKE_RESULT, "Craft Item in Bio-Forge");

		addSound(ModSoundEvents.DECOMPOSER_CRAFTING, "Decomposer is crafting");
		addSound(ModSoundEvents.UI_DECOMPOSER_OPEN, "Open Menu of Decomposer");
		addSound(ModSoundEvents.DECOMPOSER_EAT, "Decomposer is eating");
		addSound(ModSoundEvents.DECOMPOSER_CRAFTING_RANDOM, "Decomposer is burping");
		addSound(ModSoundEvents.DECOMPOSER_CRAFTING_COMPLETED, "Decomposer finished crafting");

		addSound(ModSoundEvents.BIO_LAB_CRAFTING, "Bio-Lab is crafting");
		addSound(ModSoundEvents.UI_BIO_LAB_OPEN, "Open Menu of Bio-Lab");
		addSound(ModSoundEvents.BIO_LAB_CRAFTING_RANDOM, "Bio-Lab is slurping");
		addSound(ModSoundEvents.BIO_LAB_CRAFTING_COMPLETED, "Bio-Lab finished crafting");

		addSound(ModSoundEvents.DIGESTER_CRAFTING, "Digester is crafting");
		addSound(ModSoundEvents.UI_DIGESTER_OPEN, "Open Menu of Digester");
		addSound(ModSoundEvents.DIGESTER_CRAFTING_RANDOM, "Digester is burping");
		addSound(ModSoundEvents.DIGESTER_CRAFTING_COMPLETED, "Digester finished crafting");

		addSound(ModSoundEvents.FLESH_BLOB_JUMP, "Flesh Blob Jump");
		addSound(ModSoundEvents.FLESH_BLOB_HURT, "Flesh Blob Hurt");
	}

	private void addDamageTranslations() {
		addDeathMessage(ModDamageSources.CHEST_BITE, "%1$s tried touching a chest but was eaten instead");
		addDeathMessage(ModDamageSources.CREATOR_SPIKES, "%1$s angered the Maykrs and was impaled by bone spikes");

		addDeathMessage(ModDamageSources.FALL_ON_BONE_SPIKE, "%1$s fell on a sharp bone");
		addDeathMessage(ModDamageSources.IMPALED_BY_BONE_SPIKE, "%1$s was impaled by a sharp bone");

		addDeathMessage(ModDamageSources.CORROSIVE_ACID, "%1$s died from severe acid burns");

		addDeathMessage(ModEntityTypes.TOOTH_PROJECTILE, "%1$s was forcefully implanted with teeth by %2$s", "%1$s received a lethal dental implant by %2$s using %3$s");
		addDeathMessage(ModEntityTypes.CORROSIVE_ACID_PROJECTILE, "%1$s was doused with corrosive acid by %2$s", "%1$s was showered in corrosive acid by %2$s using %3$s");
	}

	private void addSerumTranslations() {
		//serums that are not tied to a specific item
		add(Serum.EMPTY, "INVALID SERUM");
	}

	private void addStatusEffectTranslations() {
		addEffect(ModMobEffects.CORROSIVE, "Corrosive Acid");
		addEffect(ModMobEffects.ARMOR_SHRED, "Armor Shred");
		addEffect(ModMobEffects.ESSENCE_ANEMIA, "Essence Anemia");
		addEffect(ModMobEffects.LIBIDO, "Fertility");
		addEffect(ModMobEffects.FLESH_EATING_DISEASE, "Necrotizing Flesh");
	}

	private void addEnchantmentTranslations() {
		addEnchantment(ModEnchantments.DESPOIL, "Despoil");
		addEnchantment(ModEnchantments.ANESTHETIC, "Anesthetic Touch");
		//		addEnchantment(ModEnchantments.QUICK_SHOT, "Quick Shot");
		//		addEnchantment(ModEnchantments.MAX_AMMO, "Max Ammo");
	}

	private void addBannerPatternTranslations() {
		addBannerPattern(ModBannerPatterns.MASCOT_BASE, "Mascot Base");
		addBannerPattern(ModBannerPatterns.MASCOT_OUTLINE, "Mascot Outline");
		addBannerPattern(ModBannerPatterns.MASCOT_ACCENT, "Mascot Accent");
	}

	private void addItemTranslations() {
		addItem(ModItems.MOB_SINEW, "Sinew", "Tissue made of Elastic Fibers.");
		addItem(ModItems.MOB_FANG, "Sharp Fang", "Cutting tooth made of Bone tissue rich in Bio-Minerals.");
		addItem(ModItems.MOB_CLAW, "Sharp Claw", "Hardened Claw made of tough fibers and rich in Bio-Minerals.");
		addItem(ModItems.MOB_MARROW, "Bone Marrow", "Marrow extracted from the bones of your victims. Rich in Hormones and Bio-Minerals. ");
		addItem(ModItems.WITHERED_MOB_MARROW, "Withered Bone Marrow", "Withered Marrow, some dark fluid is oozing out of it.\nMaybe you should suck it dry...");

		addItem(ModItems.GENERIC_MOB_GLAND, "Bile Gland", "Organ sac filled with bile to the brim.");
		addItem(ModItems.TOXIN_GLAND, "Toxin Gland", "A organ full of toxin, maybe you should drink this...");
		addItem(ModItems.VOLATILE_GLAND, "Volatile Gland", "Gland filled with a reactive fluid.\nSeems safe to drink...");

		addItem(ModItems.FLESH_BITS, "Flesh Bits", "A tiny bit of flesh... Used as a primary crafting ingredient.");
		addItem(ModItems.BONE_FRAGMENTS, "Bone Fragments", "A tiny fragment of bone... Provides rigidity and shape to flesh. Useful for assembling mechanical parts.");
		addItem(ModItems.TOUGH_FIBERS, "Tough Fibers", "Fibrous tissue that is very tough and rigid. Useful for crafting things that need more resilience.");
		addItem(ModItems.ELASTIC_FIBERS, "Elastic Fibers", "Fibrous tissue that is sturdy but has rubbery properties. It appears like a good material to imitate muscles.");
		addItem(ModItems.MINERAL_FRAGMENT, "Mineral Fragment", "Component only obtained in Decomposer.\nUsed to toughen and harden fibers.");
		addItem(ModItems.GEM_FRAGMENTS, "Gem Fragments", "Used to enhance hardened fibers to their maximum resilience, very expensive.");
		addItem(ModItems.BIO_LUMENS, "Bioluminescent Goo", "Basic component, only obtained via the Decomposer, usually alongside biotic matter.\n\nUsed as a cosmetic upgrade to make things glow.");
		addItem(ModItems.ORGANIC_MATTER, "Biotic Matter", "Moist organic material, extracted from Plants. With its dark and fibrous texture almost seems like humus.");
		addItem(ModItems.EXOTIC_DUST, "Exotic Dust", "Exotic substance, maybe magical. Byproduct from decomposing magical things.");
		addItem(ModItems.STONE_POWDER, "Lithic Powder", "Very Basic Crafting component, usually obtained as a byproduct from decomposing things.");

		addItem(ModItems.NUTRIENTS, "Nutrients", "Very hard pellets rich in energy, almost look like vitamin pills. Occasional byproduct from decomposing plants, but mainly obtained from digesting plants.");
		addItem(ModItems.NUTRIENT_PASTE, "Nutrient Paste", "Nutrients combined with Biotic Matter to moisten the hard pellets and get a paste. Almost looks like yellowish cake, a useful source of energy.");
		addItem(ModItems.NUTRIENT_BAR, "Nutrient Bar", "Compressed Nutrient Paste into the shape of an edible Bar.");

		addItem(ModItems.REGENERATIVE_FLUID, "Regenerative Fluid", "Fluid that has regenerative properties, used to concoct healing additives.");
		addItem(ModItems.WITHERING_OOZE, "Withering Ooze", "Corrosive extract, might have a bio-alchemical use.");
		addItem(ModItems.HORMONE_SECRETION, "Hormone Secretion", "A fluid extract very rich in hormones. Potent material for making drugs.");
		addItem(ModItems.TOXIN_EXTRACT, "Toxin Extract", "Toxic fluid");
		addItem(ModItems.VOLATILE_FLUID, "Volatile Fluid", "Combustible extract, needs further processing to be more dangerous.");
		addItem(ModItems.BILE, "Bile", "Organic base material which is often used in bio-alchemy.");

		addItem(ModItems.BONE_CLEAVER, "Bone Cleaver", "A specialized bone tool for getting additional special loot from its victims.");
		addItem(ModItems.GLASS_VIAL, "Glass Vial", "Glass is transparent... very brittle, yet strong and rigid. Very high alchemical resistance, seems perfect for holding reactive substances.");
		addItem(ModItems.LIVING_FLESH, "Living Flesh", "It's alive!\nBut it looks too dumb to be the brain of a mob. You should turn it into a construct.");
		addItem(ModItems.PRIMORDIAL_LIVING_FLESH, "Primordial Flesh", "A ominous piece of primal flesh oozing primordial soup. It looks hungry...");
		addItem(ModItems.PRIMORDIAL_LIVING_OCULUS, "Primordial Oculus", "A ominous eye is gazing at you...");
		addItem(ModItems.GUIDE_BOOK, "Primordial Index", "Ask questions?");
		addItem(ModItems.CREATOR_MIX, "Exotic Flesh Mix", "A meal made for the cradle... just not for you.");
		addItem(ModItems.INJECTOR, "Bio-Injector", "A simple device which utilizes a razor sharp needle to quickly and forcefully inject Serums into Mobs and Players.");
		addItem(ModItems.FERTILIZER, "Bio-Alchemical Fertilizer", "Fertilizer that induces hyper-growth in plants, even for reeds, cactus, nether wart and chorus plant.");

		addItem(ModItems.LONG_CLAWS, "Living Long Claws", "Claws made of living flesh\n\nFeed it with nutrients via right click in the inventory.");
		addItem(ModItems.DEV_ARM_CANNON, "[Dev Tool] Arm Cannon", "Creative/Developer Tool for testing projectiles.");
		addItem(ModItems.BILE_SPITTER, "[EXPERIMENTAL] Bile Spitter", "WIP: \"living\" projectile weapon that shoots corrosive bile.\nIntended behaviour: charge it like a bow to increase damage & size of projectile.");

		addItem(ModItems.BIO_EXTRACTOR, "Bio-Extractor", EMPTY_STRING);
		addItem(ModItems.ESSENCE, "Essence", EMPTY_STRING);

		addItem(ModItems.ORGANIC_COMPOUND, "Organic Compound", "Slimy substance made of bile infused with nutrients.");
		addItem(ModItems.UNSTABLE_COMPOUND, "Unstable Compound", "Very unstable and reactive substance. Seems like it will combust if it comes in contact with other things.");
		addItem(ModItems.EXOTIC_COMPOUND, "Exotic Compound", "Substance of a questionable nature, comprised of exotic material and other trace elements.");
		addItem(ModItems.GENETIC_COMPOUND, "Genetic Compound", "Cocktail of various hormones, nutrients and organic elements. Compound that seems useful for producing potent stimulants.");
		addItem(ModItems.CORROSIVE_ADDITIVE, "Corrosive Additive", "An highly corrosive fluid that seems useful for alchemically burning away organic material or weakening the bonds of complex substances.");
		addItem(ModItems.HEALING_ADDITIVE, "Healing Additive", "An highly concentrated substance that is used to imbue it properties to other compounds.");

		addSerumItem(ModItems.AGEING_SERUM, "Ageing Serum", "Forces the maturation of young Mobs into Adults. Some rare Mobs may turn into Elders.");
		addSerumItem(ModItems.REJUVENATION_SERUM, "Rejuvenation Serum", "Reverses the maturation of Mobs, in most cases they turn into children.");
		addSerumItem(ModItems.ENLARGEMENT_SERUM, "Enlargement Serum", "Induces growth in Slimes, Magma Cubes and Flesh Blobs.\n\n(If Pehkui is installed you can enlarge yourself and all Mobs)");
		addSerumItem(ModItems.SHRINKING_SERUM, "Shrinking Serum", "Shrinks Slimes, Magma Cubes and Flesh Blobs.\n\n(If Pehkui is installed you can shrink yourself and all Mobs)");

		addSerumItem(ModItems.CLEANSING_SERUM, "Cleansing Serum", "Burns away all foreign substances inside a living entity.\nVery effective for sticky status effects that refuse to be healed with milk.");
		addSerumItem(ModItems.BREEDING_STIMULANT, "Breeding Stimulant", "Makes Animals hyper-fertile, making them able to constantly reproduce for a short time.");
		addSerumItem(ModItems.ABSORPTION_BOOST, "Absorption Stimulant", "Grants stackable absorption health points for Mobs and Players.");
		addSerumItem(ModItems.INSOMNIA_CURE, "Insomnia Cure", "Resets the last slept time, no need to sleep for quite some time.");

		addBannerPatternItem(ModItems.MASCOT_BANNER_PATTERNS, "Banner Pattern", "Biomancy Mascot");

		addItem(ModItems.HUNGRY_FLESH_BLOB_SPAWN_EGG, "Hungry Flesh Blob Spawn Egg");
		addItem(ModItems.FLESH_BLOB_SPAWN_EGG, "Flesh Blob Spawn Egg");
		addItem(ModItems.MALIGNANT_FLESH_BLOB_SPAWN_EGG, "Malignant Flesh Blob Spawn Egg");
	}

	private void addBlockTranslations() {
		addBlock(ModBlocks.PRIMORDIAL_CRADLE, "Primordial Cradle", "Offer adequate Tributes to the cradle and summon forth primordial messengers of exquisite flesh.");

		addBlock(ModBlocks.DECOMPOSER, "Decomposer", "A bio-machine that deconstructs items into their base components.\nThe bio-machine consumes nutrients to function.");
		addBlock(ModBlocks.DIGESTER, "Digester", "A machine born from flesh that converts food and plants into nutrients.");
		addBlock(ModBlocks.BIO_FORGE, "Bio-Forge", "Crafting Station");
		addBlock(ModBlocks.BIO_LAB, "Bio-Lab", "Bio-alchemical Brewer");

		addBlock(ModBlocks.VOICE_BOX, "Modular Larynx", EMPTY_STRING);
		addBlock(ModBlocks.TONGUE, "Tongue", "Extracts up to 3 items of the same type every 24 ticks from containers its attached to and drops them on the ground.");
		addBlock(ModBlocks.MAW_HOPPER, "Maw Hopper", "A fleshy sister of the hopper. Transfers up to 16 items at a time.");

		addBlock(ModBlocks.STORAGE_SAC, "Storage Sac", "Cheap Shulker-like storage sac that also works like a bundle.");

		addBlock(ModBlocks.FLESHKIN_CHEST, "Fleshkin Chest", """
				A fleshkin forged into the shape of a chest with sharp teeth and a resilient stomach allowing it to keeps its contents when mined.
				Only its master may open and takes its content without repercussion.

				It's fangs look awfully sharp...""");
		addBlock(ModBlocks.FLESHKIN_DOOR, "Fleshkin Door", EMPTY_STRING);
		addBlock(ModBlocks.FLESHKIN_TRAPDOOR, "Fleshkin Trap Door", EMPTY_STRING);
		addBlock(ModBlocks.FLESHKIN_PRESSURE_PLATE, "Fleshkin Pressure Sensor", """
				Fleshkin pancake. Yummy...
				It has two behaviors, either it only activates for its owner or it only works for everyone else.
										
				Sneak click to change its behavior.""");

		addBlock(ModBlocks.FLESH, "Flesh Block", "A generic block of flesh... Don't bother me with this!");
		addBlock(ModBlocks.FLESH_SLAB, "Flesh Slab", "A generic slab of flesh... Don't bother me with this!");
		addBlock(ModBlocks.FLESH_STAIRS, "Flesh Stairs", "Stairs made of generic flesh... Don't bother me with this!");
		addBlock(ModBlocks.FLESH_WALL, "Flesh Wall", "A generic wall of flesh.");
		addBlock(ModBlocks.PACKED_FLESH, "Packed Flesh Block", "Tenacious Block of flesh. Is it tough enough?");
		addBlock(ModBlocks.PACKED_FLESH_SLAB, "Packed Flesh Slab", "Tenacious Slab of flesh. Is it tough enough?");
		addBlock(ModBlocks.PACKED_FLESH_STAIRS, "Packed Flesh Stairs", "Stairs made of tenacious flesh. Is it tough enough?");
		addBlock(ModBlocks.PACKED_FLESH_WALL, "Packed Flesh Wall", "Tenacious wall of flesh.");

		addBlock(ModBlocks.FLESH_FENCE, "Flesh Fence", "Fence made of bones and flesh...");
		addBlock(ModBlocks.FLESH_FENCE_GATE, "Flesh Fence Gate", "Fence gate made of bones and flesh...");
		addBlock(ModBlocks.FLESH_IRIS_DOOR, "Flesh Iris-Door", "Trapdoor-like iris door made of flesh...");
		addBlock(ModBlocks.FLESH_DOOR, "Flesh Door", "A sliding door made of flesh...");
		addBlock(ModBlocks.FULL_FLESH_DOOR, "Wide Flesh Door", "A wide sliding door made of flesh...");
		addBlock(ModBlocks.BONE_SPIKE, "Bone Spike", "A dangerous spike made of reinforced bone, colliding with it will hurt.");
		addBlock(ModBlocks.FLESH_LADDER, "Flesh Ladder", "Ladder mainly made of bones and a little bit of flesh...");
		addBlock(ModBlocks.BIO_LANTERN, "Bioluminescent Lantern", "Biological light source.");
		addBlock(ModBlocks.TENDON_CHAIN, "Tendon Chain", "Chain made of tendons.");

		addBlock(ModBlocks.PRIMAL_FLESH, "Primal Flesh Block", "Primitive and pure, you better not touch this with your dirty paws.");
		addBlock(ModBlocks.PRIMAL_FLESH_SLAB, "Primal Flesh Slab", "Primitive and pure, you better not touch this with your dirty paws.");
		addBlock(ModBlocks.PRIMAL_FLESH_STAIRS, "Primal Flesh Stairs", "Stairs made of primal flesh. Feels primitive and pure...");
		addBlock(ModBlocks.CORRUPTED_PRIMAL_FLESH, "Corrupted Primal Flesh", "It's no longer pure... Don't deny it. Your dirty paws caused this!");
		addBlock(ModBlocks.MALIGNANT_FLESH, "Malignant Flesh Block", "Looks dangerous, you better not touch this block!");
		addBlock(ModBlocks.MALIGNANT_FLESH_SLAB, "Malignant Flesh Slab", "Looks dangerous, you better not touch this slab!");
		addBlock(ModBlocks.MALIGNANT_FLESH_STAIRS, "Malignant Flesh Stairs", "Stairs made of malignant flesh. Looks diseased...");
		addBlock(ModBlocks.MALIGNANT_FLESH_VEINS, "Malignant Flesh Veins", "Looks dangerous, you better not touch these veins!");
	}

	private void addEntityTranslations() {
		addEntityType(ModEntityTypes.HUNGRY_FLESH_BLOB, "Hungry Flesh Blob");
		addEntityType(ModEntityTypes.FLESH_BLOB, "Flesh Blob");
		addEntityType(ModEntityTypes.MALIGNANT_FLESH_BLOB, "Malignant Flesh Blob");
	}

}
