package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.client.renderer.entity.BloblingRenderer;
import com.github.elenterius.blightlings.client.renderer.entity.BroodmotherRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public abstract class ClientSetupHandler
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BLOBLING.get(), BloblingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BROOD_MOTHER.get(), BroodmotherRenderer::new);
    }

    @SubscribeEvent
    public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, index) -> 0xff5eeb, ModItems.BLIGHT_GOO.get());
        event.getItemColors().register((stack, index) -> 0xf99fee, ModItems.BLIGHT_STRING.get());
    }

}
