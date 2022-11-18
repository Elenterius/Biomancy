package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.IngameOverlays;
import com.github.elenterius.biomancy.client.model.entity.FleshkinModel;
import com.github.elenterius.biomancy.client.renderer.block.*;
import com.github.elenterius.biomancy.client.renderer.entity.AcidProjectileRenderer;
import com.github.elenterius.biomancy.client.renderer.entity.FleshBlobRenderer;
import com.github.elenterius.biomancy.client.renderer.entity.WitherProjectileRenderer;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
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
		ClientRegistry.registerKeyBinding(ITEM_DEFAULT_KEY_BINDING);

		IngameOverlays.registerGameOverlays();
		ModScreens.registerMenuScreens();
		ModScreens.registerTooltipComponents();

		setBlockRenderLayers();

		event.enqueueWork(() -> {
			ModRecipeBookCategories.init();
			registerItemModelProperties();
		});
	}

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntities.CREATOR.get(), CreatorBlockEntityRenderer::new);
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
		LayerDefinition humanoidBase = LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 32);
		LayerDefinition humanoidOuterArmor = LayerDefinition.create(HumanoidModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION, 0), 64, 32);
		LayerDefinition humanoidInnerArmor = LayerDefinition.create(HumanoidModel.createMesh(LayerDefinitions.INNER_ARMOR_DEFORMATION, 0), 64, 32);
		event.registerLayerDefinition(FleshkinModel.MODEL_LAYER, () -> humanoidBase);
		event.registerLayerDefinition(FleshkinModel.INNER_ARMOR_LAYER, () -> humanoidInnerArmor);
		event.registerLayerDefinition(FleshkinModel.OUTER_ARMOR_LAYER, () -> humanoidOuterArmor);

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
	public static void onBlockModelRegistry(final ModelRegistryEvent event) {
		//placeholder
	}

	//	public static boolean isPlayerCosmeticVisible(Player player) {
	//		return HASHES.isValid(player.getGameProfile().getId());
	//	}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, index) -> ModItems.ESSENCE.get().getColor(stack, index), ModItems.ESSENCE.get());
	}

	@SubscribeEvent
	public static void onBlockColorRegistry(final ColorHandlerEvent.Block event) {
		//placeholder
	}

	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event) {
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
