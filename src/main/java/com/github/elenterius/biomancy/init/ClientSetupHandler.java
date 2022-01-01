package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.renderer.block.CreatorBlockEntityRenderer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetupHandler {

//	public static final KeyBinding ITEM_DEFAULT_KEY_BINDING = new KeyBinding(String.format("key.%s.item_default", BiomancyMod.MOD_ID), KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories." + BiomancyMod.MOD_ID);

	private ClientSetupHandler() {}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
//		ClientRegistry.registerKeyBinding(ITEM_DEFAULT_KEY_BINDING);
		event.enqueueWork(() -> {
			ModContainerTypes.registerContainerScreens();
			registerItemModelProperties();
			ModBlocks.setRenderLayers();
		});
	}

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntities.CREATOR.get(), CreatorBlockEntityRenderer::new);

		//event.registerBlockEntityRenderer(ModBlockEntities.FLESH_CHEST.get(), FleshChestTileEntityRenderer::new);
//
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FLESH_BLOB.get(), FleshBlobRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.OCULUS_OBSERVER.get(), OculusObserverRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FLESHKIN.get(), FleshkinRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BOOMLING.get(), BoomlingRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BEETLING.get(), BeetlingRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BROOD_MOTHER.get(), BroodmotherRenderer::new);
//
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FAILED_SHEEP.get(), FailedSheepRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.CHROMA_SHEEP.get(), ChromaSheepRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.SILKY_WOOL_SHEEP.get(), SilkyWoolSheepRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.THICK_WOOL_SHEEP.get(), ThickWoolSheepRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.NUTRIENT_SLURRY_COW.get(), NutrientSlurryCowRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.FAILED_COW.get(), FailedCowRenderer::new);
//
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.TOOTH_PROJECTILE.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), WitherSkullProjectileRenderer::new);
//		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.BOOMLING_PROJECTILE.get(), BoomlingProjectileRenderer::new);
	}

	private static void registerItemModelProperties() {
//		ItemModelsProperties.register(ModItems.LONG_RANGE_CLAW.get(), new ResourceLocation("extended"), (stack, clientWorld, livingEntity) -> LongRangeClawItem.isClawExtended(stack) ? 1f : 0f);
//		ItemModelsProperties.register(ModItems.SINGLE_ITEM_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.SINGLE_ITEM_BAG_ITEM.get().getFullness(stack));
//		ItemModelsProperties.register(ModItems.SMALL_ENTITY_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.SMALL_ENTITY_BAG_ITEM.get().getFullness(stack));
//		ItemModelsProperties.register(ModItems.LARGE_ENTITY_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.LARGE_ENTITY_BAG_ITEM.get().getFullness(stack));
//			ItemModelsProperties.registerProperty(ModItems.SINEW_BOW.get(), new ResourceLocation("pull"), (stack, clientWorld, livingEntity) -> livingEntity == null || livingEntity.getActiveItemStack() != stack ? 0f : ModItems.SINEW_BOW.get().getPullProgress(stack, livingEntity));
//			ItemModelsProperties.registerProperty(ModItems.SINEW_BOW.get(), new ResourceLocation("pulling"), (stack, clientWorld, livingEntity) -> livingEntity != null && livingEntity.isHandActive() && livingEntity.getActiveItemStack() == stack ? 1f : 0f);
//		ItemModelsProperties.register(ModItems.BONE_SCRAPS.get(), new ResourceLocation("type"), (stack, clientWorld, livingEntity) -> stack.getOrCreateTag().getInt("ScrapType"));
	}

	@SubscribeEvent
	public static void onBlockModelRegistry(final ModelRegistryEvent event) {}

	public static boolean isPlayerCosmeticVisible(Player player) {
		return HASHES.isValid(player.getGameProfile().getId());
	}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, index) -> 0x8d758c, ModItems.NECROTIC_FLESH.get(), ModItems.NECROTIC_FLESH_BLOCK.get());
//		event.getItemColors().register((stack, index) -> 0xedaeaa, ModItems.PROTEIN_BAR.get());
//
//		event.getItemColors().register((stack, index) -> index == 1 ? ModItems.INJECTION_DEVICE.get().getReagentColor(stack) : -1, ModItems.INJECTION_DEVICE.get());
//		event.getItemColors().register((stack, index) -> index == 0 ? ModItems.REAGENT.get().getReagentColor(stack) : -1, ModItems.REAGENT.get());
//		event.getItemColors().register((stack, index) -> index == 1 ? ModItems.BOOMLING.get().getPotionColor(stack) : -1, ModItems.BOOMLING.get());
//		event.getItemColors().register((stack, index) -> index == 1 ? ModItems.BOOMLING_HIVE_GUN.get().getPotionColor(stack) : -1, ModItems.BOOMLING_HIVE_GUN.get());
	}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Block event) {
		event.getBlockColors().register((state, displayReader, pos, index) -> 0x8d758c, ModBlocks.NECROTIC_FLESH_BLOCK.get());
//		event.getBlockColors().register((state, displayReader, pos, index) -> state.getValue(MeatsoupCauldronBlock.LEVEL) == MeatsoupCauldronBlock.MAX_LEVEL ? 0x8d758c : -1, ModBlocks.MEATSOUP_CAULDRON.get());
	}

	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre event) {
//		if (!event.getAtlas().location().equals()) {
//			return;
//		}
//
//		event.addSprite(FleshChestTileEntityRenderer.FLESH_CHEST_TEXTURE);
	}

	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event) {
		//block with "glowing" overlay texture
		//addFullBrightOverlayBakedModel(ModBlocks.FOOBAR.get(), event);
	}

	private static void addFullBrightOverlayBakedModel(Block block, ModelBakeEvent event) {
//		for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
//			ModelResourceLocation modelLocation = BlockModelShapes.stateToModelLocation(blockState);
//			IBakedModel bakedModel = event.getModelRegistry().get(modelLocation);
//			if (bakedModel == null) {
//				BiomancyMod.LOGGER.warn(MarkerManager.getMarker("ModelBakeEvent"), "Did not find any vanilla baked models for {}", block.getRegistryName());
//			}
//			else if (bakedModel instanceof FullBrightOverlayBakedModel) {
//				BiomancyMod.LOGGER.warn(MarkerManager.getMarker("ModelBakeEvent"), "Attempted to replace already existing FullBrightOverlayBakedModel for {}", block.getRegistryName());
//			}
//			else {
//				FullBrightOverlayBakedModel customModel = new FullBrightOverlayBakedModel(bakedModel);
//				event.getModelRegistry().put(modelLocation, customModel);
//			}
//		}
	}

	private static final class HASHES {

		private static final Set<HashCode> VALID = Set.of(
				HashCode.fromString("20f0bf6814e62bb7297669efb542f0af6ee0be1a9b87d0702853d8cc5aa15dc4")
		);

		private static final CacheLoader<UUID, HashCode> CACHE_LOADER = new CacheLoader<>() {
			@SuppressWarnings("NullableProblems")
			@Override
			public HashCode load(UUID key) {
				//noinspection UnstableApiUsage
				return Hashing.sha256().hashString(key.toString(), StandardCharsets.UTF_8);
			}
		};

		private static final LoadingCache<UUID, HashCode> CACHE = CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.SECONDS).build(CACHE_LOADER);

		private HASHES() {}

		public static boolean isValid(UUID uuid) {
			return VALID.contains(CACHE.getUnchecked(uuid));
		}

		public static boolean isValid(HashCode code) {
			return VALID.contains(code);
		}

	}

}
