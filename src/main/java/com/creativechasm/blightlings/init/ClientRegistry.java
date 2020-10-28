package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.client.renderer.entity.BloblingRenderer;
import com.creativechasm.blightlings.client.renderer.entity.BroodmotherRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public abstract class ClientRegistry
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(CommonRegistry.EntityTypes.BLOBLING, BloblingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CommonRegistry.EntityTypes.BROOD_MOTHER, BroodmotherRenderer::new);
    }

}
