package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.IngameOverlays;
import com.github.elenterius.biomancy.client.model.entity.FleshkinModel;
import com.github.elenterius.biomancy.client.renderer.block.*;
import com.github.elenterius.biomancy.client.renderer.entity.BoomlingRenderer;
import com.github.elenterius.biomancy.client.renderer.entity.FleshBlobRenderer;
import com.github.elenterius.biomancy.client.renderer.entity.FleshkinRenderer;
import com.github.elenterius.biomancy.client.renderer.entity.WitherProjectileRenderer;
import com.github.elenterius.biomancy.world.item.weapon.LongClawItem;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
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

		event.enqueueWork(() -> {
			ModMenuTypes.registerMenuScreens();
			registerItemModelProperties();
			ModBlocks.setRenderLayers();
		});
	}

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntities.CREATOR.get(), CreatorBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.DECOMPOSER.get(), DecomposerBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BIO_FORGE.get(), BioForgeBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BIO_LAB.get(), BioLabBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.TONGUE.get(), TongueBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FLESH_CHEST.get(), FleshChestBlockEntityRenderer::new);

		event.registerEntityRenderer(ModEntityTypes.FLESH_BLOB.get(), FleshBlobRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.FLESHKIN.get(), FleshkinRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.BOOMLING.get(), BoomlingRenderer::new);

//		event.registerEntityRenderer(ModEntityTypes.OCULUS_OBSERVER.get(), OculusObserverRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.BROOD_MOTHER.get(), BroodmotherRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.FAILED_SHEEP.get(), FailedSheepRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.CHROMA_SHEEP.get(), ChromaSheepRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.SILKY_WOOL_SHEEP.get(), SilkyWoolSheepRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.THICK_WOOL_SHEEP.get(), ThickWoolSheepRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.NUTRIENT_SLURRY_COW.get(), NutrientSlurryCowRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.FAILED_COW.get(), FailedCowRenderer::new);

		event.registerEntityRenderer(ModEntityTypes.TOOTH_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), WitherProjectileRenderer::new);
//		event.registerEntityRenderer(ModEntityTypes.BOOMLING_PROJECTILE.get(), BoomlingProjectileRenderer::new);
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
		ItemProperties.register(ModItems.LONG_CLAW.get(), new ResourceLocation("extended"), (stack, level, entity, seed) -> LongClawItem.isClawExtended(stack) ? 1f : 0f);
//		ItemProperties.register(ModItems.SINGLE_ITEM_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.SINGLE_ITEM_BAG_ITEM.get().getFullness(stack));
//		ItemProperties.register(ModItems.SMALL_ENTITY_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.SMALL_ENTITY_BAG_ITEM.get().getFullness(stack));
//		ItemProperties.register(ModItems.LARGE_ENTITY_BAG_ITEM.get(), new ResourceLocation("fullness"), (stack, clientWorld, livingEntity) -> ModItems.LARGE_ENTITY_BAG_ITEM.get().getFullness(stack));
	}

	@SubscribeEvent
	public static void registerLayers(final EntityRenderersEvent.AddLayers event) {}

	@SubscribeEvent
	public static void onBlockModelRegistry(final ModelRegistryEvent event) {}

	public static boolean isPlayerCosmeticVisible(Player player) {
		return HASHES.isValid(player.getGameProfile().getId());
	}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, index) -> index == 0 ? ModItems.SERUM.get().getSerumColor(stack) : -1, ModItems.SERUM.get());
		event.getItemColors().register((stack, index) -> ModItems.ESSENCE.get().getColor(stack, index), ModItems.ESSENCE.get());
		event.getItemColors().register((stack, index) -> index == 1 ? ModItems.BIO_INJECTOR.get().getSerumColor(stack) : -1, ModItems.BIO_INJECTOR.get());
		event.getItemColors().register((stack, index) -> index == 1 ? ModItems.BOOMLING.get().getPotionColor(stack) : -1, ModItems.BOOMLING.get());

		event.getItemColors().register((stack, index) -> 0x8d758c, ModItems.NECROTIC_FLESH_LUMP.get(), ModItems.NECROTIC_FLESH_BLOCK.get());
		event.getItemColors().register((stack, index) -> 0xedaeaa, ModItems.PROTEIN_BAR.get());
	}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Block event) {
		event.getBlockColors().register((state, displayReader, pos, index) -> 0x8d758c, ModBlocks.NECROTIC_FLESH_BLOCK.get());
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
