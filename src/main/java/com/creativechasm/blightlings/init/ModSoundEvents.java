package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BlightlingsMod.MOD_ID)
public class ModSoundEvents
{
    @ObjectHolder("impactsplat")
    public static SoundEvent IMPACT_SPLAT;

    @ObjectHolder("wahwah")
    public static SoundEvent WAH_WAH;

    protected static SoundEvent createSoundEvent(String name) {
        final ResourceLocation identifier = new ResourceLocation(BlightlingsMod.MOD_ID, name);
        return new SoundEvent(identifier).setRegistryName(identifier);
    }
}
