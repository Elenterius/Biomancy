package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundProvider extends SoundDefinitionsProvider {

	protected ModSoundProvider(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, BiomancyMod.MOD_ID, helper);
	}

	@Override
	public void registerSounds() {
		addSimple(ModSoundEvents.INJECT);
		addSimple(ModSoundEvents.FLESH_BLOCK_HIT);
		addSimple(ModSoundEvents.FLESH_BLOCK_PLACE);
		addSimple(ModSoundEvents.FLESH_BLOCK_STEP);
	}

	private void addSimple(RegistryObject<SoundEvent> soundHolder) {
		add(soundHolder, definition()
				.subtitle("sounds.biomancy." + soundHolder.getId().getPath())
				.with(sound(soundHolder.getId()))
		);
	}

}
