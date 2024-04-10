package com.github.elenterius.biomancy;

import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;

@Mod(BiomancyMod.MOD_ID)
public final class BiomancyMod {

	public static final String MOD_ID = "biomancy";
	public static final Logger LOGGER = LogManager.getLogger("Biomancy");
	public static final Random GLOBAL_RANDOM = new Random();

	public static boolean WE_DO_A_LITTLE_FOOLING;

	public BiomancyMod() {
		GeckoLib.initialize();

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext modLoadingContext = ModLoadingContext.get();

		ModBannerPatterns.BANNERS.register(modEventBus);

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		CREATIVE_TABS.register(modEventBus);
		ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

		ModFluids.FLUID_TYPES.register(modEventBus);
		ModFluids.FLUIDS.register(modEventBus);

		ModEntityTypes.ENTITIES.register(modEventBus);
		ModAttributes.ATTRIBUTES.register(modEventBus);
		ModEnchantments.ENCHANTMENTS.register(modEventBus);
		ModMobEffects.EFFECTS.register(modEventBus);
		ModSerums.SERUMS.register(modEventBus);
		ModPotions.POTIONS.register(modEventBus);

		ModMenuTypes.MENUS.register(modEventBus);

		ModRecipes.RECIPE_TYPES.register(modEventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
		ModBioForgeTabs.BIO_FORGE_TABS.register(modEventBus);

		ModLoot.GLOBAL_MODIFIERS.register(modEventBus);

		ModSoundEvents.SOUND_EVENTS.register(modEventBus);
		ModParticleTypes.PARTICLE_TYPES.register(modEventBus);

		BiomancyConfig.register(modLoadingContext);
		ModsCompatHandler.onBiomancyInit(modEventBus);


		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		WE_DO_A_LITTLE_FOOLING = calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 1;
	}

	public static ResourceLocation createRL(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static String createRLString(String path) {
		return MOD_ID + ":" + path;
	}

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BiomancyMod.MOD_ID);
	public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("example", () -> CreativeModeTab.builder()
			.title(ComponentUtil.translatable("item_group." + MOD_ID))
			.icon(() -> new ItemStack(ModItems.LIVING_FLESH.get()))
			.displayItems((params, output) -> {
				Set<RegistryObject<? extends Item>> hiddenItems = Set.of(
						ModItems.GIFT_SAC,
						ModItems.ESSENCE,
						ModItems.GUIDE_BOOK,
						ModItems.TOXICUS,
						ModItems.BILE_SPITTER,
						ModItems.DEV_ARM_CANNON
				);
				ModItems.ITEMS.getEntries().stream()
						.filter(entry -> !hiddenItems.contains(entry))
						.forEach(entry -> {
							output.accept(entry.get());

							if (entry.equals(ModItems.RAVENOUS_CLAWS)) {
								output.accept(ModItems.RAVENOUS_CLAWS.get().createItemStackForCreativeTab());
							}

							if (entry.equals(ModItems.FLESHKIN_CHEST)) {
								output.accept(ModBlocks.FLESHKIN_CHEST.get().createItemStackForCreativeTab());
							}
						});

				for (RegistryObject<Enchantment> entry : ModEnchantments.ENCHANTMENTS.getEntries()) {
					Enchantment enchantment = entry.get();
					output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
				}
			})
			.build()
	);

}
