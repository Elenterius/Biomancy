package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;

public class ModSoundProvider extends SoundDefinitionsProvider {

	protected ModSoundProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, BiomancyMod.MOD_ID, helper);
	}

	@Override
	public void registerSounds() {

		addSimpleRedirect(ModSoundEvents.CLAWS_ATTACK_STRONG, SoundEvents.PLAYER_ATTACK_SWEEP, 0.85f, 0.9f);
		addSimpleRedirect(ModSoundEvents.CLAWS_ATTACK_BLEED_PROC, SoundEvents.PLAYER_ATTACK_CRIT, 1f, 1.5f);

		addSimpleSound(ModSoundEvents.INJECTOR_INJECT);
		addSimpleRedirect(ModSoundEvents.INJECTOR_FAIL, SoundEvents.DISPENSER_FAIL);
		addSimpleRedirect(ModSoundEvents.MARROW_DRINK, SoundEvents.HONEY_DRINK, 0.9f, 1.25f);

		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_HIT, 4);
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_PLACE, 2);
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_STEP, 4);
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_BREAK, 4);
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_FALL, 3);

		addSimpleRedirects(ModSoundEvents.BONY_FLESH_BLOCK_HIT, ModSoundEvents.FLESH_BLOCK_HIT.get(), SoundEvents.BONE_BLOCK_HIT);
		addSimpleRedirects(ModSoundEvents.BONY_FLESH_BLOCK_PLACE, ModSoundEvents.FLESH_BLOCK_PLACE.get(), SoundEvents.BONE_BLOCK_PLACE);
		addSimpleRedirects(ModSoundEvents.BONY_FLESH_BLOCK_STEP, ModSoundEvents.FLESH_BLOCK_STEP.get(), SoundEvents.BONE_BLOCK_STEP);
		addSimpleRedirects(ModSoundEvents.BONY_FLESH_BLOCK_BREAK, ModSoundEvents.FLESH_BLOCK_BREAK.get(), SoundEvents.BONE_BLOCK_BREAK);
		addSimpleRedirects(ModSoundEvents.BONY_FLESH_BLOCK_FALL, ModSoundEvents.FLESH_BLOCK_FALL.get(), SoundEvents.BONE_BLOCK_FALL);

		addSimpleSounds(ModSoundEvents.FLESH_DOOR_OPEN, 2);
		addSimpleSounds(ModSoundEvents.FLESH_DOOR_CLOSE, 2);

		addSimpleRedirect(ModSoundEvents.FLESHKIN_NO, SoundEvents.VILLAGER_NO, 0.75f, 0.3f);
		addSimpleRedirect(ModSoundEvents.FLESHKIN_EAT, SoundEvents.GENERIC_EAT, 0.9f, 0.9f);
		addSimpleRedirect(ModSoundEvents.FLESHKIN_BREAK, ModSoundEvents.FLESH_BLOCK_BREAK, 1.2f, 1.25f);
		addSimpleRedirect(ModSoundEvents.FLESHKIN_BECOME_DORMANT, ModSoundEvents.FLESH_BLOCK_HIT, 1.2f, 1.25f);
		addSimpleRedirect(ModSoundEvents.FLESHKIN_BECOME_AWAKENED, ModSoundEvents.FLESH_BLOCK_PLACE, 1.2f, 1.25f);

		addSimpleSounds(ModSoundEvents.FLESHKIN_CHEST_OPEN, 2);
		addSimpleSounds(ModSoundEvents.FLESHKIN_CHEST_CLOSE, 2);
		addSimpleRedirect(ModSoundEvents.FLESHKIN_CHEST_BITE_ATTACK, SoundEvents.GOAT_SCREAMING_RAM_IMPACT, 1f, 0.5f);

		addSimpleRedirect(ModSoundEvents.CRADLE_SPIKE_ATTACK, SoundEvents.GOAT_SCREAMING_RAM_IMPACT, 1f, 0.5f);
		addSimpleRedirect(ModSoundEvents.CRADLE_CRAFTING_RANDOM, SoundEvents.PLAYER_BURP, 1f, 0.5f);
		addSimpleRedirect(ModSoundEvents.CRADLE_SPAWN_MOB, SoundEvents.PLAYER_BURP, 1.5f, 0.55f);
		addSimpleRedirect(ModSoundEvents.CRADLE_SPAWN_PRIMORDIAL_MOB, SoundEvents.FOX_SCREECH, 2f, 0.5f);
		addSimpleRedirect(ModSoundEvents.CRADLE_BECAME_FULL, SoundEvents.PLAYER_BURP, 1f, 0.25f);
		addSimpleRedirect(ModSoundEvents.CRADLE_EAT, SoundEvents.GOAT_SCREAMING_EAT, 1f, 0.25f);
		addSimpleRedirect(ModSoundEvents.CRADLE_NO, SoundEvents.VILLAGER_NO, 0.75f, 0.3f);

		addSimpleRedirect(ModSoundEvents.UI_BUTTON_CLICK, SoundEvents.UI_BUTTON_CLICK.get());
		addSimpleSound(ModSoundEvents.UI_MENU_OPEN);
		addSimpleRedirect(ModSoundEvents.UI_RADIAL_MENU_OPEN, SoundEvents.SHULKER_BOX_OPEN, 0.5f, 1f);

		addSimpleRedirect(ModSoundEvents.UI_STORAGE_SAC_OPEN, ModSoundEvents.UI_MENU_OPEN);

		addSimpleRedirect(ModSoundEvents.UI_BIO_FORGE_OPEN, ModSoundEvents.UI_MENU_OPEN);
		addSimpleRedirect(ModSoundEvents.UI_BIO_FORGE_SELECT_RECIPE, SoundEvents.UI_STONECUTTER_SELECT_RECIPE);
		addSimpleRedirect(ModSoundEvents.UI_BIO_FORGE_TAKE_RESULT, SoundEvents.PLAYER_BURP, 1f, 1.25f);

		addSimpleSound(ModSoundEvents.DECOMPOSER_CRAFTING, 0.5f, 1f);
		addSimpleRedirect(ModSoundEvents.UI_DECOMPOSER_OPEN, ModSoundEvents.UI_MENU_OPEN);
		addSimpleRedirect(ModSoundEvents.DECOMPOSER_EAT, SoundEvents.GENERIC_EAT, 1f, 0.5f);
		addSimpleRedirect(ModSoundEvents.DECOMPOSER_CRAFTING_RANDOM, SoundEvents.PLAYER_BURP, 0.9f, 0.5f);
		addSimpleRedirect(ModSoundEvents.DECOMPOSER_CRAFTING_COMPLETED, SoundEvents.PLAYER_BURP, 1f, 1.5f);

		addSimpleSound(ModSoundEvents.BIO_LAB_CRAFTING);
		addSimpleRedirect(ModSoundEvents.UI_BIO_LAB_OPEN, ModSoundEvents.UI_MENU_OPEN);
		addSimpleRedirect(ModSoundEvents.BIO_LAB_CRAFTING_RANDOM, SoundEvents.BREWING_STAND_BREW, 0.9f, 0.5f);
		addSimpleRedirect(ModSoundEvents.BIO_LAB_CRAFTING_COMPLETED, SoundEvents.WITCH_DRINK);

		addSimpleSound(ModSoundEvents.DIGESTER_CRAFTING);
		addSimpleRedirect(ModSoundEvents.UI_DIGESTER_OPEN, ModSoundEvents.UI_MENU_OPEN);
		addSimpleRedirect(ModSoundEvents.DIGESTER_CRAFTING_RANDOM, SoundEvents.PLAYER_BURP, 0.9f, 0.5f);
		addSimpleRedirect(ModSoundEvents.DIGESTER_CRAFTING_COMPLETED, SoundEvents.PLAYER_BURP, 1f, 1.25f);

		addSimpleRedirect(ModSoundEvents.FLESH_BLOB_JUMP, SoundEvents.SLIME_JUMP);
		addSimpleRedirect(ModSoundEvents.FLESH_BLOB_HURT, ModSoundEvents.FLESH_BLOCK_BREAK, 0.8f, 0.9f);
		addSimpleRedirect(ModSoundEvents.FLESH_BLOB_DEATH, SoundEvents.GENERIC_DEATH);
		addSimpleSounds(ModSoundEvents.FLESH_BLOB_AMBIENT, 3, 0.8f, 0.9f);
		addSimpleSound(ModSoundEvents.FLESH_BLOB_MEW_PURR, 0.7f, 0.45f);
		addSimpleSound(ModSoundEvents.FLESH_BLOB_GROWL, 0.9f, 0.55f);
	}

	public String translationKey(RegistryObject<SoundEvent> soundHolder) {
		return "sounds.biomancy." + soundHolder.getId().getPath();
	}

	public ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + "_" + suffix);
	}

	public ResourceLocation extend(ResourceLocation rl, int variant) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + variant);
	}

	protected void addSimpleSounds(RegistryObject<SoundEvent> soundHolder, int variants) {
		SoundDefinition soundDefinition = definition().subtitle(translationKey(soundHolder));
		for (int i = 1; i <= variants; i++) {
			soundDefinition.with(SoundDefinitionsProvider.sound(extend(soundHolder.getId(), i)));
		}
		add(soundHolder, soundDefinition);
	}

	protected void addSimpleSounds(RegistryObject<SoundEvent> soundHolder, int variants, float volume, float pitch) {
		SoundDefinition soundDefinition = definition().subtitle(translationKey(soundHolder));
		for (int i = 1; i <= variants; i++) {
			soundDefinition.with(SoundDefinitionsProvider.sound(extend(soundHolder.getId(), i)).volume(volume).pitch(pitch));
		}
		add(soundHolder, soundDefinition);
	}

	protected void addSimpleSounds(RegistryObject<SoundEvent> soundHolder, String... suffixes) {
		SoundDefinition soundDefinition = definition().subtitle(translationKey(soundHolder));
		Arrays.stream(suffixes)
				.map(suffix -> extend(soundHolder.getId(), suffix))
				.map(SoundDefinitionsProvider::sound)
				.forEach(soundDefinition::with);

		add(soundHolder, soundDefinition);
	}

	protected void addSimpleSound(RegistryObject<SoundEvent> soundHolder) {
		add(soundHolder, definition()
				.subtitle(translationKey(soundHolder))
				.with(sound(soundHolder.getId()))
		);
	}

	protected void addSimpleSound(RegistryObject<SoundEvent> soundHolder, float volume, float pitch) {
		add(soundHolder, definition()
				.subtitle(translationKey(soundHolder))
				.with(sound(soundHolder.getId()).volume(volume).pitch(pitch))
		);
	}

	protected void addSimpleRedirect(RegistryObject<SoundEvent> soundHolder, SoundEvent redirectTarget) {
		add(soundHolder, definition()
				.subtitle(translationKey(soundHolder))
				.with(sound(redirectTarget.getLocation(), SoundDefinition.SoundType.EVENT))
		);
	}

	protected void addSimpleRedirect(RegistryObject<SoundEvent> soundHolder, RegistryObject<SoundEvent> redirectTarget) {
		add(soundHolder, definition()
				.subtitle(translationKey(soundHolder))
				.with(sound(redirectTarget.getId(), SoundDefinition.SoundType.EVENT))
		);
	}

	protected void addSimpleRedirect(RegistryObject<SoundEvent> soundHolder, SoundEvent redirectTarget, float volume, float pitch) {
		add(soundHolder, definition()
				.subtitle(translationKey(soundHolder))
				.with(sound(redirectTarget.getLocation(), SoundDefinition.SoundType.EVENT).volume(volume).pitch(pitch))
		);
	}

	protected void addSimpleRedirect(RegistryObject<SoundEvent> soundHolder, RegistryObject<SoundEvent> redirectTarget, float volume, float pitch) {
		add(soundHolder, definition()
				.subtitle(translationKey(soundHolder))
				.with(sound(redirectTarget.getId(), SoundDefinition.SoundType.EVENT).volume(volume).pitch(pitch))
		);
	}

	protected void addSimpleRedirects(RegistryObject<SoundEvent> soundHolder, SoundEvent... redirectTargets) {
		SoundDefinition soundDefinition = definition().subtitle(translationKey(soundHolder));

		for (SoundEvent redirectTarget : redirectTargets) {
			soundDefinition.with().with(sound(redirectTarget.getLocation(), SoundDefinition.SoundType.EVENT));
		}

		add(soundHolder, soundDefinition);
	}
}
