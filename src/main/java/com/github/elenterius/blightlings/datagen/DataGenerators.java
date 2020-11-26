package com.github.elenterius.blightlings.datagen;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators
{

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new ModLootTableProvider(generator));
//        generator.addProvider(new ModRecipesProvider(generator));
    }

}