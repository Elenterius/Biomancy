package com.github.elenterius.biomancy;

import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

import javax.annotation.Nonnull;
import java.util.Random;

@Mod(BiomancyMod.MOD_ID)
public final class BiomancyMod {

	public static final String MOD_ID = "biomancy";
	public static final Logger LOGGER = LogManager.getLogger("Biomancy");
	public static final Random GLOBAL_RANDOM = new Random();

	public BiomancyMod() {
		GeckoLib.initialize();

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModBannerPatterns.BANNERS.register(modEventBus);

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		ModEntityTypes.ENTITIES.register(modEventBus);
		ModAttributes.ATTRIBUTES.register(modEventBus);

		ModEnchantments.ENCHANTMENTS.register(modEventBus);
		ModMobEffects.EFFECTS.register(modEventBus);
		ModSerums.SERUMS.register(modEventBus);

		ModMenuTypes.MENUS.register(modEventBus);

		ModRecipes.RECIPE_TYPES.register(modEventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
		ModBioForgeTabs.BIO_FORGE_TABS.register(modEventBus);

		ModLoot.GLOBAL_MODIFIERS.register(modEventBus);

		ModSoundEvents.SOUND_EVENTS.register(modEventBus);

		ModsCompatHandler.onBiomancyInit(modEventBus);
	}

	public static ResourceLocation createRL(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static String createRLString(String path) {
		return MOD_ID + ":" + path;
	}

	public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MOD_ID) {

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.LIVING_FLESH.get());
		}

		@Override
		public void fillItemList(@Nonnull NonNullList<ItemStack> items) {
			super.fillItemList(items);
			for (RegistryObject<Enchantment> entry : ModEnchantments.ENCHANTMENTS.getEntries()) {
				Enchantment enchantment = entry.get();
				if (enchantment == ModEnchantments.QUICK_SHOT.get() || enchantment == ModEnchantments.MAX_AMMO.get()) continue; //"hide" unused gun enchants
				items.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
			}
		}

	};

}
