package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomancyMod.MOD_ID);
	public static final RegistryObject<SoundEvent> WAH_WAH = SOUND_EVENTS.register("wahwah", () -> createSoundEvent("wahwah"));
	//    public static final RegistryObject<SoundEvent> IMPACT_SPLAT = SOUND_EVENTS.register("impactsplat", () -> createSoundEvent("impactsplat"));

	private ModSoundEvents() {}

	private static SoundEvent createSoundEvent(String name) {
		return new SoundEvent(new ResourceLocation(BiomancyMod.MOD_ID, name));
	}
}
