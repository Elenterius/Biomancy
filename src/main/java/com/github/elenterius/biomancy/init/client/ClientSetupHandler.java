package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlock;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlock;
import com.github.elenterius.biomancy.client.gui.IngameOverlays;
import com.github.elenterius.biomancy.client.gui.tooltip.EmptyLineClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.HrTooltipClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.StorageSacTooltipClientComponent;
import com.github.elenterius.biomancy.client.particle.BloodDripParticle;
import com.github.elenterius.biomancy.client.particle.ParticleProviders;
import com.github.elenterius.biomancy.client.render.block.bioforge.BioForgeRenderer;
import com.github.elenterius.biomancy.client.render.block.biolab.BioLabRenderer;
import com.github.elenterius.biomancy.client.render.block.cradle.PrimordialCradleRenderer;
import com.github.elenterius.biomancy.client.render.block.decomposer.DecomposerRenderer;
import com.github.elenterius.biomancy.client.render.block.digester.DigesterRenderer;
import com.github.elenterius.biomancy.client.render.block.fleshkinchest.FleshkinChestRenderer;
import com.github.elenterius.biomancy.client.render.block.mawhopper.MawHopperRenderer;
import com.github.elenterius.biomancy.client.render.block.storagesac.StorageSacRenderer;
import com.github.elenterius.biomancy.client.render.block.tongue.TongueRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.fleshblob.FleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.fleshblob.LegacyFleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.fleshblob.PrimordialFleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.projectile.AcidProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.projectile.acidblob.AcidBlobProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.projectile.bloomberry.BloomberryProjectileRenderer;
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
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
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
		event.registerEntityRenderer(ModEntityTypes.BLOOMBERRY_PROJECTILE.get(), BloomberryProjectileRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.ACID_BLOB_PROJECTILE.get(), AcidBlobProjectileRenderer::new);
	}

	@SuppressWarnings("removal")
	private static void setBlockRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_IRIS_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FULL_FLESH_DOOR.get(), RenderType.cutout());

		ItemBlockRenderTypes.setRenderLayer(ModFluids.ACID.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_ACID.get(), RenderType.translucent());

		//block with "glowing" overlay texture, also needs a overlay model see onModelBakeEvent() in ClientSetupHandler
		//ItemBlockRenderTypes.setRenderLayer(ModBlocks.FOOBAR.get(), renderType -> renderType == RenderType.getCutout() || renderType == RenderType.getTranslucent());
	}

	@SubscribeEvent
	public static void registerParticles(final RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), AttackSweepParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.FALLING_BLOOD.get(), BloodDripParticle.FallingBloodFactory::new);
		event.registerSpriteSet(ModParticleTypes.LANDING_BLOOD.get(), BloodDripParticle.LandingBloodFactory::new);
		event.registerSpriteSet(ModParticleTypes.CORROSIVE_SWIPE_ATTACK.get(), AttackSweepParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.DRIPPING_ACID.get(), ParticleProviders.AcidHangProvider::new);
		event.registerSpriteSet(ModParticleTypes.FALLING_ACID.get(), ParticleProviders.AcidFallProvider::new);
		event.registerSpriteSet(ModParticleTypes.LANDING_ACID.get(), ParticleProviders.AcidLandProvider::new);
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
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
		event.register((stack, index) -> index == 1 ? IClientFluidTypeExtensions.of(((BucketItem) stack.getItem()).getFluid()).getTintColor() : 0xFF_FFFFFF, ModItems.ACID_BUCKET.get());
		event.register(BiometricMembraneBlock::getTintColor, ModItems.BIOMETRIC_MEMBRANE.get());
	}

	@SubscribeEvent
	public static void onBlockColorRegistry(final RegisterColorHandlersEvent.Block event) {
		event.register(VialHolderBlock::getTintColor, ModBlocks.VIAL_HOLDER.get());
		event.register(BiometricMembraneBlock::getTintColor, ModBlocks.BIOMETRIC_MEMBRANE.get());
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
