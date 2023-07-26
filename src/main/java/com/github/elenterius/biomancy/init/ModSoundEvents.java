package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomancyMod.MOD_ID);

	//# Attacks
	public static final RegistryObject<SoundEvent> CLAWS_ATTACK_STRONG = registerSoundEvent("claws.attack.strong");
	public static final RegistryObject<SoundEvent> CLAWS_ATTACK_BLEED_PROC = registerSoundEvent("claws.attack.bleed_proc");

	//# Items
	public static final RegistryObject<SoundEvent> INJECTOR_INJECT = registerSoundEvent("item.injector.inject");
	public static final RegistryObject<SoundEvent> INJECTOR_FAIL = registerSoundEvent("item.injector.fail");
	public static final RegistryObject<SoundEvent> MARROW_DRINK = registerSoundEvent("item.bone_marrow.drink");

	//# Blocks
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_PLACE = registerSoundEvent("flesh_block.place");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_HIT = registerSoundEvent("flesh_block.hit");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_BREAK = registerSoundEvent("flesh_block.break");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_STEP = registerSoundEvent("flesh_block.step");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_FALL = registerSoundEvent("flesh_block.fall");
	public static final RegistryObject<SoundEvent> FLESH_DOOR_OPEN = registerSoundEvent("flesh_door.open");
	public static final RegistryObject<SoundEvent> FLESH_DOOR_CLOSE = registerSoundEvent("flesh_door.close");

	//# Fleshkin
	public static final RegistryObject<SoundEvent> FLESHKIN_NO = registerSoundEvent("fleshkin.no");
	public static final RegistryObject<SoundEvent> FLESHKIN_EAT = registerSoundEvent("fleshkin.eat");
	public static final RegistryObject<SoundEvent> FLESHKIN_BREAK = registerSoundEvent("fleshkin.break");
	public static final RegistryObject<SoundEvent> FLESHKIN_BECOME_DORMANT = registerSoundEvent("fleshkin.dormant");
	public static final RegistryObject<SoundEvent> FLESHKIN_BECOME_AWAKENED = registerSoundEvent("fleshkin.awakened");

	//## Misc
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_OPEN = registerSoundEvent("fleshkin_chest.open");
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_CLOSE = registerSoundEvent("fleshkin_chest.close");
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_BITE_ATTACK = registerSoundEvent("fleshkin_chest.bite_attack");
	public static final RegistryObject<SoundEvent> CREATOR_SPIKE_ATTACK = registerSoundEvent("creator.spike_attack");
	public static final RegistryObject<SoundEvent> CREATOR_CRAFTING_RANDOM = registerSoundEvent("block.creator.crafting_random");
	public static final RegistryObject<SoundEvent> CREATOR_SPAWN_MOB = registerSoundEvent("block.creator.spawn_mob");
	public static final RegistryObject<SoundEvent> CREATOR_BECAME_FULL = registerSoundEvent("block.creator.became_full");
	public static final RegistryObject<SoundEvent> CREATOR_EAT = registerSoundEvent("block.creator.eat");
	public static final RegistryObject<SoundEvent> CREATOR_NO = registerSoundEvent("block.creator.no");

	//## Crafting
	public static final RegistryObject<SoundEvent> DECOMPOSER_CRAFTING = registerSoundEvent("block.decomposer.crafting");
	public static final RegistryObject<SoundEvent> DECOMPOSER_EAT = registerSoundEvent("block.decomposer.eat");
	public static final RegistryObject<SoundEvent> DECOMPOSER_CRAFTING_RANDOM = registerSoundEvent("block.decomposer.crafting_random");
	public static final RegistryObject<SoundEvent> DECOMPOSER_CRAFTING_COMPLETED = registerSoundEvent("block.decomposer.crafting_completed");
	public static final RegistryObject<SoundEvent> DIGESTER_CRAFTING = registerSoundEvent("block.digester.crafting");
	public static final RegistryObject<SoundEvent> DIGESTER_CRAFTING_RANDOM = registerSoundEvent("block.digester.crafting_random");
	public static final RegistryObject<SoundEvent> DIGESTER_CRAFTING_COMPLETED = registerSoundEvent("block.digester.crafting_completed");
	public static final RegistryObject<SoundEvent> BIO_LAB_CRAFTING = registerSoundEvent("block.bio_lab.crafting");
	public static final RegistryObject<SoundEvent> BIO_LAB_CRAFTING_RANDOM = registerSoundEvent("block.bio_lab.crafting_random");
	public static final RegistryObject<SoundEvent> BIO_LAB_CRAFTING_COMPLETED = registerSoundEvent("block.bio_lab.crafting_completed");

	//# UI
	public static final RegistryObject<SoundEvent> UI_BUTTON_CLICK = registerSoundEvent("ui.button.click");
	public static final RegistryObject<SoundEvent> UI_RADIAL_MENU_OPEN = registerSoundEvent("ui.radial_menu.open");
	public static final RegistryObject<SoundEvent> UI_BIO_FORGE_SELECT_RECIPE = registerSoundEvent("ui.bio_forge.select_recipe");
	public static final RegistryObject<SoundEvent> UI_BIO_FORGE_TAKE_RESULT = registerSoundEvent("ui.bio_forge.take_result");
	public static final RegistryObject<SoundEvent> UI_MENU_OPEN = registerSoundEvent("ui.menu.open");
	public static final RegistryObject<SoundEvent> UI_STORAGE_SAC_OPEN = registerSoundEvent("ui.storage_sac.open");
	public static final RegistryObject<SoundEvent> UI_BIO_FORGE_OPEN = registerSoundEvent("ui.bio_forge.open");
	public static final RegistryObject<SoundEvent> UI_DECOMPOSER_OPEN = registerSoundEvent("ui.decomposer.open");
	public static final RegistryObject<SoundEvent> UI_BIO_LAB_OPEN = registerSoundEvent("ui.bio_lab.open");
	public static final RegistryObject<SoundEvent> UI_DIGESTER_OPEN = registerSoundEvent("ui.digester.open");

	//# Mobs
	public static final RegistryObject<SoundEvent> FLESH_BLOB_JUMP = registerSoundEvent("entity.flesh_blob.jump");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_HURT = registerSoundEvent("entity.flesh_blob.hurt");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_DEATH = registerSoundEvent("entity.flesh_blob.death");
	public static final RegistryObject<SoundEvent> FLESH_BLOB_AMBIENT = registerSoundEvent("entity.flesh_blob.ambient");

	private ModSoundEvents() {}

	private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(BiomancyMod.createRL(name)));
	}

}
