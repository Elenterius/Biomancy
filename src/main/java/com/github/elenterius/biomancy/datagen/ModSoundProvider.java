package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;

public class ModSoundProvider extends SoundDefinitionsProvider {

	protected ModSoundProvider(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, BiomancyMod.MOD_ID, helper);
	}

	@Override
	public void registerSounds() {
		addSimpleSound(ModSoundEvents.INJECT);

		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_HIT, "1_2", "1_3", "1_6", "2_2");
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_PLACE, "1_4", "3");
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_STEP, "1", "2", "3", "4");
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_BREAK, "5", "6");
		addSimpleRedirect(ModSoundEvents.FLESH_BLOCK_FALL, SoundEvents.SLIME_BLOCK_FALL);

		addSimpleSounds(ModSoundEvents.FLESHY_DOOR_OPEN, "1");
		addSimpleRedirect(ModSoundEvents.FLESHY_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_CLOSE);

		addSimpleSounds(ModSoundEvents.FLESHKIN_CHEST_OPEN, "1");
		addSimpleSounds(ModSoundEvents.FLESHKIN_CHEST_CLOSE, "1", "1_1");
	}

	public String translationKey(RegistryObject<SoundEvent> soundHolder) {
		return "sounds.biomancy." + soundHolder.getId().getPath();
	}

	public ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + "_" + suffix);
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

	protected void addSimpleRedirect(RegistryObject<SoundEvent> soundHolder, SoundEvent redirectTarget) {
		add(soundHolder, definition()
				.subtitle(translationKey(soundHolder))
				.with(sound(redirectTarget.getLocation(), SoundDefinition.SoundType.EVENT))
		);
	}

}
