package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.IngameOverlays;
import com.github.elenterius.biomancy.client.gui.tooltip.EmptyLineClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.HrTooltipClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.StorageSacTooltipClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.TabTooltipClientComponent;
import com.github.elenterius.biomancy.client.renderer.block.*;
import com.github.elenterius.biomancy.client.renderer.entity.AcidProjectileRenderer;
import com.github.elenterius.biomancy.client.renderer.entity.FleshBlobRenderer;
import com.github.elenterius.biomancy.client.renderer.entity.WitherProjectileRenderer;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import com.github.elenterius.biomancy.tooltip.TabTooltipComponent;
import com.github.elenterius.biomancy.world.item.SerumItem;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetupHandler {

	public static final KeyMapping ITEM_DEFAULT_KEY_BINDING = new KeyMapping(
			String.format("key.%s.item_default", BiomancyMod.MOD_ID),
			KeyConflictContext.UNIVERSAL,
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_V,
			"key.categories." + BiomancyMod.MOD_ID
	);

	private ClientSetupHandler() {}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		ModScreens.registerMenuScreens();

		setBlockRenderLayers();

		event.enqueueWork(() -> {
			//ModRecipeBookCategories.init();
			registerItemModelProperties();
		});

		ModsCompatHandler.onBiomancyClientSetup(event);
	}

	@SubscribeEvent
	public static void onClientSetup(RegisterKeyMappingsEvent event) {
		event.register(ITEM_DEFAULT_KEY_BINDING);
	}
	
	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntities.PRIMORDIAL_CRADLE.get(), PrimordialCradleBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.DECOMPOSER.get(), DecomposerBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.DIGESTER.get(), DigesterBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BIO_FORGE.get(), BioForgeBlockRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BIO_LAB.get(), BioLabBlockRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.TONGUE.get(), TongueBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FLESHKIN_CHEST.get(), FleshkinChestBlockRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.STORAGE_SAC.get(), StorageSacBERenderer::new);

		event.registerEntityRenderer(ModEntityTypes.FLESH_BLOB.get(), FleshBlobRenderer::new);

		event.registerEntityRenderer(ModEntityTypes.ANTI_GRAVITY_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.CORROSIVE_ACID_PROJECTILE.get(), AcidProjectileRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.TOOTH_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), WitherProjectileRenderer::new);
	}

	@SuppressWarnings("removal")
	static void setBlockRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIGESTER.get(), RenderType.cutout());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.VOICE_BOX.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.STORAGE_SAC.get(), RenderType.translucent());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_IRIS_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_FENCE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_LADDER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.MALIGNANT_FLESH_VEINS.get(), RenderType.cutout());

		//block with "glowing" overlay texture, also needs a overlay model see onModelBakeEvent() in ClientSetupHandler
		//ItemBlockRenderTypes.setRenderLayer(ModBlocks.FOOBAR.get(), renderType -> renderType == RenderType.getCutout() || renderType == RenderType.getTranslucent());
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(WitherProjectileRenderer.MODEL_LAYER, WitherSkullRenderer::createSkullLayer);
	}

	private static void registerItemModelProperties() {
		//ItemProperties.register(ModItems.SINGLE_ITEM_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.SINGLE_ITEM_BAG_ITEM.get().getFullness(stack));
	}

	@SubscribeEvent
	public static void registerLayers(final EntityRenderersEvent.AddLayers event) {
		//placeholder
	}

	@SubscribeEvent
	public static void onBlockModelRegistry(final ModelEvent.RegisterAdditional event) {
		//placeholder
	}

	//	public static boolean isPlayerCosmeticVisible(Player player) {
	//		return HASHES.isValid(player.getGameProfile().getId());
	//	}

	@SubscribeEvent
	public static void onItemColorRegistry(final RegisterColorHandlersEvent.Item event) {
		event.register((stack, index) -> ModItems.ESSENCE.get().getColor(stack, index), ModItems.ESSENCE.get());
		event.register((stack, index) -> index == 0 ? ((SerumItem) stack.getItem()).getSerumColor(stack) : -1, ModItems.ENLARGEMENT_SERUM.get(), ModItems.SHRINKING_SERUM.get());
	}

	@SubscribeEvent
	public static void onBlockColorRegistry(final RegisterColorHandlersEvent.Block event) {
		//placeholder
	}

	@SubscribeEvent
	public static void registerGameOverlays(RegisterGuiOverlaysEvent event) {
		//	event.registerAboveAll("Biomancy ControlStaff", CONTROL_STAFF_OVERLAY);
		event.registerAboveAll("biomancy_gun", IngameOverlays.GUN_OVERLAY);
		event.registerAboveAll("biomancy_injector", IngameOverlays.INJECTOR_OVERLAY);
	}
	
	@SubscribeEvent
	static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(TabTooltipComponent.class, TabTooltipClientComponent::new);
		event.register(HrTooltipComponent.class, HrTooltipClientComponent::new);
		event.register(EmptyLineTooltipComponent.class, EmptyLineClientComponent::new);
		event.register(StorageSacTooltipComponent.class, StorageSacTooltipClientComponent::new);
	}
	
	@SubscribeEvent
	public static void registerRecipeBooks(RegisterRecipeBookCategoriesEvent event) {
		event.registerBookCategories(ModRecipeBookTypes.BIO_FORGE, ModRecipeBookCategories.BIOFORGE_CATEGORIES);
		event.registerAggregateCategory(ModRecipeBookCategories.SEARCH_CATEGORY, ModRecipeBookCategories.BIOFORGE_CATEGORIES);
		event.registerRecipeCategoryFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), (rc) -> ModRecipeBookCategories.MISC_CATEGORY);
		event.registerRecipeCategoryFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), (rc) -> ModRecipeBookCategories.BLOCKS_CATEGORY);
		event.registerRecipeCategoryFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), (rc) -> ModRecipeBookCategories.MACHINES_CATEGORY);
		event.registerRecipeCategoryFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), (rc) -> ModRecipeBookCategories.WEAPONS_CATEGORY);
		
		event.registerRecipeCategoryFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), (rc) -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), (rc) -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), (rc) -> RecipeBookCategories.UNKNOWN);
	}
	
	@SubscribeEvent
	public static void onModelBakeEvent(ModelEvent.RegisterAdditional event) {
		//block with "glowing" overlay texture
		//addFullBrightOverlayBakedModel(ModBlocks.FOOBAR.get(), event);
	}

	//	private static void addFullBrightOverlayBakedModel(Block block, ModelBakeEvent event) {
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
	//	}

	//	private static final class HASHES {
	//
	//		private static final Set<HashCode> VALID = Set.of(
	//				HashCode.fromString("20f0bf6814e62bb7297669efb542f0af6ee0be1a9b87d0702853d8cc5aa15dc4")
	//		);
	//
	//		private static final CacheLoader<UUID, HashCode> CACHE_LOADER = new CacheLoader<>() {
	//			@Override
	//			public HashCode load(UUID key) {
	//				//noinspection UnstableApiUsage
	//				return Hashing.sha256().hashString(key.toString(), StandardCharsets.UTF_8);
	//			}
	//		};
	//
	//		private static final LoadingCache<UUID, HashCode> CACHE = CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.SECONDS).build(CACHE_LOADER);
	//
	//		private HASHES() {}
	//
	//		public static boolean isValid(UUID uuid) {
	//			return VALID.contains(CACHE.getUnchecked(uuid));
	//		}
	//
	//		public static boolean isValid(HashCode code) {
	//			return VALID.contains(code);
	//		}
	//
	//	}

}
