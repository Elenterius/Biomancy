package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.client.renderer.block.FullBrightOverlayBakedModel;
import com.github.elenterius.blightlings.client.renderer.entity.BloblingRenderer;
import com.github.elenterius.blightlings.client.renderer.entity.BroodmotherRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.MarkerManager;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public abstract class ClientSetupHandler
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BLOBLING.get(), BloblingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BROOD_MOTHER.get(), BroodmotherRenderer::new);

        RenderTypeLookup.setRenderLayer(ModBlocks.LILY_TREE_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.LUMINOUS_SOIL.get(), renderType -> renderType == RenderType.getCutout() || renderType == RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLIGHT_PUSTULE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLIGHT_SPROUT.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLIGHT_SPROUT_SMALL.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLIGHT_TENTACLE_0.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLIGHT_TENTACLE_1.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLIGHT_SHROOM_TALL.get(), RenderType.getCutout());
    }

    @SubscribeEvent
    public static void onBlockModelRegistry(final ModelRegistryEvent event) {

    }

    @SubscribeEvent
    public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, index) -> 0xff5eeb, ModItems.BLIGHT_GOO.get());
        event.getItemColors().register((stack, index) -> 0xf99fee, ModItems.BLIGHT_STRING.get());
    }

    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        for (BlockState blockState : ModBlocks.LUMINOUS_SOIL.get().getStateContainer().getValidStates()) {
            ModelResourceLocation modelLocation = BlockModelShapes.getModelLocation(blockState);
            IBakedModel bakedModel = event.getModelRegistry().get(modelLocation);
            if (bakedModel == null) {
                BlightlingsMod.LOGGER.warn(MarkerManager.getMarker("ModelBakeEvent"), "Did not find any vanilla baked models for block luminous_soil");
            }
            else if (bakedModel instanceof FullBrightOverlayBakedModel) {
                BlightlingsMod.LOGGER.warn(MarkerManager.getMarker("ModelBakeEvent"), "Attempted to replace already existing FullBrightOverlayBakedModel for block luminous_soil");
            }
            else {
                FullBrightOverlayBakedModel customModel = new FullBrightOverlayBakedModel(bakedModel);
                event.getModelRegistry().put(modelLocation, customModel);
            }
        }
    }
}
