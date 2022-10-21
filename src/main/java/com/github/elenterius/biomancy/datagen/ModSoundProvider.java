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
		addSimpleRedirect(ModSoundEvents.ACTION_FAIL, SoundEvents.DISPENSER_FAIL);

		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_HIT, 4);
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_PLACE, 2);
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_STEP, 4);
		addSimpleSounds(ModSoundEvents.FLESH_BLOCK_BREAK, 4);
        addSimpleSounds(ModSoundEvents.FLESH_BLOCK_FALL, 3);

		addSimpleSounds(ModSoundEvents.FLESH_DOOR_OPEN, 2);
        addSimpleSounds(ModSoundEvents.FLESH_DOOR_CLOSE, 2);

		addSimpleSounds(ModSoundEvents.FLESHKIN_CHEST_OPEN, 2);
		addSimpleSounds(ModSoundEvents.FLESHKIN_CHEST_CLOSE, 2);
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
