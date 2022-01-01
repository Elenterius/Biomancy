package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<SoundEvent> WAH_WAH = registerSoundEvent("wahwah");
	public static final RegistryObject<SoundEvent> INJECT = registerSoundEvent("inject");
//	public static final RegistryObject<SoundEvent> SQUISH_0 = registerSoundEvent("squish_0");

	private ModSoundEvents() {}

	private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(BiomancyMod.createRL(name)));
	}

}
