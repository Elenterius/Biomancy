package com.github.elenterius.biomancy.datagen.lang;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.livingtool.LivingToolState;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.init.client.ClientSetupHandler;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.MaykerBannerPatternItem;
import com.github.elenterius.biomancy.item.SerumItem;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class EnglishLangProvider extends AbstractLangProvider {

	public static final Marker LOG_MARKER = MarkerFactory.getMarker("EnglishTranslationProvider");
	private static final String EMPTY_STRING = "";

	private List<Item> itemsToTranslate = List.of();
	private List<Block> blocksToTranslate = List.of();
	private List<Serum> serumsToTranslate = List.of();

	public EnglishLangProvider(PackOutput packOutput) {
		super(packOutput, BiomancyMod.MOD_ID, "en_us");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		itemsToTranslate = new ArrayList<>(ModItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(item -> !(item instanceof BlockItem)).toList());
		blocksToTranslate = new ArrayList<>(ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).toList());
		serumsToTranslate = new ArrayList<>(ModSerums.SERUMS.getEntries().stream().map(RegistryObject::get).toList());
		return super.run(cache);
	}

	@Override
	public boolean hasMissingTranslations() {
		boolean isAnyMissing = false;

		if (!itemsToTranslate.isEmpty()) {
			for (Item item : itemsToTranslate) {
				LOGGER.error(LOG_MARKER, "Missing {} translation for item '{}'", languageLocale, item);
			}
			isAnyMissing = true;
		}

		if (!blocksToTranslate.isEmpty()) {
			for (Block block : blocksToTranslate) {
				LOGGER.error(LOG_MARKER, "Missing {} translation for block '{}'", languageLocale, block);
			}
			isAnyMissing = true;
		}

		if (!serumsToTranslate.isEmpty()) {
			for (Serum serum : serumsToTranslate) {
				LOGGER.error(LOG_MARKER, "Missing {} translation for serum '{}'", languageLocale, serum);
			}
			isAnyMissing = true;
		}

		return isAnyMissing;
	}

	private void addBioForgeTab(Supplier<BioForgeTab> supplier, String name) {
		add(supplier.get(), name);
	}

	private void add(BioForgeTab tab, String name) {
		add(tab.translationKey(), name);
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
		add(serum.getNameTranslationKey(), name);
		serumsToTranslate.remove(serum);
	}

	private void addHudMessage(String id, String text) {
		add("msg.biomancy." + id, text);
	}

	private void addTooltip(String id, String text) {
		add("tooltip.biomancy." + id, text);
	}

	private void addAbility(String id, String text) {
		add("ability.biomancy." + id, text);
	}

	private <T extends Item> void addItem(Supplier<T> supplier, String name, String tooltip) {
		T item = supplier.get();
		add(item.getDescriptionId(), name);

		if (item instanceof ItemTooltipStyleProvider provider) {
			add(provider.getTooltipKey(new ItemStack(item)), tooltip);
		}
		else {
			add(TextComponentUtil.getItemTooltipKey(item), tooltip);
		}

		itemsToTranslate.remove(item);
	}

	private <T extends SerumItem> void addSerumItem(Supplier<T> supplier, String serumName, String tooltip) {
		T item = supplier.get();
		ItemStack stack = new ItemStack(item);

		add(item.getSerum(), serumName);

		add(item.getDescriptionId(stack), serumName);
		add(item.getTooltipKey(stack), tooltip);
		itemsToTranslate.remove(item);
	}

	private <T extends Enchantment> void addEnchantment(Supplier<T> supplier, String name, String tooltip) {
		T enchantment = supplier.get();
		add(enchantment.getDescriptionId(), name);
		add(enchantment.getDescriptionId() + ".desc", tooltip);
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
		add(BiomancyMod.CREATIVE_TAB.get().getDisplayName(), "Biomancy 2");
		add(ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.getCategory(), "Biomancy 2 Mod");
		add(ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.getName(), "Default Item Action");

		addItemTranslations();
		addBlockTranslations();
		addFluidTranslations();
		addEntityTranslations();
		addEnchantmentTranslations();
		addPotionTranslations();
		addStatusEffectTranslations();
		addSerumTranslations();
		addDamageTranslations();

		addTooltip("empty", "Empty");
		addTooltip("contains", "Contains: %1$s");
		addTooltip("nutrients_fuel", "Nutrients");
		addTooltip("primal_energy", "Primal Energy");
		addTooltip("nutrients_consumes", "Consumes %1$s u");
		addTooltip("consumption", "Consumption");
		addTooltip("bile_fuel", "Bile");
		addTooltip("blood_charge", "Blood Charge");
		addTooltip("contains_unique_dna", "Contains Unique Genetic Sequences");
		addTooltip("press_button_to", "Hold %1$s to %2$s");

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
		addTooltip("action.switch_mode", "switch mode");
		addTooltip("action.enable_awakened_mode", "activate Awakened Mode");
		addTooltip("action.disable_awakened_mode", "cancel Awakened Mode");

		addAbility("bleed_proc", "Eviscerating Slash");
		addAbility("bleed_proc.desc", "When triggered, adds one Bleed Stack (max 2)");
		addAbility("blood_explosion", "Blood Explosion");
		addAbility("blood_explosion.desc", "On Bleed Stack, deals 10% of max health as damage");
		addAbility("acid_coat", "Acid Coat");
		addAbility("acid_coat.desc", """
				On Right-Click coats the blade in Acid for 10 seconds.
				Full-Strength Attacks inflict one stack of Armor Shred for 4s and one stack of Corrosion for 2s.
				Critical Hits inflict two stacks of Armor Shred for 4s.""");
		addAbility("thorny_hide", "Thorny Hide");
		addAbility("thorny_hide.desc", "On successful block, deals thorns damage to the attacker");
		addAbility("fleshkin_affinity", "Fleshkin Affinity");
		addAbility("fleshkin_affinity.desc", "-5% Primordial Cradle Hostility");
		addAbility("bio_alchemical_epidermis", "Bio-Alchemical Epidermis");
		addAbility("bio_alchemical_epidermis.desc", "+15% Resistance to external application of harmful Status-Effects\n -25% Injection Resistance\n -25% Injection Damage");

		addTooltip("fire_rate", "Fire Rate");
		addTooltip("accuracy", "Accuracy");
		addTooltip("ammo", "Ammo");
		addTooltip("reload_time", "Reload Time");
		addTooltip("projectile_damage", "Projectile Damage");

		add(LivingToolState.getTooltipTranslationKey(), "The Tool is %1$s");
		add(LivingToolState.BROKEN.getTranslationKey(), "Broken");
		add(LivingToolState.DORMANT.getTranslationKey(), "Dormant");
		add(LivingToolState.AWAKENED.getTranslationKey(), "Awakened");

		addHudMessage("not_sleepy", "You don't feel sleepy...");
		//addHudMessage("set_behavior_command", "%1$s will now execute the %2$s command");
		addHudMessage("not_enough_ammo", "Not enough Ammo");
		addHudMessage("not_enough_nutrients", "Not enough Nutrients");
		addHudMessage("not_enough_blood_charge", "Not enough Blood Charge");
		addHudMessage("mob_too_old", "The Mob is too old");
		addHudMessage("already_full", "Already Full");
		addHudMessage("failed_to_store_mob", "Failed to store the mob");
		addHudMessage("failed_to_spawn_mob", "Failed to spawn the stored mob");
		addHudMessage("item_interaction_not_allowed", "Item interaction is not allowed");

		add(ClientTextUtil.getCtrlKey(), "ctrl");
		add(ClientTextUtil.getAltKey(), "alt");
		add(ClientTextUtil.getShiftKey(), "shift");
		add(ClientTextUtil.getRightMouseKey(), "right mouse");

		addBioForgeTab(ModBioForgeTabs.SEARCH, "All");
		addBioForgeTab(ModBioForgeTabs.BUILDING_BLOCKS, "Building Blocks");
		addBioForgeTab(ModBioForgeTabs.MACHINES, "Machines");
		addBioForgeTab(ModBioForgeTabs.TOOLS, "Tools");
		addBioForgeTab(ModBioForgeTabs.COMPONENTS, "Components");
		addBioForgeTab(ModBioForgeTabs.MISC, "Misc");

		add("jei.biomancy.recipe.bio_lab", "Bio-Lab Recipes");
		add("jei.biomancy.recipe.decomposer", "Decomposer Recipes");
		add("jei.biomancy.recipe.digester", "Digester Recipes");
		add("jei.biomancy.recipe.bio_forge", "Bio-Forge Recipes");

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

		addSound(ModSoundEvents.BONY_FLESH_BLOCK_HIT, "Hit Bony Flesh Block");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_PLACE, "Place Bony Flesh Block");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_STEP, "Step on Bony Flesh Block");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_BREAK, "Break Bony Flesh Block");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_FALL, "Fall on Bony Flesh Block");

		addSound(ModSoundEvents.FLESH_DOOR_OPEN, "Open Fleshy Door");
		addSound(ModSoundEvents.FLESH_DOOR_CLOSE, "Close Fleshy Door");

		addSound(ModSoundEvents.CLAWS_ATTACK_STRONG, "Strong Claw Attack");
		addSound(ModSoundEvents.CLAWS_ATTACK_BLEED_PROC, "Eviscerating Slash triggers");
		addSound(ModSoundEvents.FLESHKIN_NO, "Fleshkin disagrees");
		addSound(ModSoundEvents.FLESHKIN_EAT, "Fleshkin eats");
		addSound(ModSoundEvents.FLESHKIN_BREAK, "Fleshkin Break");
		addSound(ModSoundEvents.FLESHKIN_BECOME_DORMANT, "Fleshkin becomes Dormant");
		addSound(ModSoundEvents.FLESHKIN_BECOME_AWAKENED, "Fleshkin becomes Awakened");

		addSound(ModSoundEvents.FLESHKIN_CHEST_OPEN, "Open Fleshy Chest");
		addSound(ModSoundEvents.FLESHKIN_CHEST_CLOSE, "Close Fleshy Chest");
		addSound(ModSoundEvents.FLESHKIN_CHEST_BITE_ATTACK, "Fleshy Chest Bite Attack");

		addSound(ModSoundEvents.CRADLE_SPAWN_MOB, "Primordial Cradle Spawns a Mob");
		addSound(ModSoundEvents.CRADLE_SPAWN_PRIMORDIAL_MOB, "Primordial Cradle Spawns a Primal Mob");
		addSound(ModSoundEvents.CRADLE_BECAME_FULL, "Primordial Cradle became full");
		addSound(ModSoundEvents.CRADLE_EAT, "Primordial Cradle Eating");
		addSound(ModSoundEvents.CRADLE_NO, "Primordial Cradle disagrees");
		addSound(ModSoundEvents.CRADLE_CRAFTING_RANDOM, "Primordial Cradle is summoning");
		addSound(ModSoundEvents.CRADLE_SPIKE_ATTACK, "Primordial Cradle attacks");

		addSound(ModSoundEvents.UI_STORAGE_SAC_OPEN, "Open Menu of Storage Sac");
		addSound(ModSoundEvents.UI_BIO_FORGE_OPEN, "Open Menu of Bio-Forge");
		addSound(ModSoundEvents.UI_BIO_FORGE_SELECT_RECIPE, "Select Recipe in Bio-Forge");
		addSound(ModSoundEvents.UI_BIO_FORGE_TAKE_RESULT, "Craft Item in Bio-Forge");

		addSound(ModSoundEvents.DECOMPOSER_CRAFTING, "Decomposer is crafting");
		addSound(ModSoundEvents.UI_DECOMPOSER_OPEN, "Open Menu of Decomposer");
		addSound(ModSoundEvents.DECOMPOSER_EAT, "Decomposer is eating");
		addSound(ModSoundEvents.DECOMPOSER_CRAFTING_RANDOM, "Decomposer burps");
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
		addSound(ModSoundEvents.FLESH_BLOB_DEATH, "Flesh Blob Death");
		addSound(ModSoundEvents.FLESH_BLOB_AMBIENT, "Flesh Blob Ambient");
		addSound(ModSoundEvents.FLESH_BLOB_GROWL, "Flesh Blob Growls");
		addSound(ModSoundEvents.FLESH_BLOB_MEW_PURR, "Flesh Blob Purrs");
		addSound(ModSoundEvents.FLESH_COW_AMBIENT, "Flesh Cow moos");
		addSound(ModSoundEvents.FLESH_COW_HURT, "Flesh Cow hurts");
		addSound(ModSoundEvents.FLESH_COW_DEATH, "Flesh Cow dies");
		addSound(ModSoundEvents.FLESH_SHEEP_AMBIENT, "Flesh Sheep baahs");
		addSound(ModSoundEvents.FLESH_SHEEP_HURT, "Flesh Sheep hurts");
		addSound(ModSoundEvents.FLESH_SHEEP_DEATH, "Flesh Sheep dies");
		addSound(ModSoundEvents.FLESH_PIG_AMBIENT, "Flesh Pig oinks");
		addSound(ModSoundEvents.FLESH_PIG_HURT, "Flesh Pig hurts");
		addSound(ModSoundEvents.FLESH_PIG_DEATH, "Flesh Pig dies");
		addSound(ModSoundEvents.FLESH_CHICKEN_AMBIENT, "Flesh Chicken clucks");
		addSound(ModSoundEvents.FLESH_CHICKEN_HURT, "Flesh Chicken hurts");
		addSound(ModSoundEvents.FLESH_CHICKEN_DEATH, "Flesh Chicken dies");
	}

	private void addDamageTranslations() {
		addDeathMessage(ModDamageTypes.CHEST_BITE, "%1$s tried opening a chest, but was eaten instead");
		addDeathMessage(ModDamageTypes.PRIMORDIAL_SPIKES, "%1$s was impaled by primordial spikes");

		addDeathMessage(ModDamageTypes.FALL_ON_SPIKE,
				"%1$s fell on a sharp spike",
				"%1$s was thrown into a spike pit by %2$s",
				"%1$s was skewered by a sharp spike by %2$s using %3$s"
		);
		addDeathMessage(ModDamageTypes.IMPALED_BY_SPIKE,
				"%1$s was impaled by a sharp spike",
				"%1$s was impaled onto a sharp spike by %2$s",
				"%1$s was impaled onto a sharp spike by %2$s using %3$s"
		);

		addDeathMessage(ModDamageTypes.CORROSIVE_ACID,
				"%1$s succumbed to severe acid burns",
				"%1$s was doused with corrosive acid by %2$s",
				"%1$s was showered in corrosive acid by %2$s using %3$s"
		);
		addDeathMessage(ModDamageTypes.BLEED,
				"%1$s perished from severe blood loss",
				"%1$s was drained of blood by %2$s",
				"%1$s was blood let by %2$s using %3$s"
		);
		addDeathMessage(ModDamageTypes.SLASH,
				"%1$s was shredded to ribbons",
				"%1$s was ripped apart by %2$s",
				"%1$s was eviscerate by %2$s using %3$s"
		);

		addDeathMessage(ModDamageTypes.TOOTH_PROJECTILE, "[WIP]",
				"[WIP] %1$s was forcefully implanted with teeth by %2$s",
				"[WIP] %1$s received a lethal dental implant by %2$s using %3$s");
	}

	private void addSerumTranslations() {
		//serums that are not tied to a specific item
		add(Serum.EMPTY, "ERROR: INVALID SERUM");
	}

	private void addPotionTranslations() {
		String suffix = ModPotions.GASTRIC_JUICE.getId().toLanguageKey();
		add(Items.POTION.getDescriptionId() + ".effect." + suffix, "Gastric Juice");
		add(Items.SPLASH_POTION.getDescriptionId() + ".effect." + suffix, "Gastric Splash-Juice");
		add(Items.LINGERING_POTION.getDescriptionId() + ".effect." + suffix, "Gastric Linger-Juice");
		add(Items.TIPPED_ARROW.getDescriptionId() + ".effect." + suffix, "Arrow of Gastric Juice");

		suffix = ModPotions.PRIMORDIAL_INFESTATION.getId().toLanguageKey();
		add(Items.POTION.getDescriptionId() + ".effect." + suffix, "Potion of Primordial Infestation");
		add(Items.SPLASH_POTION.getDescriptionId() + ".effect." + suffix, "Splash Potion of Primordial Infestation");
		add(Items.LINGERING_POTION.getDescriptionId() + ".effect." + suffix, "Lingering Potion of Primordial Infestation");
		add(Items.TIPPED_ARROW.getDescriptionId() + ".effect." + suffix, "Arrow of Primordial Infestation");
	}

	private void addStatusEffectTranslations() {
		addEffect(ModMobEffects.CORROSIVE, "Corrosive Acid");
		addEffect(ModMobEffects.ARMOR_SHRED, "Armor Shred");
		addEffect(ModMobEffects.LIBIDO, "Fertility");
		addEffect(ModMobEffects.BLEED, "Bleed");
		addEffect(ModMobEffects.ESSENCE_ANEMIA, "Essence Anemia");
		addEffect(ModMobEffects.DROWSY, "Drowsy");
		addEffect(ModMobEffects.DESPOIL, "Despoil");
		addEffect(ModMobEffects.FRENZY, "Frenzy");
		addEffect(ModMobEffects.WITHDRAWAL, "Withdrawal");
		addEffect(ModMobEffects.PRIMORDIAL_INFESTATION, "Primordial Infestation");
	}

	private void addEnchantmentTranslations() {
		addEnchantment(ModEnchantments.DESPOIL, "Despoil", "When killing something with a weapon that has this enchantment, they will drop additional special loot.");
		addEnchantment(ModEnchantments.ANESTHETIC, "Anesthetic Touch", "Prevents the Damage & Aggro caused by injecting mobs with the Injector. Mobs won't know who attacked them, and animals won't panic anymore.");
		addEnchantment(ModEnchantments.SURGICAL_PRECISION, "Surgical Precision", "Improves the quality and success of surgeries or extraction of essences from mobs.");
		addEnchantment(ModEnchantments.PARASITIC_METABOLISM, "Parasitic Metabolism", "When any held or equipped living item is damaged, consumes every 1.5 seconds nutrition from a healthy players food bar to restore nutrients.");
		addEnchantment(ModEnchantments.SELF_FEEDING, "Self-Feeding", "When any held or equipped living item is damaged, consumes every 1.5 seconds nutrients from the players hotbar.");
	}

	private void addBannerPatternTranslations() {
		addBannerPattern(ModBannerPatterns.MASCOT_BASE, "Mascot Base");
		addBannerPattern(ModBannerPatterns.MASCOT_OUTLINE, "Mascot Outline");
		addBannerPattern(ModBannerPatterns.MASCOT_ACCENT, "Mascot Accent");
	}

	private void addItemTranslations() {
		addItem(ModItems.MOB_SINEW, "Sinew", "Tissue made of Elastic Fibers.");
		addItem(ModItems.MOB_FANG, "Sharp Fang", "Cutting tooth made of tissue rich in minerals.");
		addItem(ModItems.MOB_CLAW, "Sharp Claw", "Hardened Claw made of tough fibers and rich in minerals.");
		addItem(ModItems.MOB_MARROW, "Bone Marrow", "Marrow extracted from the bones of your victims. Rich in Hormones and minerals. ");
		addItem(ModItems.WITHERED_MOB_MARROW, "Withered Bone Marrow", "Withered Marrow, some dark fluid is oozing out of it.\nIt looks tasty, maybe you should suck it dry...");

		addItem(ModItems.GENERIC_MOB_GLAND, "Bile Gland", "A sac filled to the brim with bile.");
		addItem(ModItems.TOXIN_GLAND, "Toxin Gland", "An organ full of toxins, maybe you should drink it...");
		addItem(ModItems.VOLATILE_GLAND, "Volatile Gland", "A gland filled with an extremely reactive fluid.\nDoesn't seem like it's poisonous... Might be worth drinking.");

		addItem(ModItems.FLESH_BITS, "Flesh Bits", "A tiny bit of flesh... Used as a primary crafting ingredient.");
		addItem(ModItems.BONE_FRAGMENTS, "Bone Fragments", "A tiny fragment of bone. Provides rigidity and shape to flesh. Useful for assembling mechanical parts.");
		addItem(ModItems.TOUGH_FIBERS, "Tough Fibers", "Fibrous tissue that is very tough and rigid. Useful for crafting things that need more resilience.");
		addItem(ModItems.ELASTIC_FIBERS, "Elastic Fibers", "Fibrous tissue that is sturdy, while still being flexible and stretchy. It appears to be a good material to imitate muscles.");
		addItem(ModItems.MINERAL_FRAGMENT, "Mineral Fragment", "Component only obtained in Decomposer.\nUsed to toughen and harden fibers.");
		addItem(ModItems.GEM_FRAGMENTS, "Gem Fragments", "Bedazzle your fleshy creations!\nUsed to enhance hardened fibers to their maximum resilience, but they're very expensive.");
		addItem(ModItems.BIO_LUMENS, "Bioluminescent Goo", "A basic component only obtained via the Decomposer, usually alongside biotic matter.\n\nUsed as a cosmetic upgrade to make things glow.");
		addItem(ModItems.ORGANIC_MATTER, "Biotic Matter", "Moist organic material, extracted from plants. With its dark and fibrous texture, it almost seems like humus.");
		addItem(ModItems.EXOTIC_DUST, "Exotic Dust", "Exotic substance, perhaps the very essence of magic itself.\nAn uncommon byproduct obtained by decomposing magical things.");
		addItem(ModItems.STONE_POWDER, "Lithic Powder", "A very basic crafting component, usually obtained as a byproduct from decomposing things.");

		addItem(ModItems.NUTRIENTS, "Nutrients", "Very hard pellets rich in energy that almost look like vitamin pills.");
		addItem(ModItems.NUTRIENT_PASTE, "Nutrient Paste", "Nutrients combined with Biotic Matter to moisten the hard pellets, producing a paste.\nIt almost looks like yellowish cake, and is a convenient source of energy.");
		addItem(ModItems.NUTRIENT_BAR, "Nutrient Bar", "Nutrient Paste compressed into the shape of a bar. Looks edible, if a bit bland.");
		addItem(ModItems.BLOOMBERRY, "Bloomberry", "An exotic delicacy which has a bioluminescent sheen and grants random potion effects when eaten.");

		addItem(ModItems.REGENERATIVE_FLUID, "Regenerative Fluid", "A fluid with regenerative properties, used to concoct healing additives.");
		addItem(ModItems.WITHERING_OOZE, "Withering Ooze", "A corrosive extract. It likely has uses in bio-alchemy.");
		addItem(ModItems.HORMONE_SECRETION, "Hormone Secretion", "A fluid extract very rich in various hormones. A potent material for making drugs.");
		addItem(ModItems.TOXIN_EXTRACT, "Toxin Extract", "A fluid so toxic you probably shouldn't touch it. Good for making a plethora of dubious substances.");
		addItem(ModItems.VOLATILE_FLUID, "Volatile Fluid", "A very combustible extract, however it needs further processing to be truly dangerous.");
		addItem(ModItems.BILE, "Bile", "Organic base material which is often used in bio-alchemy.");

		addItem(ModItems.DESPOIL_SICKLE, "Flesh Plunderer", "A very brittle yet highly specialized tool for pillaging special loot from dying mobs.\n\nWhen held in either hand, the tool guarantees Despoil loot drops. When doing so, it receives extra durability damage.");
		addItem(ModItems.VIAL, "Organic Vial", "A small organic container with very high alchemical resistance, perfect for holding reactive substances.\nThe vial dissolves on use.");
		addItem(ModItems.LIVING_FLESH, "Living Flesh", "It's alive!\nUnfortunately, it looks too dumb to be the brain of a mob. You should turn it into a construct instead.");
		addItem(ModItems.PRIMORDIAL_CORE, "Primordial Core", "A ominous artifact made of flesh. Looking at it makes you feel uneasy...");
		addItem(ModItems.GUIDE_BOOK, "[WIP] Primordial Index", "[WIP] Ask questions?");
		addItem(ModItems.CREATOR_MIX, "Exotic Flesh Mix", "A meal made for the cradle... not for you.");
		addItem(ModItems.INJECTOR, "Bio-Injector", """
				A simple device which utilizes a razor sharp needle to quickly and forcefully inject Serums into Mobs and Players.
								
				Can be enchanted with:
				 - Piercing: Increases the chance to pierce through armor
				 - Anesthetic Touch: Nullifies the damage from the needle""");
		addItem(ModItems.FERTILIZER, "Bio-Alchemical Fertilizer", "Fertilizer that induces hyper-growth in plants, even for reeds, cactus, nether wart and chorus.");
		addItem(ModItems.GIFT_SAC, "Gift Sac", """
				Looks like some objects are wrapped in an organic layer of skin. Might be filled with items or toxin if your language is set to German.
								
				Right Click the Sac to retrieve the Items.""");
		addItem(ModItems.ACID_BUCKET, "Gastric Acid Bucket");

		addItem(ModItems.RAVENOUS_CLAWS, "Ravenous Claws", """
				Extremely hungry and vicious Claws forged by starving living flesh and grafting claws onto it.
								
				Repair the famished claws by feeding them with food via the player inventory, as you would fill a bundle.
								
				Killing Mobs with these claws grants blood charges, which allow you to use the Awakened mode.""");
		addItem(ModItems.CAUSTIC_GUNBLADE, "Caustic Gunblade", """
				A caustic gunblade that can alternate between melee and ranged mode and utilizes acidic ammo to damage its targets.
				The acidic ammunition regenerates by itself after 5s of no usage.""");
		addItem(ModItems.DEV_ARM_CANNON, "[Dev Tool] Arm Cannon", "Creative/Developer Tool for testing projectiles.");

		addItem(ModItems.ACOLYTE_ARMOR_HELMET, "Acolyte Helmet");
		addItem(ModItems.ACOLYTE_ARMOR_CHESTPLATE, "Acolyte Chestplate");
		addItem(ModItems.ACOLYTE_ARMOR_LEGGINGS, "Acolyte Leggings");
		addItem(ModItems.ACOLYTE_ARMOR_BOOTS, "Acolyte Boots");

		addItem(ModItems.THORN_SHIELD, "The Thorn", "Cute but prickly shield.");

		addItem(ModItems.ESSENCE_EXTRACTOR, "Essence Extractor", "Primed Suck, slurps essence from its victims.");
		addItem(ModItems.ESSENCE, "Essence", "Extracted life essence from a Mob. Contains sequences of genetic information.");
		add(ModItems.ESSENCE.get().getDescriptionId() + ".mob", "%1$s Essence");
		add(ModItems.ESSENCE.get().getDescriptionId() + ".unique_mob", "Unique %1$s Essence");

		addItem(ModItems.ORGANIC_COMPOUND, "Organic Compound", "Slimy substance made of bile infused with nutrients.");
		addItem(ModItems.UNSTABLE_COMPOUND, "Unstable Compound", "Very unstable and reactive substance. Seems like it will combust if it comes in contact with just about anything else.\nThe explosion is highly incendiary and has been known to liquefy magma blocks.");
		addItem(ModItems.EXOTIC_COMPOUND, "Exotic Compound", "Substance of a questionable nature, comprised of exotic material and other trace elements.");
		addItem(ModItems.GENETIC_COMPOUND, "Genetic Compound", "Cocktail of various hormones, nutrients and organic elements. It seems useful for producing potent stimulants.");
		addItem(ModItems.CORROSIVE_ADDITIVE, "Corrosive Additive", "A highly corrosive fluid that seems useful for alchemically burning away organic material, or weakening the bonds of complex substances.");
		addItem(ModItems.HEALING_ADDITIVE, "Healing Additive", "An highly concentrated substance that is used to imbue its properties to other compounds.");

		addSerumItem(ModItems.AGEING_SERUM, "Aging Serum", "Forces the maturation of young Mobs into Adults. Some rare Mobs may even turn into Elders.");
		addSerumItem(ModItems.REJUVENATION_SERUM, "Rejuvenation Serum", "Reverses the maturation of Mobs, in most cases turning them into children.");
		addSerumItem(ModItems.ENLARGEMENT_SERUM, "Enlargement Serum", "Induces growth in Slimes, Magma Cubes and Flesh Blobs.\n\n(If Pehkui is installed you can enlarge yourself and all Mobs)");
		addSerumItem(ModItems.SHRINKING_SERUM, "Shrinking Serum", "Shrinks Slimes, Magma Cubes and Flesh Blobs.\n\n(If Pehkui is installed you can shrink yourself and all Mobs)");

		addSerumItem(ModItems.CLEANSING_SERUM, "Cleansing Serum", "Burns away all foreign substances inside a creature.\nVery effective on sticky status effects that refuse to be healed with milk.");
		addSerumItem(ModItems.BREEDING_STIMULANT, "Breeding Stimulant", "Potent drug that that temporarily forces animals into a state of hyper-fertility, allowing them to reproduce repeatedly for a short time.\nUse with caution, as this stimulant may lead to undesirable mutations in the offspring.");
		addSerumItem(ModItems.ABSORPTION_BOOST, "Absorption Stimulant", "Grants stackable absorption health points to Mobs and Players.");
		addSerumItem(ModItems.INSOMNIA_CURE, "Insomnia Cure", "Resets the last slept time, no need to sleep for quite some time.\nCoffee who?");
		addSerumItem(ModItems.FRENZY_SERUM, "Frenzy Serum", "Potent stackable drug that greatly boost your attack damage and speed at the cost of withdrawal symptoms. Increases hostility and aggression of affected mobs. Rabbits seems to be affected in an irreversible manner.\nCommon side effects after stoppage of use include weakness, sluggish movement and nausea. Continue the use to alleviate the withdrawal or try your luck with sugary foods.");

		addBannerPatternItem(ModItems.MASCOT_BANNER_PATTERNS, "Banner Pattern", "Biomancy Mascot");

		addItem(ModItems.HUNGRY_FLESH_BLOB_SPAWN_EGG, "Hungry Flesh Blob Spawn Egg");
		addItem(ModItems.FLESH_BLOB_SPAWN_EGG, "Flesh Blob Spawn Egg");
		addItem(ModItems.LEGACY_FLESH_BLOB_SPAWN_EGG, "Legacy Flesh Blob Spawn Egg");
		addItem(ModItems.PRIMORDIAL_FLESH_BLOB_SPAWN_EGG, "Primordial Flesh Blob Spawn Egg");
		addItem(ModItems.PRIMORDIAL_HUNGRY_FLESH_BLOB_SPAWN_EGG, "Primordial Hungry Flesh Blob Spawn Egg");
		addItem(ModItems.FLESH_COW_SPAWN_EGG, "Flesh Cow Spawn Egg");
		addItem(ModItems.FLESH_SHEEP_SPAWN_EGG, "Flesh Sheep Spawn Egg");
		addItem(ModItems.FLESH_PIG_SPAWN_EGG, "Flesh Pig Spawn Egg");
		addItem(ModItems.FLESH_CHICKEN_SPAWN_EGG, "Flesh Chicken Spawn Egg");
		addItem(ModItems.CHROMA_SHEEP_SPAWN_EGG, "Chroma Sheep Spawn Egg");
		addItem(ModItems.THICK_FUR_SHEEP_SPAWN_EGG, "Thick Fur Sheep Spawn Egg");
	}

	private void addBlockTranslations() {
		addBlock(ModBlocks.PRIMORDIAL_CRADLE, "Primordial Cradle", "Offer adequate Tributes to the cradle and summon forth primordial messengers of exquisite flesh.");

		addBlock(ModBlocks.DECOMPOSER, "Decomposer", "A bio-machine that deconstructs items into their base components.\nThe Decomposer consumes nutrients to function.");
		addBlock(ModBlocks.DIGESTER, "Digester", "A machine born from flesh that converts food and plants into extremely bio-available nutrients.");
		addBlock(ModBlocks.BIO_FORGE, "Bio-Forge", "Crafting Station");
		addBlock(ModBlocks.BIO_LAB, "Bio-Lab", "Bio-alchemical Brewer");

		addBlock(ModBlocks.TONGUE, "Tongue", "Extracts up to 3 items of the same type every 24 ticks from containers it's attached to, and drops them on the ground.");
		addBlock(ModBlocks.MAW_HOPPER, "Maw Hopper", "A fleshy sister of the hopper. Transfers up to 16 items at a time.");

		addBlock(ModBlocks.STORAGE_SAC, "Storage Sac", "Cheap Shulker-like storage sac that also acts like a bundle.");
		addBlock(ModBlocks.CHRYSALIS, "Chrysalis", "Stores babies/children, small mobs and flesh blobs");

		addBlock(ModBlocks.FLESHKIN_CHEST, "Fleshkin Chest", """
				Crafted from living flesh, the organic composition of the chest grants it remarkable resilience, ensuring the safety of its precious contents even from explosions.
								
				Only its true master can unlock its contents without invoking the wrath of its razor-sharp fangs.""");
		//		addBlock(ModBlocks.FLESHKIN_DOOR, "[WIP] Fleshkin Door", EMPTY_STRING);
		//		addBlock(ModBlocks.FLESHKIN_TRAPDOOR, "[WIP] Fleshkin Trap Door", EMPTY_STRING);
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
		addBlock(ModBlocks.FIBROUS_FLESH, "Fibrous Flesh Block", "A unusual block flesh made from the innards of someone.");
		addBlock(ModBlocks.FIBROUS_FLESH_SLAB, "Fibrous Flesh Slab", "A unusual slab of flesh made from the innards of someone.");
		addBlock(ModBlocks.FIBROUS_FLESH_STAIRS, "Fibrous Flesh Stairs", "Stairs made of unusual flesh made from the innards of someone.");
		addBlock(ModBlocks.FIBROUS_FLESH_WALL, "Fibrous Flesh Wall", "A unusual wall of flesh made from the innards of someone.");

		addBlock(ModBlocks.FLESH_PILLAR, "Flesh Pillar", "A Pillar made of bones and flesh.");
		addBlock(ModBlocks.CHISELED_FLESH, "Chiseled Flesh Block", "A regal block of flesh... I'm most delighted");
		addBlock(ModBlocks.ORNATE_FLESH, "Ornamental Flesh Block", "A set of regal teeth and flesh.");
		addBlock(ModBlocks.ORNATE_FLESH_SLAB, "Ornamental Flesh Slab", "An incomplete set of regal teeth and flesh.");
		addBlock(ModBlocks.TUBULAR_FLESH_BLOCK, "Tubular Flesh Block", "Fake flesh pipes for everyone.");

		addBlock(ModBlocks.FLESH_FENCE, "Flesh Fence", "Fence made of bones and flesh...");
		addBlock(ModBlocks.FLESH_FENCE_GATE, "Flesh Fence Gate", "Fence gate made of bones and flesh...");
		addBlock(ModBlocks.FLESH_IRIS_DOOR, "Flesh Iris-Door", "Trapdoor-like iris door made of flesh...");
		addBlock(ModBlocks.FLESH_DOOR, "Flesh Door", "A sliding door made of flesh...");
		addBlock(ModBlocks.FULL_FLESH_DOOR, "Wide Flesh Door", "A wide sliding door made of flesh...");
		addBlock(ModBlocks.FLESH_SPIKE, "Flesh Spike", """
				A deadly trap fashioned from the amalgamation of reinforced bone and sinew. Approach with caution, for any contact will inflict considerable harm.
								
				Multiple spikes can be meticulously placed within a single location, intensifying their lethality.""");
		addBlock(ModBlocks.FLESH_LADDER, "Flesh Ladder", "Ladder mainly made of bones and a little bit of flesh...");
		addBlock(ModBlocks.YELLOW_BIO_LANTERN, "Yellow Bio-Lantern", "A bioluminescent light source that is energy-efficient and environmentally friendly.");
		addBlock(ModBlocks.BLUE_BIO_LANTERN, "Blue Bio-Lantern", "A bioluminescent light source. This one is blue!");
		addBlock(ModBlocks.PRIMORDIAL_BIO_LANTERN, "Bloom-Lantern", "A magenta light source made from a bioluminescent berry.");
		addBlock(ModBlocks.BLOOMLIGHT, "Bloomlight", "A malignant light source. This one is magenta as well!");
		addBlock(ModBlocks.TENDON_CHAIN, "Tendon Chain", "A chain made of tendons.");
		addBlock(ModBlocks.VIAL_HOLDER, "Vial Holder", "Display and organize your serums.");
		addBlock(ModBlocks.IMPERMEABLE_MEMBRANE, "Impermeable Membrane", "Gelatinous-like membrane reinforced with elastic fibers.");
		addBlock(ModBlocks.IMPERMEABLE_MEMBRANE_PANE, "Impermeable Membrane Pane", "Gelatinous-like membrane reinforced with elastic fibers.");
		addBlock(ModBlocks.BABY_PERMEABLE_MEMBRANE, "Baby-Permeable Membrane", "Gelatinous-like membrane reinforced with elastic fibers.\n\nBaby mobs can diffuse through the membrane.");
		addBlock(ModBlocks.BABY_PERMEABLE_MEMBRANE_PANE, "Baby-Permeable Membrane Pane", "Gelatinous-like membrane reinforced with elastic fibers.\n\nBaby mobs can diffuse through the membrane.");
		addBlock(ModBlocks.ADULT_PERMEABLE_MEMBRANE, "Adult-Permeable Membrane", "Gelatinous-like membrane reinforced with elastic fibers.\n\nAdult mobs can diffuse through the membrane.");
		addBlock(ModBlocks.ADULT_PERMEABLE_MEMBRANE_PANE, "Adult-Permeable Membrane Pane", "Gelatinous-like membrane reinforced with elastic fibers.\n\nAdult mobs can diffuse through the membrane.");
		addBlock(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE, "Primal Membrane", "Gelatinous-like membrane reinforced with elastic fibers.\n\nOnly mobs that are alive can diffuse through the membrane.");
		addBlock(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE_PANE, "Primal Membrane Pane", "Gelatinous-like membrane reinforced with elastic fibers.\n\nOnly mobs that are alive can diffuse through the membrane.");
		addBlock(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE, "Undead-Permeable Membrane", "Gelatinized phantom membrane reinforced with elastic fibers.\n\nOnly mobs that are undead can diffuse through the membrane.");
		addBlock(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE_PANE, "Undead-Permeable Membrane Pane", "Gelatinized phantom membrane reinforced with elastic fibers.\n\nOnly mobs that are undead can diffuse through the membrane.");
		addBlock(ModBlocks.BIOMETRIC_MEMBRANE, "Biometric Membrane", "Membrane which is configured via Mob Essence");
		add(ModBlocks.BIOMETRIC_MEMBRANE.get().getDescriptionId() + ".inverted", "Inverted Biometric Membrane");
		add(ModBlocks.BIOMETRIC_MEMBRANE.get().getDescriptionId() + ".inverted.unique", "Inverted Unique Biometric Membrane");
		add(ModBlocks.BIOMETRIC_MEMBRANE.get().getDescriptionId() + ".unique", "Unique Biometric Membrane");

		addBlock(ModBlocks.MODULAR_LARYNX, "Modular Larynx", """
				Similar to a Jukebox but made of an adaptive larynx. Capable of reproducing the sounds of a mob via the insertion of essence and redstone power.
						
				Right-Click with Mob Essence in hand to insert it.
				Or Shift Right-Click with empty hand to extract Mob Essence.
						
				You can also use automation like Hoppers to insert and extract the Mob Essence.
				""");
		add(TextComponentUtil.getItemTooltipKey(ModBlocks.MODULAR_LARYNX.get()) + ".1", "When Placed on Soul Sand or Soil:");
		add(TextComponentUtil.getItemTooltipKey(ModBlocks.MODULAR_LARYNX.get()) + ".2", "Use Death Sound");
		add(TextComponentUtil.getItemTooltipKey(ModBlocks.MODULAR_LARYNX.get()) + ".3", "When Placed on Magma:");
		add(TextComponentUtil.getItemTooltipKey(ModBlocks.MODULAR_LARYNX.get()) + ".4", "Use Hurt Sound");
		add(TextComponentUtil.getItemTooltipKey(ModBlocks.MODULAR_LARYNX.get()) + ".5", "When Placed on Other:");
		add(TextComponentUtil.getItemTooltipKey(ModBlocks.MODULAR_LARYNX.get()) + ".6", "Use Ambient Sound");

		//addBlock(ModBlocks.NEURAL_INTERCEPTOR, "Neural Interceptor", "A psychic node that prevents natural mob spawning in a 48 block radius.");

		addBlock(ModBlocks.PRIMAL_FLESH, "Primal Flesh Block", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.PRIMAL_FLESH_SLAB, "Primal Flesh Slab", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.PRIMAL_FLESH_STAIRS, "Primal Flesh Stairs", "Stairs made of primal flesh.\nFeels primitive and pure...");
		addBlock(ModBlocks.PRIMAL_FLESH_WALL, "Primal Flesh Wall", "Wall of primal flesh. Is this Terraria?\nYou better start running >:D");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH, "Smooth Primal Flesh Block", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB, "Smooth Primal Flesh Slab", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS, "Smooth Primal Flesh Stairs", "Stairs made of primal flesh.\nFeels primitive and pure...");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH_WALL, "Smooth Primal Flesh Wall", "Wall of primal flesh. Is this Terraria?\nYou better start running >:D");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH, "Porous Primal Flesh Block", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH_SLAB, "Porous Primal Flesh Slab", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH_STAIRS, "Porous Primal Flesh Stairs", "Stairs made of primal flesh.\nFeels primitive and pure...");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH_WALL, "Porous Primal Flesh Wall", "Wall of primal flesh. Is this Terraria?\nYou better start running >:D");
		addBlock(ModBlocks.MALIGNANT_FLESH, "Malignant Flesh Block", "Looks dangerous, you better not touch it!");
		addBlock(ModBlocks.MALIGNANT_FLESH_SLAB, "Malignant Flesh Slab", "Looks off-putting, probably best not to touch it.");
		addBlock(ModBlocks.MALIGNANT_FLESH_STAIRS, "Malignant Flesh Stairs", "Stairs made of malignant flesh.\nLooks diseased...");
		addBlock(ModBlocks.MALIGNANT_FLESH_WALL, "Malignant Flesh Wall", "Wall of malignant flesh.\nIt's coming for you! ;)");
		addBlock(ModBlocks.MALIGNANT_FLESH_VEINS, "Malignant Flesh Veins", "They look almost feral...\nyou better not touch them.");
		addBlock(ModBlocks.PRIMAL_BLOOM, "Primal Bloom", "An exotic flower of primordial beauty.\n\nIt will spread itself by launching it's ripe berry into the air.\nOn impact the berry explodes and spreads malignant veins as well.");

		addBlock(ModBlocks.PRIMAL_ORIFICE, "Primal Orifice", "A grotesque, flesh mass riddled with holes, oozing gastric juices. These orifices appear to grow inside flesh mound chambers and can be harvested using buckets or bottles, yielding a highly acidic fluid.");
		addBlock(ModBlocks.ACID_FLUID_BLOCK, "Gastric Acid");

		addBlock(ModBlocks.ACID_CAULDRON,"Gastric Acid Cauldron");
	}   

	private void addEntityTranslations() {
		addEntityType(ModEntityTypes.HUNGRY_FLESH_BLOB, "Hungry Flesh Blob");
		addEntityType(ModEntityTypes.FLESH_BLOB, "Flesh Blob");
		addEntityType(ModEntityTypes.LEGACY_FLESH_BLOB, "Legacy Flesh Blob");
		addEntityType(ModEntityTypes.PRIMORDIAL_FLESH_BLOB, "Primordial Flesh Blob");
		addEntityType(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB, "Primordial Hungry Flesh Blob");
		addEntityType(ModEntityTypes.FLESH_COW, "Flesh Cow");
		addEntityType(ModEntityTypes.FLESH_SHEEP, "Flesh Sheep");
		addEntityType(ModEntityTypes.FLESH_PIG, "Flesh Pig");
		addEntityType(ModEntityTypes.FLESH_CHICKEN, "Flesh Chicken");
		addEntityType(ModEntityTypes.CHROMA_SHEEP, "Chroma Sheep");
		addEntityType(ModEntityTypes.THICK_FUR_SHEEP, "Thick Fur Sheep");
	}

	private void addFluidTranslations() {
		addFluidType(ModFluids.ACID_TYPE, "Acid");
	}
}
