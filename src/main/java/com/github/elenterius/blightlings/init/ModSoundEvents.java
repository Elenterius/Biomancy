package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BlightlingsMod.MOD_ID);

    public static final RegistryObject<SoundEvent> WAH_WAH = SOUND_EVENTS.register("wahwah", () -> createSoundEvent("wahwah"));
//    public static final RegistryObject<SoundEvent> IMPACT_SPLAT = SOUND_EVENTS.register("impactsplat", () -> createSoundEvent("impactsplat"));

    private static SoundEvent createSoundEvent(String name) {
        return new SoundEvent(new ResourceLocation(BlightlingsMod.MOD_ID, name));
    }
}
