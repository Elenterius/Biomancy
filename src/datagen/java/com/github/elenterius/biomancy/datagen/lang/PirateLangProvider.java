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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PirateLangProvider extends AbstractLangProvider {

	public static final Marker LOG_MARKER = MarkerFactory.getMarker("PirateSpeakTranslationProvider");
	private static final String EMPTY_STRING = "";

	private List<Item> itemsToTranslate = List.of();
	private List<Block> blocksToTranslate = List.of();
	private List<Serum> serumsToTranslate = List.of();

	public PirateLangProvider(PackOutput packOutput) {
		super(packOutput, BiomancyMod.MOD_ID, "en_pt");
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
		addStatusEffectTranslations();
		addSerumTranslations();
		addDamageTranslations();

		addTooltip("empty", "Empty");
		addTooltip("contains", "Cargo: %1$s");
		addTooltip("nutrients_fuel", "Sustenance");
		addTooltip("primal_energy", "Ol' Magic");
		addTooltip("nutrients_consumes", "Consumin' %1$s u");
		addTooltip("consumption", "Consumin'");
		addTooltip("bile_fuel", "Vitriol");
		addTooltip("blood_charge", "Humor Capacity");
		addTooltip("contains_unique_dna", "Contains Unique Genetic Sequences");
		addTooltip("press_button_to", "Press %1$s ta' %2$s");

		addTooltip("owner", "Captain: %1$s");

		addTooltip("slots", "Holdin's");
		addTooltip("drops_from", "Severed from");
		addTooltip("and_more", "and even more booty...");

		addTooltip("action.show_info", "show knowledge");
		addTooltip("action.self_inject", "stab yerself");
		addTooltip("action.self_extract", "drain from yerself");
		addTooltip("action.open_inventory", "open its pack");
		addTooltip("action.activate", "set sail");
		addTooltip("action.deactivate", "abandon ship");
		addTooltip("action.reload", "rearm the cannons");
		addTooltip("action.cycle", "cycle");
		addTooltip("action.switch_mode", "change sails");
		addTooltip("action.enable_awakened_mode", "enter a Berserker's Rage");
		addTooltip("action.disable_awakened_mode", "exit Berserker's Rage");

		addTooltip("ability.bleed_proc", "Serrated Cutlass");
		addTooltip("ability.bleed_proc.desc", "When activated, makes yer' target Bleed (max 2)");
		addTooltip("ability.blood_explosion", "Bleein' Blast");
		addTooltip("ability.blood_explosion.desc", "When yer' target is Bleedin', they lose 10% of t'er vitality");
		addTooltip("ability.shredding_strike", "Shredding Slice");
		addTooltip("ability.shredding_strike.desc", "When hittin' 'em 'ard, shreds any Armor they might be wearin' (max 20)");
		addTooltip("ability.corrosive_proc", "Corrosive Blow");
		addTooltip("ability.corrosive_proc.desc", "When hittin' their weakness, covers 'em in grog");

		//		addTooltip("fire_rate", "Fire Rate");
		//		addTooltip("accuracy", "Accuracy");
		//		addTooltip("ammo", "Ammo");
		//		addTooltip("reload_time", "Reload Time");
		//		addTooltip("projectile_damage", "Projectile Damage");

		add(LivingToolState.getTooltipTranslationKey(), "The Tool is %1$s");
		add(LivingToolState.BROKEN.getTranslationKey(), "Broken");
		add(LivingToolState.DORMANT.getTranslationKey(), "Dormant");
		add(LivingToolState.AWAKENED.getTranslationKey(), "Awakened");

		addHudMessage("not_sleepy", "You don't feel like nappin'...");
		//		addHudMessage("set_behavior_command", "%1$s will now execute the %2$s command");
		//		addHudMessage("not_enough_ammo", "Not enough Ammo");
		addHudMessage("not_enough_nutrients", "Not enough Sustenance");
		addHudMessage("not_enough_blood_charge", "Not enough Humor Capacity");

		add(ClientTextUtil.getCtrlKey(), "ctrl");
		add(ClientTextUtil.getAltKey(), "alt");
		add(ClientTextUtil.getShiftKey(), "shift");
		add(ClientTextUtil.getRightMouseKey(), "right mouse");

		addBioForgeTab(ModBioForgeTabs.SEARCH, "The Captain's Loot");
		addBioForgeTab(ModBioForgeTabs.BUILDING_BLOCKS, "Construction Blocks");
		addBioForgeTab(ModBioForgeTabs.MACHINES, "Machinations");
		addBioForgeTab(ModBioForgeTabs.TOOLS, "Nifty Objects");
		addBioForgeTab(ModBioForgeTabs.COMPONENTS, "Workin' Bits");
		addBioForgeTab(ModBioForgeTabs.MISC, "Other Loot");

		add("jei.biomancy.recipe.bio_lab", "Witches' Apothecary Craftin'");
		add("jei.biomancy.recipe.decomposer", "Muncher Craftin'");
		add("jei.biomancy.recipe.digester", "Digester Craftin'");
		add("jei.biomancy.recipe.bio_forge", "Forge o' Flesh Craftin'");

		//		add("jer.biomancy.requiresDespoilOrBoneCleaver", "Requires Despoil Enchantment or Bone Cleaver (#forge:tools/knives)");
		//		add("jer.biomancy.requiresBoneCleaver", "Requires Bone Cleaver");

		addSoundTranslations();
		addBannerPatternTranslations();
	}

	private void addSoundTranslations() {
		addSound(ModSoundEvents.UI_BUTTON_CLICK, "Click Squidgy Button");
		addSound(ModSoundEvents.UI_MENU_OPEN, "Open Squidgy Menu");
		addSound(ModSoundEvents.UI_RADIAL_MENU_OPEN, "Open Round Menu");

		addSound(ModSoundEvents.INJECTOR_INJECT, "Injectin' Serum");
		addSound(ModSoundEvents.INJECTOR_FAIL, "Injection Failed");
		addSound(ModSoundEvents.MARROW_DRINK, "Slurping Marrow o' Bone");

		addSound(ModSoundEvents.FLESH_BLOCK_HIT, "Hit Squidgy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_PLACE, "Place Squidgy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_STEP, "Step on Squidgy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_BREAK, "Break Squidgy Block");
		addSound(ModSoundEvents.FLESH_BLOCK_FALL, "Fall on Squidgy Block");

		addSound(ModSoundEvents.BONY_FLESH_BLOCK_HIT, "Hit Squidgy Block o' Bone");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_PLACE, "Place Squidgy Block o' Bone");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_STEP, "Step on Squidgy Block o' Bone");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_BREAK, "Break Squidgy Block o' Bone");
		addSound(ModSoundEvents.BONY_FLESH_BLOCK_FALL, "Fall on Squidgy Block o' Bone");

		addSound(ModSoundEvents.FLESH_DOOR_OPEN, "Squidgy Door Openin'");
		addSound(ModSoundEvents.FLESH_DOOR_CLOSE, "Squidgy Door Closin'");

		addSound(ModSoundEvents.CLAWS_ATTACK_STRONG, "'ard Multi-Cutlass Attack");
		addSound(ModSoundEvents.CLAWS_ATTACK_BLEED_PROC, "Serated Cutlass trigger'in");
		addSound(ModSoundEvents.FLESHKIN_NO, "Fleshsiblin' sayin' Nay");
		addSound(ModSoundEvents.FLESHKIN_BREAK, "Fleshsiblin' Break");
		addSound(ModSoundEvents.FLESHKIN_BECOME_DORMANT, "Fleshsiblin' starts nappin'");
		addSound(ModSoundEvents.FLESHKIN_BECOME_AWAKENED, "Fleshsiblin' awakens");

		addSound(ModSoundEvents.FLESHKIN_CHEST_OPEN, "Open Squidgy Chest");
		addSound(ModSoundEvents.FLESHKIN_CHEST_CLOSE, "Close Squidgy Chest");
		addSound(ModSoundEvents.FLESHKIN_CHEST_BITE_ATTACK, "Squidgy Chest Bites Somethin'");

		addSound(ModSoundEvents.CRADLE_SPAWN_MOB, "Summonin' Cauldron Spawns a Mob");
		addSound(ModSoundEvents.CRADLE_BECAME_FULL, "Summonin' Cauldron is full");
		addSound(ModSoundEvents.CRADLE_EAT, "Summonin' Cauldron Munchin'");
		addSound(ModSoundEvents.CRADLE_NO, "Summonin' Cauldron sayin' Nay");
		addSound(ModSoundEvents.CRADLE_CRAFTING_RANDOM, "Summonin' Cauldron is summonin'");
		addSound(ModSoundEvents.CRADLE_SPIKE_ATTACK, "Summonin' Cauldron attacks");

		addSound(ModSoundEvents.UI_STORAGE_SAC_OPEN, "Open Menu of Lootin' Sac");
		addSound(ModSoundEvents.UI_BIO_FORGE_OPEN, "Open Menu of Forge o' Flesh");
		addSound(ModSoundEvents.UI_BIO_FORGE_SELECT_RECIPE, "Choose yer Recipe in Forge o' Flesh");
		addSound(ModSoundEvents.UI_BIO_FORGE_TAKE_RESULT, "Craft somethin' in Forge o' Flesh");

		addSound(ModSoundEvents.DECOMPOSER_CRAFTING, "Muncher be craftin'");
		addSound(ModSoundEvents.UI_DECOMPOSER_OPEN, "Open Menu of Muncher");
		addSound(ModSoundEvents.DECOMPOSER_EAT, "Muncher be munchin'");
		addSound(ModSoundEvents.DECOMPOSER_CRAFTING_RANDOM, "Muncher be burpin'");
		addSound(ModSoundEvents.DECOMPOSER_CRAFTING_COMPLETED, "Muncher be finished craftin'");

		addSound(ModSoundEvents.BIO_LAB_CRAFTING, "Witches' Apothecary be craftin'");
		addSound(ModSoundEvents.UI_BIO_LAB_OPEN, "Open Menu of Witches' Apothecary");
		addSound(ModSoundEvents.BIO_LAB_CRAFTING_RANDOM, "Witches' Apothecary be slurpin'");
		addSound(ModSoundEvents.BIO_LAB_CRAFTING_COMPLETED, "Witches Apothecary be finished craftin'");

		addSound(ModSoundEvents.DIGESTER_CRAFTING, "Stomach Rumbler be craftin'");
		addSound(ModSoundEvents.UI_DIGESTER_OPEN, "Open Menu of Stomach Rumbler");
		addSound(ModSoundEvents.DIGESTER_CRAFTING_RANDOM, "Stomach Rumbler be burpin'");
		addSound(ModSoundEvents.DIGESTER_CRAFTING_COMPLETED, "Stomach Rumbler be finished craftin'");

		addSound(ModSoundEvents.FLESH_BLOB_JUMP, "Cube o' Flesh Jump");
		addSound(ModSoundEvents.FLESH_BLOB_HURT, "Cube o' Flesh Hurt");
		addSound(ModSoundEvents.FLESH_BLOB_DEATH, "Cube o' Flesh Death");
		addSound(ModSoundEvents.FLESH_BLOB_AMBIENT, "Cube o' Flesh Ambient");
		addSound(ModSoundEvents.FLESH_BLOB_GROWL, "Cube o' Flesh Growls");
		addSound(ModSoundEvents.FLESH_BLOB_MEW_PURR, "Cube o' Flesh Purrs");
	}

	private void addDamageTranslations() {
		addDeathMessage(ModDamageTypes.CHEST_BITE, "%1$s tried openin' a coffer, but got cursed instead");
		addDeathMessage(ModDamageTypes.PRIMORDIAL_SPIKES, "%1$s was impaled by ol'-magic spikes");

		addDeathMessage(ModDamageTypes.FALL_ON_SPIKE,
				"%1$s fell on a scarrin' spike",
				"%1$s was thrown into a spike pit by %2$s",
				"%1$s was skewered by a sharp spike by %2$s using %3$s"
		);
		addDeathMessage(ModDamageTypes.IMPALED_BY_SPIKE,
				"%1$s was impaled by a scarrin' spike",
				"%1$s was impaled onto a scarrin' spike by %2$s",
				"%1$s was impaled onto a scarrin' spike by %2$s using %3$s"
		);

		addDeathMessage(ModDamageTypes.CORROSIVE_ACID,
				"%1$s succumbed to lots o' acid burnin'",
				"%1$s was doused with corrosive acid by %2$s",
				"%1$s was showered in corrosive acid by %2$s using %3$s"
		);
		addDeathMessage(ModDamageTypes.BLEED,
				"%1$s fell to hefty humor imbalance",
				"%1$s was robbed of humor by %2$s",
				"%1$s was blood let by %2$s using %3$s"
		);

		addDeathMessage(ModDamageTypes.TOOTH_PROJECTILE, "[WIP]",
				"[WIP] %1$s was forcefully implanted with teeth by %2$s",
				"[WIP] %1$s received a lethal dental implant by %2$s using %3$s");
	}

	private void addSerumTranslations() {
		//serums that are not tied to a specific item
		add(Serum.EMPTY, "ERROR: INVALID SERUM");
	}

	private void addStatusEffectTranslations() {
		addEffect(ModMobEffects.CORROSIVE, "Acid Burnin'");
		addEffect(ModMobEffects.ARMOR_SHRED, "Armor Shreddin'");
		addEffect(ModMobEffects.LIBIDO, "A Night Out");
		addEffect(ModMobEffects.BLEED, "Bleedin'");
		addEffect(ModMobEffects.ESSENCE_ANEMIA, "[PH] Lackin' Essence");
		addEffect(ModMobEffects.DROWSY, "Davy Jones' Call");
		addEffect(ModMobEffects.DESPOIL, "Keepin' Fresh");
	}

	private void addEnchantmentTranslations() {
		addEnchantment(ModEnchantments.DESPOIL, "Keepin' Fresh", "When killing somethin' with a weapon that has this magic, it will drop more special loot.");
		addEnchantment(ModEnchantments.ANESTHETIC, "Numbin' Hands", "Keeps yer target from noticin' yer Injector stickin' into 'em, and makes sure you don't hurt 'em when ya do it.");
	}

	private void addBannerPatternTranslations() {
		addBannerPattern(ModBannerPatterns.MASCOT_BASE, "Mascot Base");
		addBannerPattern(ModBannerPatterns.MASCOT_OUTLINE, "Mascot Outline");
		addBannerPattern(ModBannerPatterns.MASCOT_ACCENT, "Mascot Accent");
	}

	private void addItemTranslations() {
		addItem(ModItems.MOB_SINEW, "Stretchin' Flesh", "Fleshy bits made of Elastic Fibers.");
		addItem(ModItems.MOB_FANG, "Sharp Tooth", "Tooth made o' tissue with lots o' earthy bits.");
		addItem(ModItems.MOB_CLAW, "Sharp Nail", "Nail made o' tough fibers with lot o' earthy bits.");
		addItem(ModItems.MOB_MARROW, "Marrow o' Bone", "Marrow 'arvested from ta' bones of yer victims. Made with lots o' humors and earthy bits.");
		addItem(ModItems.WITHERED_MOB_MARROW, "Marrow o' Withered Bone", "Withered Marrow, some dark seawater be oozin' out o' it.\nIt be lookin' tasty, maybe ya' should suck it dry...");

		addItem(ModItems.GENERIC_MOB_GLAND, "Vitriol Sac", "A sac filled ta' the brim with vitriol.");
		addItem(ModItems.TOXIN_GLAND, "Poison Sac", "A sac full of poison, maybe you should make ta' captain drink it...");
		addItem(ModItems.VOLATILE_GLAND, "Cannon Sac", "A sac filled with seawater mixed with cannon-powder.\nIt don't look like it be deadly... May be worth drinkin' if ye run out of rations.");

		addItem(ModItems.FLESH_BITS, "Squidgy Bits", "A wee scrap o' flesh... Used mostly fer craftin'.");
		addItem(ModItems.BONE_FRAGMENTS, "Fragments o' Bone", "A few wee fragments o' bone. It be gvin' rigidity and shape ta' flesh. Good fer assemblin' machine parts.");
		addItem(ModItems.TOUGH_FIBERS, "Tough Fibers", "Natural rope that be very tough and rigid. Useful fer craftin' things that be needin' strength.");
		addItem(ModItems.ELASTIC_FIBERS, "Stretchy Fibers", "Natural rope that be sturdy, an' still twisty and stretchy. It be a good material ta' fake muscle.");
		addItem(ModItems.MINERAL_FRAGMENT, "Earthy Bits", "Craftin' bits only obtained in Muncher.\nUsed ta' toughen and harden natural ropes.");
		addItem(ModItems.GEM_FRAGMENTS, "Loot Bits", "Now yer bedazzlin' yer squidgy monstrosities!\nUsed ta' enhance hardened natural ropes all the way, but it be very pricey.");
		addItem(ModItems.BIO_LUMENS, "Glowy Goop", "A basic craftin' bit only found from ta' Muncher, usually with last night's leftovers.\n\nUsed ta upgrade things so they be glowin'.");
		addItem(ModItems.ORGANIC_MATTER, "Last Night's Leftovers", "Moist once-livin craftin' bits, made out'ta plants. It be dark and fibrous, lookin' much like what be left in the latrine.");
		addItem(ModItems.EXOTIC_DUST, "Magic Powder", "Ta' very essence of magic itself.\nA rarity, obtained by munchin' magical odds 'n ends.");
		addItem(ModItems.STONE_POWDER, "Earthy Powder", "A very basic craftin' component, looted as a byproduct from munchin' odds 'n ends.");

		addItem(ModItems.NUTRIENTS, "Sustenance", "Packed wit' energy, it be almost lookin' like green musket shot.");
		addItem(ModItems.NUTRIENT_PASTE, "Sustenance Slop", "Sustenance mixed wit' Last Night's Leftovers to moisten ta' 'ard pellets, makin' a paste.\nIt be lookin' like jaundiced cake, and it be an easy source o' grub.");
		addItem(ModItems.NUTRIENT_BAR, "Sustenance Bar", "Sustenance Slop pressed inta the shape of an ingot. It's bettr'n hard-tack!");
		addItem(ModItems.BLOOMBERRY, "Bloomin' berry", "A tasy treat which be glowin' on it's own. It be grantin' random boons when eaten.");

		addItem(ModItems.REGENERATIVE_FLUID, "Healin' Seawater", "Seawater infused with healin' power, it be used by witches to concoct remedies.");
		addItem(ModItems.WITHERING_OOZE, "Witherin' Ooze", "A corrosive ooze. It be used by witches fer their evil brewin'.");
		addItem(ModItems.HORMONE_SECRETION, "Humor Vial", "A vial with ta' perfect balance o' ta' four humors. It be good fer' makin'... substances, if ye know what I mean.");
		addItem(ModItems.TOXIN_EXTRACT, "Tainted Seawater", "A fluid so deadly ya really shouldn't touch it. Used by witches to make ta' real nasty stuff.");
		addItem(ModItems.VOLATILE_FLUID, "Cannon Fluid", "Seawater that gets a little too excited. It needs further processin' to be truly deadly.");
		addItem(ModItems.BILE, "Vitriol", "Base material which is often used by witches ta' make their brews.");

		addItem(ModItems.DESPOIL_SICKLE, "Cutlass o' Flesh", "A fragile weapon forged fer the sole purpose o' pillaging special loot from yer enemies.\n\nWhen in either hook, the tool guarantees Fresh loot drops. When it triggers, it be damaged a little extra.");
		addItem(ModItems.VIAL, "Organic Vial", "A small container, it be very resistant to the witches' brews and be perfect for holding 'em.\nThe vial melts after it be used.");
		addItem(ModItems.LIVING_FLESH, "Livin' Flesh", "It be alive!\nExcept, it be lookin' a little too dull ta' be the brain of somethin'. Ya could turn it inta a construct instead.");
		addItem(ModItems.PRIMORDIAL_CORE, "Ol'-Magic Core", "An ominous bit o' plunder made o' flesh. Lookin' at it makes you feel like ya' lost yer sea-legs...");
		//		addItem(ModItems.PRIMORDIAL_LIVING_OCULUS, "Primordial Oculus", "A ominous eye is gazing at you...");
		addItem(ModItems.GUIDE_BOOK, "[WIP] Primordial Index", "[WIP] Ask questions?");
		addItem(ModItems.CREATOR_MIX, "Magic Flesh Mix", "Some grub made for ta' cauldron... not fer you.");
		addItem(ModItems.INJECTOR, "Injector o' Witches", """
				A simple tool that be usin' a sharp needle ta' quickly and forcefully inject Serums inta Mobs an' Players.
								
				Can be infused with:
				 - Sailin' through: Increasin' ta' chance to get through yer target's armor
				 - Numbin' Hands: Preventin' ya from hurtin' the thing ya be injectin'""");
		addItem(ModItems.FERTILIZER, "Witches' Fertilizer", "Fertilizer that causes yer plants ta' grow quickly, even for Sugar reeds, cactus, verruca o' nether and tall one's flower.");
		addItem(ModItems.GIFT_SAC, "Sac o' Giftin'", """
				It be lookin' like some loot be wrapped in a layer o' skin. Might be filled with loot if ye be German.
								
				Right Click ta' Sac to retrieve yer loot.""");
		addItem(ModItems.ACID_BUCKET, "Bucket o' Acid");

		addItem(ModItems.RAVENOUS_CLAWS, "Multi-Cutlass o' Hunger", """
				Extremely hungry an' vicious Claws forged by starvin' yer livin' flesh and slappin' claws onto it.
								
				Repair yer famished claws by feedin' them wit' grub in yer inventory, as ya' would fill a Pouch.
								
				Killin' Mobs with these claws be grantin' ya' humors, which allow ya' to use ta' Berserker's Rage.""");
		addItem(ModItems.DEV_ARM_CANNON, "[Dev Tool] Arm Cannon", "Creative/Developer Tool for testing projectiles.");

		addItem(ModItems.ESSENCE_EXTRACTOR, "Essence Extractor", "Primed Suck, slurps essence from its victims.");
		addItem(ModItems.ESSENCE, "Essence", EMPTY_STRING);

		addItem(ModItems.ORGANIC_COMPOUND, "Life Compound", "Slimy liquid made o' bile infused with sustenance.");
		addItem(ModItems.UNSTABLE_COMPOUND, "Cannoneer's Compound", "Mighty unstable substance. Seems it'd explode if it be touchin' just about anythin' else.");
		addItem(ModItems.EXOTIC_COMPOUND, "Wicthes' Compound", "It be o' questionable nature, made o' magic essence and other bits n' bobs.");
		addItem(ModItems.GENETIC_COMPOUND, "Hereditary Compound", "Mixer o' various humors, nutrients and other livin' bits. It be used fer producin' potent drugs.");
		addItem(ModItems.CORROSIVE_ADDITIVE, "Burnin' Additive", "A fluid that be quickly corrodin' even ta' thickest o' cannon barrels. It be useful for burnin' away livin' material, or weakenin' the hulls of yer rivals' ships.");
		addItem(ModItems.HEALING_ADDITIVE, "Healin' Additive", "A mightily concentrated brew that be used to imbue its healin' powers ta other brews.");

		addSerumItem(ModItems.AGEING_SERUM, "Agin' Serum", "Forces the agin' of young Mobs inta Adults. Some rare Mobs may even turn inta Elders.");
		addSerumItem(ModItems.REJUVENATION_SERUM, "Rejuvenatin' Serum", "Reverses the agin' of Mobs, most times it be turning 'em inta kiddos.");
		addSerumItem(ModItems.ENLARGEMENT_SERUM, "Embiggenin' Serum", "It be makin' Cubes o' Slime, Cubes o' Magma and Cubes o' Flesh grow.\n\n(If Pehkui be installed, ya can enlarge yerself and any Mob ya like)");
		addSerumItem(ModItems.SHRINKING_SERUM, "Shrinkin' Serum", "It be makin' Cubes o' Slime, Cubes o' Magma and Cubes o' Flesh shrink.\n\n(If Pehkui be installed ya can shrink yerself and any Mob ya like)");

		addSerumItem(ModItems.CLEANSING_SERUM, "Cleansin' Serum", "Burns away any unusual concoctions inside a creature.\nIt be very effective on hangovers n' other effects that be refusin' to be healed with cow juice.");
		addSerumItem(ModItems.BREEDING_STIMULANT, "Breedin' Stimulant", "It be makin' Animals hyper-fertile, so they can repeatedly breed for a short time.");
		addSerumItem(ModItems.ABSORPTION_BOOST, "Absorbin' Stimulant", "It be grantin' stackable absorption health boons to Mobs and Players.");
		addSerumItem(ModItems.INSOMNIA_CURE, "Sleepin' Sickness Cure", "It be makin' yer head believe you slept, so ya can stay in ta' crow's nest all night.\nWho be needin' coffee?");

		addBannerPatternItem(ModItems.MASCOT_BANNER_PATTERNS, "Banner Pattern", "Biomancy's Parrot");

		addItem(ModItems.HUNGRY_FLESH_BLOB_SPAWN_EGG, "Hungry Cube o' Flesh Cackle Fruit");
		addItem(ModItems.FLESH_BLOB_SPAWN_EGG, "Cube o' Flesh Cackle Fruit");
		addItem(ModItems.LEGACY_FLESH_BLOB_SPAWN_EGG, "Legacy Cube o' Flesh Cackle Fruit");
		addItem(ModItems.PRIMORDIAL_FLESH_BLOB_SPAWN_EGG, "Ol' Magic Cube o' Flesh Cackle Fruit");
		addItem(ModItems.PRIMORDIAL_HUNGRY_FLESH_BLOB_SPAWN_EGG, "Ol' Magic Hungry Cube o' Flesh Cackle Fruit");
	}

	private void addBlockTranslations() {
		addBlock(ModBlocks.PRIMORDIAL_CRADLE, "Summonin' Cauldron", "If ye offer adequate loot ta' the cauldron, it will summon forth messengers o' beautiful flesh.");

		addBlock(ModBlocks.DECOMPOSER, "Muncher", "A livin' contraption that munches yer loot into its base bits.\nTha' Muncher consumes sustenance to work.");
		addBlock(ModBlocks.DIGESTER, "Stomach Rumbler", "A contraption born from flesh. It be convertin' grub 'n plants into sustenance.");
		addBlock(ModBlocks.BIO_FORGE, "Forge o' Flesh", "Craftin' Station");
		addBlock(ModBlocks.BIO_LAB, "Witches' Apothecary", "A Witch's Best Friend");

		//		addBlock(ModBlocks.VOICE_BOX, "[PH] Modular Larynx", EMPTY_STRING);
		addBlock(ModBlocks.TONGUE, "Licker", "Takes up to 3 bits o' loot o' the same type ev'ry 24 ticks from containers it be attached to, and drops 'em on ta' deck.");
		addBlock(ModBlocks.MAW_HOPPER, "Nimble Suckin' Device", "A squidgy sister of ta' Suckin' Device. It be shippin' up ta' 16 bits o' loot at once.");

		addBlock(ModBlocks.STORAGE_SAC, "Loot Sac", "Cheap Shulker-like lootcontainer, that also be actin' like a bundle.");

		addBlock(ModBlocks.FLESHKIN_CHEST, "Fleshsiblin' Coffer", """
				Crafted from livin' flesh, the livin' makeup of the coffer makes it mighty resistant to even cannons, ensurin' the safety of your most precious loot.
								
				Only its captain may unlock its contents without invoking the wrath of its razor-sharp fangs.""");
		//		addBlock(ModBlocks.FLESHKIN_DOOR, "[WIP] Fleshkin Door", EMPTY_STRING);
		//		addBlock(ModBlocks.FLESHKIN_TRAPDOOR, "[WIP] Fleshkin Trap Door", EMPTY_STRING);
		addBlock(ModBlocks.FLESHKIN_PRESSURE_PLATE, "Fleshsiblin' Pressure Sensor", """
				Fleshsiblin' pancake. It be lookin' tasty...
				It be havin' two behaviors, either it only be activatin' fer its captain, or it be only workin' for ta' crew an' landlubbers.
										
				Sneak click to change how it be behavin'.""");

		addBlock(ModBlocks.FLESH, "Squidgy Block", "A generic block of flesh... Don't bother me with this!");
		addBlock(ModBlocks.FLESH_SLAB, "Squidgy Slab", "A generic slab of flesh... Don't bother me with this!");
		addBlock(ModBlocks.FLESH_STAIRS, "Squidgy Stairs", "Stairs made of generic flesh... Don't bother me with this!");
		addBlock(ModBlocks.FLESH_WALL, "Squdgy Wall", "A generic wall of flesh.");
		addBlock(ModBlocks.PACKED_FLESH, "Packed Block o' Flesh", "Tenacious Block of flesh. Is it tough enough?");
		addBlock(ModBlocks.PACKED_FLESH_SLAB, "Packed Slab o' Flesh", "Tenacious Slab of flesh. Is it tough enough?");
		addBlock(ModBlocks.PACKED_FLESH_STAIRS, "Packed Stairs o' Flesh", "Stairs made of tenacious flesh. Is it tough enough?");
		addBlock(ModBlocks.PACKED_FLESH_WALL, "Packed Wall o' Flesh", "Tenacious wall of flesh.");
		addBlock(ModBlocks.FIBROUS_FLESH, "Fibrous Block o' Flesh", "A unusual block flesh made from the innards of someone.");
		addBlock(ModBlocks.FIBROUS_FLESH_SLAB, "Fibrous Slab", "A unusual slab of flesh made from the innards of someone.");
		addBlock(ModBlocks.FIBROUS_FLESH_STAIRS, "Fibrous Stairs", "Stairs made of unusual flesh made from the innards of someone.");
		addBlock(ModBlocks.FIBROUS_FLESH_WALL, "Fibrous Wall", "A unusual wall of flesh made from the innards of someone.");

		addBlock(ModBlocks.FLESH_PILLAR, "Squidgy Pillar", "A Pillar made of bones and flesh.");
		addBlock(ModBlocks.CHISELED_FLESH, "Carv'd Block o' Flesh", "A regal block of flesh... I'm most delighted");
		addBlock(ModBlocks.ORNATE_FLESH, "Beau'iful Block o' Flesh", "A set of regal teeth and flesh.");
		addBlock(ModBlocks.ORNATE_FLESH_SLAB, "Beau'iful Slab o' Flesh", "A set of regal teeth and flesh.");
		addBlock(ModBlocks.TUBULAR_FLESH_BLOCK, "Tubular Block o' Flesh", "Fake flesh pipes for everyone.");

		addBlock(ModBlocks.FLESH_FENCE, "Squidgy Fence", "Fence made of bones and flesh...");
		addBlock(ModBlocks.FLESH_FENCE_GATE, "Squidgy Fence Gate", "Fence gate made of bones and flesh...");
		addBlock(ModBlocks.FLESH_IRIS_DOOR, "Iris-Door o' Flesh", "Trapdoor-like iris door made of flesh...");
		addBlock(ModBlocks.FLESH_DOOR, "Sqidgy Door", "A sliding door made of flesh...");
		addBlock(ModBlocks.FULL_FLESH_DOOR, "Wide Door o' Flesh", "A wide sliding door made of flesh...");
		addBlock(ModBlocks.FLESH_SPIKE, "Spike o' Flesh", """
				A deadly trap fashioned from combinin' reinforced bone and sinew. Be usin' caution when approachin', for any contact will harm ya' mightily.
								
				Many spikes can be placed within a single location, makin' 'em even more lethal.""");
		addBlock(ModBlocks.FLESH_LADDER, "Ladder o' Flesh", "Ladder mainly made of bones and a little bit of flesh...");
		addBlock(ModBlocks.YELLOW_BIO_LANTERN, "Jaundiced Lantern", "A source o' glowin' that be both energy-efficient an' environmentally friendly.");
		addBlock(ModBlocks.BLUE_BIO_LANTERN, "Nautical Lantern", "A source o' glowin'. This one be remindin' me o' ta' sea!");
		addBlock(ModBlocks.PRIMORDIAL_BIO_LANTERN, "Bloomin' Lantern", "A violet source o' light made from a glowin' berry.");
		addBlock(ModBlocks.BLOOMLIGHT, "Bloomlight", "A source o' light that's come down with scurvy. This one be violet too!");
		addBlock(ModBlocks.TENDON_CHAIN, "Chain o' Tendons", "A chain made o' tendons.");
		addBlock(ModBlocks.VIAL_HOLDER, "Vial Rack", "Display an' organize yer serums.");
		addBlock(ModBlocks.IMPERMEABLE_MEMBRANE, "Solid Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.");
		addBlock(ModBlocks.IMPERMEABLE_MEMBRANE_PANE, "Skinny Solid Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.");
		addBlock(ModBlocks.BABY_PERMEABLE_MEMBRANE, "Kiddo-Passable Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly kiddos can pass through ta' membrane.");
		addBlock(ModBlocks.BABY_PERMEABLE_MEMBRANE_PANE, "Skinny Kiddo-Passable Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly kiddos can pass through ta' membrane.");
		addBlock(ModBlocks.ADULT_PERMEABLE_MEMBRANE, "Adult-Passable Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly adults can pass through ta' membrane.");
		addBlock(ModBlocks.ADULT_PERMEABLE_MEMBRANE_PANE, "Skinny Adult-Passable Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly adults can pass through ta' membrane.");
		addBlock(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE, "Ol'-Magic Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly livin' mobs can pass through ta' membrane.");
		addBlock(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE_PANE, "Thin Ol'-Magic Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly livin' mobs can pass through ta' membrane.");
		addBlock(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE, "Diseased Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly once-dead now-livin' mobs can pass through ta' membrane.");
		addBlock(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE_PANE, "Skinny & Diseased Membrane", "Membrane that be remindin' me of jellied eels. It be reinforced with stretchy fibers.\n\nOnly once-dead now-livin' mobs can pass through ta' membrane.");
		//addBlock(ModBlocks.NEURAL_INTERCEPTOR, "Neural Interceptor", "A psychic node that prevents natural mob spawning in a 48 block radius.");

		addBlock(ModBlocks.PRIMAL_FLESH, "Ol'-Magic Block o' Flesh", "Ancient and pure, ye better not touch this with yer dirty hook.");
		addBlock(ModBlocks.PRIMAL_FLESH_SLAB, "Ol'-Magic Slab o' Flesh", "Ancient and pure, ye better not touch this with yer dirty mitts.");
		addBlock(ModBlocks.PRIMAL_FLESH_STAIRS, "Ol'-Magic Stairs o' Flesh", "Stairs made o' primal flesh.\nFeels ancient and pure...");
		addBlock(ModBlocks.PRIMAL_FLESH_WALL, "Ol'-Magic Wall o' Flesh", "Wall o' primal flesh. Do this be Terraria?\nYe better start running >:D");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH, "Smooth Ol'-Magic Block o' Flesh", "Ancient and pure, ye better not touch this with yer dirty hook.");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB, "Smooth Ol'-Magic Slab o' Flesh", "Ancient and pure, ye better not touch this with yer dirty mitts.");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS, "Smooth Ol'-Magic Stairs o' Flesh", "Stairs made o' primal flesh.\nFeels ancient and pure...");
		addBlock(ModBlocks.SMOOTH_PRIMAL_FLESH_WALL, "Smooth Ol'-Magic Wall o' Flesh", "Wall o' primal flesh. Do this be Terraria?\nYe better start running >:D");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH, "Porous Ol'-Magic Block o' Flesh", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH_SLAB, "Porous Ol'-Magic Slab o' Flesh", "Primitive and pure, you better not touch this with your dirty mitts.");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH_STAIRS, "Porous Ol'-Magic Stairs o' Flesh", "Stairs made of primal flesh.\nFeels primitive and pure...");
		addBlock(ModBlocks.POROUS_PRIMAL_FLESH_WALL, "Porous Ol'-Magic Wall o' Flesh", "Wall of primal flesh. Is this Terraria?\nYou better start running >:D");

		addBlock(ModBlocks.MALIGNANT_FLESH, "Scurvy-ridden Block o' Flesh", "It be lookin' dangerous, ye better not touch it!");
		addBlock(ModBlocks.MALIGNANT_FLESH_SLAB, "Scurvy-ridden Slab o' Flesh", "It be lookin' off-puttin', probably best not to be touchin' it.");
		addBlock(ModBlocks.MALIGNANT_FLESH_STAIRS, "Scurvy-ridden Stairs o' Flesh", "Stairs made o' scurvy-ridden flesh.");
		addBlock(ModBlocks.MALIGNANT_FLESH_WALL, "Scurvy-ridden Wall o' Flesh", "Wall o' scurvy-ridden flesh.\nIt's coming for you! ;)");
		addBlock(ModBlocks.MALIGNANT_FLESH_VEINS, "Scurvy-ridden Veins o' Flesh", "They be lookin' almost feral...\nye better not touch them.");
		addBlock(ModBlocks.PRIMAL_BLOOM, "Primal Bloom", "A beautiful flower o' ancient beauty.\n\nIt be spreadin' itself by launchin' its ripe berry into ta' air.\nOn impact, ta berry explodes like a powder-keg, and spreads scurvy-ridden veins.");
		addBlock(ModBlocks.PRIMAL_ORIFICE, "Oozin' Flesh", "An ancient hunk o' flesh that be full of holes. It be leakin' somethin' acidic.");

		addBlock(ModBlocks.ACID_FLUID_BLOCK, "Acid");
		addBlock(ModBlocks.ACID_CAULDRON, "Pot o' Acid");
	}

	private void addEntityTranslations() {
		addEntityType(ModEntityTypes.HUNGRY_FLESH_BLOB, "Hungry Cube o' Flesh");
		addEntityType(ModEntityTypes.FLESH_BLOB, "Cube o' Flesh");
		addEntityType(ModEntityTypes.LEGACY_FLESH_BLOB, "Legacy Cube o' Flesh");
		addEntityType(ModEntityTypes.PRIMORDIAL_FLESH_BLOB, "Ol' Magic Cube o' Flesh");
		addEntityType(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB, "Ol' Magic Hungry Cube o' Flesh");
	}

	private void addFluidTranslations() {
		addFluidType(ModFluids.ACID_TYPE, "Acid");
	}
}
