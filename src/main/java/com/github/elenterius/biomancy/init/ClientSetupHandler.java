package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.block.FullBrightOverlayBakedModel;
import com.github.elenterius.biomancy.client.renderer.entity.*;
import com.github.elenterius.biomancy.client.renderer.tileentity.FleshChestTileEntityRenderer;
import com.github.elenterius.biomancy.item.weapon.LongRangeClawItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetupHandler {
	private ClientSetupHandler() {}

	public static final KeyBinding ITEM_DEFAULT_KEY_BINDING = new KeyBinding(String.format("key.%s.item_default", BiomancyMod.MOD_ID), KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories." + BiomancyMod.MOD_ID);

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(ITEM_DEFAULT_KEY_BINDING);

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.FLESH_CHEST.get(), FleshChestTileEntityRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FLESH_BLOB.get(), FleshBlobRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FLESHKIN.get(), FleshkinRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BOOMLING.get(), BoomlingRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.MASON_BEETLE.get(), BlockBeetleRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FAILED_SHEEP.get(), FailedSheepRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.CHROMA_SHEEP.get(), ChromaSheepRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.SILKY_WOOL_SHEEP.get(), SheepRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.THICK_WOOL_SHEEP.get(), ThickWoolSheepRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BEETLING.get(), BeetlingRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BROOD_MOTHER.get(), BroodmotherRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.TOOTH_PROJECTILE.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), WitherSkullProjectileRenderer::new);

		event.enqueueWork(() -> {
			ModContainerTypes.registerContainerScreens();

			ItemModelsProperties.registerProperty(ModItems.LONG_RANGE_CLAW.get(), new ResourceLocation("extended"), (stack, clientWorld, livingEntity) -> LongRangeClawItem.isClawExtended(stack) ? 1f : 0f);
			ItemModelsProperties.registerProperty(ModItems.SINGLE_ITEM_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.SINGLE_ITEM_BAG_ITEM.get().getFullness(stack));
			ItemModelsProperties.registerProperty(ModItems.ENTITY_STORAGE_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.ENTITY_STORAGE_ITEM.get().getFullness(stack));
//			ItemModelsProperties.registerProperty(ModItems.SINEW_BOW.get(), new ResourceLocation("pull"), (stack, clientWorld, livingEntity) -> livingEntity == null || livingEntity.getActiveItemStack() != stack ? 0f : ModItems.SINEW_BOW.get().getPullProgress(stack, livingEntity));
//			ItemModelsProperties.registerProperty(ModItems.SINEW_BOW.get(), new ResourceLocation("pulling"), (stack, clientWorld, livingEntity) -> livingEntity != null && livingEntity.isHandActive() && livingEntity.getActiveItemStack() == stack ? 1f : 0f);
			ItemModelsProperties.registerProperty(ModItems.BONE_SCRAPS.get(), new ResourceLocation("type"), (stack, clientWorld, livingEntity) -> stack.getOrCreateTag().getInt("ScrapType"));

			ModBlocks.setRenderLayers();
		});
	}

	@SubscribeEvent
	public static void onBlockModelRegistry(final ModelRegistryEvent event) {}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
//		event.getItemColors().register((stack, index) -> 0xff6981, ModItems.VILE_MELON_BLOCK.get(), ModItems.VILE_MELON_SEEDS.get(), ModItems.VILE_MELON_SLICE.get());
//		event.getItemColors().register((stack, index) -> 0x6c2e1f, ModItems.COOKED_VILE_MELON_SLICE.get());
		event.getItemColors().register((stack, index) -> 0x8d758c, ModItems.NECROTIC_FLESH.get(), ModItems.NECROTIC_FLESH_BLOCK.get());

		event.getItemColors().register((stack, index) -> index == 1 ? ModItems.INJECTION_DEVICE.get().getReagentColor(stack) : -1, ModItems.INJECTION_DEVICE.get());
		event.getItemColors().register((stack, index) -> index == 0 ? ModItems.REAGENT.get().getReagentColor(stack) : -1, ModItems.REAGENT.get());
		event.getItemColors().register((stack, index) -> index == 1 ? ModItems.BOOMLING_GRENADE.get().getPotionColor(stack) : -1, ModItems.BOOMLING_GRENADE.get());
	}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Block event) {
//		event.getBlockColors().register((state, displayReader, pos, index) -> 0xff6981, ModBlocks.VILE_MELON_BLOCK.get(), ModBlocks.VILE_MELON_CROP.get());
		event.getBlockColors().register((state, displayReader, pos, index) -> 0x8d758c, ModBlocks.NECROTIC_FLESH_BLOCK.get());
	}

	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre event) {
		if (!event.getMap().getTextureLocation().equals(Atlases.CHEST_ATLAS)) {
			return;
		}

		event.addSprite(FleshChestTileEntityRenderer.FLESH_CHEST_TEXTURE);
	}

	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event) {
		//block with "glowing" overlay texture
		//addFullBrightOverlayBakedModel(ModBlocks.FOOBAR.get(), event);
	}

	private static void addFullBrightOverlayBakedModel(Block block, ModelBakeEvent event) {
		for (BlockState blockState : block.getStateContainer().getValidStates()) {
			ModelResourceLocation modelLocation = BlockModelShapes.getModelLocation(blockState);
			IBakedModel bakedModel = event.getModelRegistry().get(modelLocation);
			if (bakedModel == null) {
				BiomancyMod.LOGGER.warn(MarkerManager.getMarker("ModelBakeEvent"), "Did not find any vanilla baked models for " + block.getRegistryName());
			}
			else if (bakedModel instanceof FullBrightOverlayBakedModel) {
				BiomancyMod.LOGGER.warn(MarkerManager.getMarker("ModelBakeEvent"), "Attempted to replace already existing FullBrightOverlayBakedModel for " + block.getRegistryName());
			}
			else {
				FullBrightOverlayBakedModel customModel = new FullBrightOverlayBakedModel(bakedModel);
				event.getModelRegistry().put(modelLocation, customModel);
			}
		}
	}
}
