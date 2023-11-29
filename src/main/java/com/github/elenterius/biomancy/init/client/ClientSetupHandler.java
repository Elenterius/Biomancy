package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlock;
import com.github.elenterius.biomancy.client.gui.IngameOverlays;
import com.github.elenterius.biomancy.client.gui.tooltip.EmptyLineClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.HrTooltipClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.StorageSacTooltipClientComponent;
import com.github.elenterius.biomancy.client.particle.BloodDripParticle;
import com.github.elenterius.biomancy.client.render.block.bioforge.BioForgeRenderer;
import com.github.elenterius.biomancy.client.render.block.biolab.BioLabRenderer;
import com.github.elenterius.biomancy.client.render.block.cradle.PrimordialCradleRenderer;
import com.github.elenterius.biomancy.client.render.block.decomposer.DecomposerRenderer;
import com.github.elenterius.biomancy.client.render.block.digester.DigesterRenderer;
import com.github.elenterius.biomancy.client.render.block.fleshkinchest.FleshkinChestRenderer;
import com.github.elenterius.biomancy.client.render.block.mawhopper.MawHopperRenderer;
import com.github.elenterius.biomancy.client.render.block.storagesac.StorageSacRenderer;
import com.github.elenterius.biomancy.client.render.block.tongue.TongueRenderer;
import com.github.elenterius.biomancy.client.render.entity.AcidProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.WitherProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.fleshblob.FleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.fleshblob.LegacyFleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.fleshblob.PrimordialFleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.sapberry.SapberryProjectileRenderer;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
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
	public static void onSetup(final FMLClientSetupEvent event) {
		ModScreens.registerMenuScreens();

		setBlockRenderLayers();

		event.enqueueWork(ClientSetupHandler::onPostSetup);

		ModsCompatHandler.onBiomancyClientSetup(event);
	}

	private static void onPostSetup() {
		registerItemModelProperties();
	}

	@SubscribeEvent
	public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
		event.register(ITEM_DEFAULT_KEY_BINDING);
	}

	@SubscribeEvent
	public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntities.PRIMORDIAL_CRADLE.get(), PrimordialCradleRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.DECOMPOSER.get(), DecomposerRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.DIGESTER.get(), DigesterRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BIO_FORGE.get(), BioForgeRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.BIO_LAB.get(), BioLabRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.TONGUE.get(), TongueRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FLESHKIN_CHEST.get(), FleshkinChestRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.STORAGE_SAC.get(), StorageSacRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.MAW_HOPPER.get(), MawHopperRenderer::new);

		event.registerEntityRenderer(ModEntityTypes.HUNGRY_FLESH_BLOB.get(), FleshBlobRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.FLESH_BLOB.get(), FleshBlobRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.LEGACY_FLESH_BLOB.get(), LegacyFleshBlobRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get(), PrimordialFleshBlobRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get(), PrimordialFleshBlobRenderer::new);

		event.registerEntityRenderer(ModEntityTypes.CORROSIVE_ACID_PROJECTILE.get(), AcidProjectileRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.TOOTH_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.WITHER_SKULL_PROJECTILE.get(), WitherProjectileRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.SAPBERRY_PROJECTILE.get(), SapberryProjectileRenderer::new);
	}

	@SuppressWarnings("removal")
	private static void setBlockRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIGESTER.get(), RenderType.cutout());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.VOICE_BOX.get(), RenderType.translucent());
//		ItemBlockRenderTypes.setRenderLayer(ModBlocks.STORAGE_SAC.get(), RenderType.translucent());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_IRIS_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_FENCE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FULL_FLESH_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_LADDER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.MALIGNANT_FLESH_VEINS.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.YELLOW_BIO_LANTERN.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLUE_BIO_LANTERN.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.TENDON_CHAIN.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIAL_HOLDER.get(), RenderType.cutout());

		//block with "glowing" overlay texture, also needs a overlay model see onModelBakeEvent() in ClientSetupHandler
		//ItemBlockRenderTypes.setRenderLayer(ModBlocks.FOOBAR.get(), renderType -> renderType == RenderType.getCutout() || renderType == RenderType.getTranslucent());
	}

	@SubscribeEvent
	public static void registerParticles(final RegisterParticleProvidersEvent event) {
		event.register(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), AttackSweepParticle.Provider::new);
		event.register(ModParticleTypes.FALLING_BLOOD.get(), BloodDripParticle.FallingBloodFactory::new);
		event.register(ModParticleTypes.LANDING_BLOOD.get(), BloodDripParticle.LandingBloodFactory::new);
		event.register(ModParticleTypes.CORROSIVE_SWIPE_ATTACK.get(), AttackSweepParticle.Provider::new);
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

	@SubscribeEvent
	public static void onItemColorRegistry(final RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> ModItems.ESSENCE.get().getColor(stack, tintIndex), ModItems.ESSENCE.get());
		event.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF_09DF5B : 0xFF_FFFFFF, ModBlocks.BABY_PERMEABLE_MEMBRANE.get());
		event.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF_ACBF60 : 0xFF_FFFFFF, ModBlocks.ADULT_PERMEABLE_MEMBRANE.get());
		event.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF_F740FD : 0xFF_FFFFFF, ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get());
	}

	@SubscribeEvent
	public static void onBlockColorRegistry(final RegisterColorHandlersEvent.Block event) {
		event.register(VialHolderBlock::getTintColor, ModBlocks.VIAL_HOLDER.get());
		event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 0xFF_09DF5B : 0xFF_FFFFFF, ModBlocks.BABY_PERMEABLE_MEMBRANE.get());
		event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 0xFF_ACBF60 : 0xFF_FFFFFF, ModBlocks.ADULT_PERMEABLE_MEMBRANE.get());
		event.register((state, level, pos, tintIndex) -> tintIndex == 1 ? 0xFF_F740FD : 0xFF_FFFFFF, ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get());
	}

	@SubscribeEvent
	public static void registerGameOverlays(RegisterGuiOverlaysEvent event) {
//		event.registerAboveAll("biomancy_gun", IngameOverlays.GUN_OVERLAY);
		event.registerAboveAll("biomancy_injector", IngameOverlays.INJECTOR_OVERLAY);
		event.registerAboveAll("biomancy_charge_bar", IngameOverlays.CHARGE_BAR_OVERLAY);
		event.registerAboveAll("biomancy_attack_reach", IngameOverlays.ATTACK_REACH_OVERLAY);
	}

	@SubscribeEvent
	static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(HrTooltipComponent.class, HrTooltipClientComponent::new);
		event.register(EmptyLineTooltipComponent.class, EmptyLineClientComponent::new);
		event.register(StorageSacTooltipComponent.class, StorageSacTooltipClientComponent::new);
	}

}
