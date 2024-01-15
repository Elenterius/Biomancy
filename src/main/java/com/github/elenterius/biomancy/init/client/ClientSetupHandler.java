package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
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
import com.github.elenterius.biomancy.client.render.entity.AcidProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.WitherProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.acidblob.AcidBlobProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.bloomberry.BloomberryProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.fleshblob.FleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.fleshblob.LegacyFleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.fleshblob.PrimordialFleshBlobRenderer;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.gui.OverlayRegistry;
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
		registerKeyMappings();

		ModScreens.registerMenuScreens();
		registerGameOverlays();
		registerTooltipComponents();

		setBlockRenderLayers();

		event.enqueueWork(ClientSetupHandler::onPostSetup);

		ModsCompatHandler.onBiomancyClientSetup(event);
	}

	private static void onPostSetup() {
		ModRecipeBookCategories.registerRecipeBooks();
	}

	static void registerKeyMappings() {
		ClientRegistry.registerKeyBinding(ITEM_DEFAULT_KEY_BINDING);
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
		event.registerEntityRenderer(ModEntityTypes.BLOOMBERRY_PROJECTILE.get(), BloomberryProjectileRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.ACID_BLOB_PROJECTILE.get(), AcidBlobProjectileRenderer::new);
	}

	private static void setBlockRenderLayers() {
		//ItemBlockRenderTypes.setRenderLayer(ModBlocks.VOICE_BOX.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.STORAGE_SAC.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIAL_HOLDER.get(), RenderType.cutout());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_IRIS_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_FENCE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FULL_FLESH_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_LADDER.get(), RenderType.cutout());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.MALIGNANT_FLESH_VEINS.get(), RenderType.cutout());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.PRIMAL_BLOOM.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.YELLOW_BIO_LANTERN.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLUE_BIO_LANTERN.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.PRIMORDIAL_BIO_LANTERN.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.TENDON_CHAIN.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.FLESH_SPIKE.get(), RenderType.cutout());

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.IMPERMEABLE_MEMBRANE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BABY_PERMEABLE_MEMBRANE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.ADULT_PERMEABLE_MEMBRANE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE.get(), RenderType.translucent());

		ItemBlockRenderTypes.setRenderLayer(ModFluids.ACID.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_ACID.get(), RenderType.translucent());
	}

	@SubscribeEvent
	public static void registerParticles(final ParticleFactoryRegisterEvent event) {
		ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
		particleEngine.register(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), AttackSweepParticle.Provider::new);
		particleEngine.register(ModParticleTypes.FALLING_BLOOD.get(), BloodDripParticle.FallingBloodFactory::new);
		particleEngine.register(ModParticleTypes.LANDING_BLOOD.get(), BloodDripParticle.LandingBloodFactory::new);
		particleEngine.register(ModParticleTypes.CORROSIVE_SWIPE_ATTACK.get(), AttackSweepParticle.Provider::new);
		particleEngine.register(ModParticleTypes.DRIPPING_ACID.get(), ParticleProviders.AcidHangProvider::new);
		particleEngine.register(ModParticleTypes.FALLING_ACID.get(), ParticleProviders.AcidFallProvider::new);
		particleEngine.register(ModParticleTypes.LANDING_ACID.get(), ParticleProviders.AcidLandProvider::new);
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(WitherProjectileRenderer.MODEL_LAYER, WitherSkullRenderer::createSkullLayer);
	}

	@SubscribeEvent
	public static void onItemColorRegistry(final ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, tintIndex) -> ModItems.ESSENCE.get().getColor(stack, tintIndex), ModItems.ESSENCE.get());
		event.getItemColors().register((stack, tintIndex) -> tintIndex == 1 ? 0xFF_09DF5B : 0xFF_FFFFFF, ModBlocks.BABY_PERMEABLE_MEMBRANE.get());
		event.getItemColors().register((stack, tintIndex) -> tintIndex == 1 ? 0xFF_ACBF60 : 0xFF_FFFFFF, ModBlocks.ADULT_PERMEABLE_MEMBRANE.get());
		event.getItemColors().register((stack, tintIndex) -> tintIndex == 1 ? 0xFF_F740FD : 0xFF_FFFFFF, ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get());
		event.getItemColors().register((stack, index) -> index == 1 ? ((BucketItem) stack.getItem()).getFluid().getAttributes().getColor() : 0xFF_FFFFFF, ModItems.ACID_BUCKET.get());
	}

	@SubscribeEvent
	public static void onBlockColorRegistry(final ColorHandlerEvent.Block event) {
		event.getBlockColors().register(VialHolderBlock::getTintColor, ModBlocks.VIAL_HOLDER.get());
		event.getBlockColors().register((state, level, pos, tintIndex) -> tintIndex == 1 ? 0xFF_09DF5B : 0xFF_FFFFFF, ModBlocks.BABY_PERMEABLE_MEMBRANE.get());
		event.getBlockColors().register((state, level, pos, tintIndex) -> tintIndex == 1 ? 0xFF_ACBF60 : 0xFF_FFFFFF, ModBlocks.ADULT_PERMEABLE_MEMBRANE.get());
		event.getBlockColors().register((state, level, pos, tintIndex) -> tintIndex == 1 ? 0xFF_F740FD : 0xFF_FFFFFF, ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get());
	}

	static void registerGameOverlays() {
		OverlayRegistry.registerOverlayTop("biomancy_injector", IngameOverlays.INJECTOR_OVERLAY);
		OverlayRegistry.registerOverlayTop("biomancy_charge_bar", IngameOverlays.CHARGE_BAR_OVERLAY);
		OverlayRegistry.registerOverlayTop("biomancy_attack_reach", IngameOverlays.ATTACK_REACH_OVERLAY);
	}

	static void registerTooltipComponents() {
		MinecraftForgeClient.registerTooltipComponentFactory(HrTooltipComponent.class, HrTooltipClientComponent::new);
		MinecraftForgeClient.registerTooltipComponentFactory(EmptyLineTooltipComponent.class, EmptyLineClientComponent::new);
		MinecraftForgeClient.registerTooltipComponentFactory(StorageSacTooltipComponent.class, StorageSacTooltipClientComponent::new);
	}

}
