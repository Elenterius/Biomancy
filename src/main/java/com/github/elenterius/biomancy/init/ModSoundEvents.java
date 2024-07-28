package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomancyMod.MOD_ID);

	//# Attacks
	public static final RegistryObject<SoundEvent> CLAWS_ATTACK_STRONG = register("claws.attack.strong");
	public static final RegistryObject<SoundEvent> CLAWS_ATTACK_BLEED_PROC = register("claws.attack.bleed_proc");

	//# Items
	public static final RegistryObject<SoundEvent> INJECTOR_INJECT = register("item.injector.inject");
	public static final RegistryObject<SoundEvent> INJECTOR_FAIL = register("item.injector.fail");
	public static final RegistryObject<SoundEvent> MARROW_DRINK = register("item.bone_marrow.drink");

	//# Blocks
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_PLACE = register("flesh_block.place");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_HIT = register("flesh_block.hit");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_BREAK = register("flesh_block.break");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_STEP = register("flesh_block.step");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_FALL = register("flesh_block.fall");

	public static final RegistryObject<SoundEvent> BONY_FLESH_BLOCK_PLACE = register("bony_flesh_block.place");
	public static final RegistryObject<SoundEvent> BONY_FLESH_BLOCK_HIT = register("bony_flesh_block.hit");
	public static final RegistryObject<SoundEvent> BONY_FLESH_BLOCK_BREAK = register("bony_flesh_block.break");
	public static final RegistryObject<SoundEvent> BONY_FLESH_BLOCK_STEP = register("bony_flesh_block.step");
	public static final RegistryObject<SoundEvent> BONY_FLESH_BLOCK_FALL = register("bony_flesh_block.fall");

	public static final RegistryObject<SoundEvent> FLESH_DOOR_OPEN = register("flesh_door.open");
	public static final RegistryObject<SoundEvent> FLESH_DOOR_CLOSE = register("flesh_door.close");

	//# Fleshkin
	public static final RegistryObject<SoundEvent> FLESHKIN_NO = register("fleshkin.no");
	public static final RegistryObject<SoundEvent> FLESHKIN_EAT = register("fleshkin.eat");
	public static final RegistryObject<SoundEvent> FLESHKIN_BREAK = register("fleshkin.break");
	public static final RegistryObject<SoundEvent> FLESHKIN_BECOME_DORMANT = register("fleshkin.dormant");
	public static final RegistryObject<SoundEvent> FLESHKIN_BECOME_AWAKENED = register("fleshkin.awakened");

	//## Misc
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_OPEN = register("fleshkin_chest.open");
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_CLOSE = register("fleshkin_chest.close");
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_BITE_ATTACK = register("fleshkin_chest.bite_attack");
	public static final RegistryObject<SoundEvent> CRADLE_SPIKE_ATTACK = register("cradle.spike_attack");
	public static final RegistryObject<SoundEvent> CRADLE_CRAFTING_RANDOM = register("block.cradle.crafting_random");
	public static final RegistryObject<SoundEvent> CRADLE_SPAWN_MOB = register("block.cradle.spawn_mob");
	public static final RegistryObject<SoundEvent> CRADLE_SPAWN_PRIMORDIAL_MOB = register("block.cradle.spawn_primordial_mob");
	public static final RegistryObject<SoundEvent> CRADLE_BECAME_FULL = register("block.cradle.became_full");
	public static final RegistryObject<SoundEvent> CRADLE_EAT = register("block.cradle.eat");
	public static final RegistryObject<SoundEvent> CRADLE_NO = register("block.cradle.no");

	//## Crafting
	public static final RegistryObject<SoundEvent> DECOMPOSER_CRAFTING = register("block.decomposer.crafting");
	public static final RegistryObject<SoundEvent> DECOMPOSER_EAT = register("block.decomposer.eat");
	public static final RegistryObject<SoundEvent> DECOMPOSER_CRAFTING_RANDOM = register("block.decomposer.crafting_random");
	public static final RegistryObject<SoundEvent> DECOMPOSER_CRAFTING_COMPLETED = register("block.decomposer.crafting_completed");
	public static final RegistryObject<SoundEvent> DIGESTER_CRAFTING = register("block.digester.crafting");
	public static final RegistryObject<SoundEvent> DIGESTER_CRAFTING_RANDOM = register("block.digester.crafting_random");
	public static final RegistryObject<SoundEvent> DIGESTER_CRAFTING_COMPLETED = register("block.digester.crafting_completed");
	public static final RegistryObject<SoundEvent> BIO_LAB_CRAFTING = register("block.bio_lab.crafting");
	public static final RegistryObject<SoundEvent> BIO_LAB_CRAFTING_RANDOM = register("block.bio_lab.crafting_random");
	public static final RegistryObject<SoundEvent> BIO_LAB_CRAFTING_COMPLETED = register("block.bio_lab.crafting_completed");

	//# UI
	public static final RegistryObject<SoundEvent> UI_BUTTON_CLICK = register("ui.button.click");
	public static final RegistryObject<SoundEvent> UI_RADIAL_MENU_OPEN = register("ui.radial_menu.open");
	public static final RegistryObject<SoundEvent> UI_BIO_FORGE_SELECT_RECIPE = register("ui.bio_forge.select_recipe");
	public static final RegistryObject<SoundEvent> UI_BIO_FORGE_TAKE_RESULT = register("ui.bio_forge.take_result");
	public static final RegistryObject<SoundEvent> UI_MENU_OPEN = register("ui.menu.open");
	public static final RegistryObject<SoundEvent> UI_STORAGE_SAC_OPEN = register("ui.storage_sac.open");
	public static final RegistryObject<SoundEvent> UI_BIO_FORGE_OPEN = register("ui.bio_forge.open");
	public static final RegistryObject<SoundEvent> UI_DECOMPOSER_OPEN = register("ui.decomposer.open");
	public static final RegistryObject<SoundEvent> UI_BIO_LAB_OPEN = register("ui.bio_lab.open");
	public static final RegistryObject<SoundEvent> UI_DIGESTER_OPEN = register("ui.digester.open");

	//# Mobs
	public static final RegistryObject<SoundEvent> FLESH_BLOB_JUMP = register("entity.flesh_blob.jump");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_HURT = register("entity.flesh_blob.hurt");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_DEATH = register("entity.flesh_blob.death");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_AMBIENT = register("entity.flesh_blob.ambient");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_MEW_PURR = register("entity.flesh_blob.mew_purr");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_GROWL = register("entity.flesh_blob.growl");
	public static final RegistryObject<SoundEvent> FLESH_COW_AMBIENT = register("entity.flesh_cow.ambient");
	public static final RegistryObject<SoundEvent> FLESH_COW_HURT = register("entity.flesh_cow.hurt");
	public static final RegistryObject<SoundEvent> FLESH_COW_DEATH = register("entity.flesh_cow.death");
	public static final RegistryObject<SoundEvent> FLESH_SHEEP_AMBIENT = register("entity.flesh_sheep.ambient");
	public static final RegistryObject<SoundEvent> FLESH_SHEEP_HURT = register("entity.flesh_sheep.hurt");
	public static final RegistryObject<SoundEvent> FLESH_SHEEP_DEATH = register("entity.flesh_sheep.death");
	public static final RegistryObject<SoundEvent> FLESH_PIG_AMBIENT = register("entity.flesh_pig.ambient");
	public static final RegistryObject<SoundEvent> FLESH_PIG_HURT = register("entity.flesh_pig.hurt");
	public static final RegistryObject<SoundEvent> FLESH_PIG_DEATH = register("entity.flesh_pig.death");
	public static final RegistryObject<SoundEvent> FLESH_CHICKEN_AMBIENT = register("entity.flesh_chicken.ambient");
	public static final RegistryObject<SoundEvent> FLESH_CHICKEN_HURT = register("entity.flesh_chicken.hurt");
	public static final RegistryObject<SoundEvent> FLESH_CHICKEN_DEATH = register("entity.flesh_chicken.death");

	private ModSoundEvents() {}

	private static RegistryObject<SoundEvent> register(String name) {
		return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(BiomancyMod.createRL(name)));
	}

	private static RegistryObject<SoundEvent> register(String name, float fixedRange) {
		return SOUND_EVENTS.register(name, () -> SoundEvent.createFixedRangeEvent(BiomancyMod.createRL(name), fixedRange));
	}

}
