package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlock;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlock;
import com.github.elenterius.biomancy.client.gui.IngameOverlays;
import com.github.elenterius.biomancy.client.gui.tooltip.EmptyLineClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.HrTooltipClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.StorageSacTooltipClientComponent;
import com.github.elenterius.biomancy.client.particle.BloodDripParticle;
import com.github.elenterius.biomancy.client.particle.CustomGlowParticle;
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
import com.github.elenterius.biomancy.client.render.entity.mob.FleshChickenRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.FleshCowRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.FleshPigRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.FleshSheepRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.fleshblob.FleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.fleshblob.LegacyFleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.fleshblob.PrimordialFleshBlobRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.sheep.ChromaSheepRenderer;
import com.github.elenterius.biomancy.client.render.entity.mob.sheep.ThickFurSheepRenderer;
import com.github.elenterius.biomancy.client.render.entity.projectile.AcidProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.projectile.acidblob.AcidBlobProjectileRenderer;
import com.github.elenterius.biomancy.client.render.entity.projectile.bloomberry.BloomberryProjectileRenderer;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.item.weapon.gun.GunbladeItem;
import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import com.github.elenterius.biomancy.util.TransliterationUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
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

		TransliterationUtil.init();

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
		event.registerEntityRenderer(ModEntityTypes.FLESH_COW.get(), FleshCowRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.FLESH_SHEEP.get(), FleshSheepRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.FLESH_PIG.get(), FleshPigRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.FLESH_CHICKEN.get(), FleshChickenRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.CHROMA_SHEEP.get(), ChromaSheepRenderer::new);
		event.registerEntityRenderer(ModEntityTypes.THICK_FUR_SHEEP.get(), ThickFurSheepRenderer::new);

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
		event.registerSpriteSet(ModParticleTypes.PINK_GLOW.get(), sprites -> new CustomGlowParticle.TwoColorProvider(sprites, 0xf740fd, 0xff6fff));
		event.registerSpriteSet(ModParticleTypes.LIGHT_GREEN_GLOW.get(), sprites -> new CustomGlowParticle.TwoColorProvider(sprites, 0x53ff53, 0x64e986));
		event.registerSpriteSet(ModParticleTypes.HOSTILE.get(), CustomGlowParticle.GenericProvider::new);
		event.registerSpriteSet(ModParticleTypes.BIOHAZARD.get(), sprites -> new CustomGlowParticle.TwoColorProvider(sprites, 0xab274f, 0x7e2a43));
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
	}

	private static void registerItemModelProperties() {
		ItemPropertyFunction shieldPropertyFunc = (stack, level, livingEntity, seed) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == stack ? 1f : 0f;
		ItemProperties.register(ModItems.THORN_SHIELD.get(), new ResourceLocation("blocking"), shieldPropertyFunc);

		ItemProperties.register(ModItems.CAUSTIC_GUNBLADE.get(), new ResourceLocation("melee"), (stack, level, livingEntity, seed) -> GunbladeItem.GunbladeMode.from(stack) == GunbladeItem.GunbladeMode.MELEE ? 1f : 0f);
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
		event.register((state, level, pos, tintIndex) -> tintIndex == 0 ? ModFluids.ACID_TYPE.get().getTintColor() : 0xFF_FFFFFF, ModBlocks.ACID_CAULDRON.get());
	}

	@SubscribeEvent
	public static void registerGameOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "injector", IngameOverlays.INJECTOR_OVERLAY);
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "charge_bar", IngameOverlays.CHARGE_BAR_OVERLAY);
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "gun", IngameOverlays.GUN_OVERLAY);
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "knowledge", IngameOverlays.KNOWLEDGE_OVERLAY);
	}

	@SubscribeEvent
	static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(HrTooltipComponent.class, HrTooltipClientComponent::new);
		event.register(EmptyLineTooltipComponent.class, EmptyLineClientComponent::new);
		event.register(StorageSacTooltipComponent.class, StorageSacTooltipClientComponent::new);
	}

}
