package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.styles.ClientHrTooltipComponent;
import com.github.elenterius.biomancy.styles.HrTooltipComponent;
import com.github.elenterius.biomancy.styles.TabTooltipComponent;
import com.github.elenterius.biomancy.world.inventory.menu.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public final class ModMenuTypes {

	private ModMenuTypes() {}

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BiomancyMod.MOD_ID);

	public static final RegistryObject<MenuType<DecomposerMenu>> DECOMPOSER = MENUS.register("decomposer", () -> IForgeMenuType.create(DecomposerMenu::createClientMenu));
	public static final RegistryObject<MenuType<BioLabMenu>> BIO_LAB = MENUS.register("bio_lab", () -> IForgeMenuType.create(BioLabMenu::createClientMenu));
	public static final RegistryObject<MenuType<GlandMenu>> GLAND = MENUS.register("gland", () -> IForgeMenuType.create(GlandMenu::createClientMenu));
	public static final RegistryObject<MenuType<StorageSacMenu>> STORAGE_SAC = MENUS.register("storage_sac", () -> IForgeMenuType.create(StorageSacMenu::createClientMenu));
	public static final RegistryObject<MenuType<GulgeMenu>> GULGE = MENUS.register("gulge", () -> IForgeMenuType.create(GulgeMenu::createClientMenu));

	public static final RegistryObject<MenuType<FleshkinChestMenu>> FLESHKIN_CHEST = MENUS.register("flesh_chest", () -> IForgeMenuType.create(FleshkinChestMenu::createClientMenu));
	public static final RegistryObject<MenuType<DigesterMenu>> DIGESTER = MENUS.register("digester", () -> IForgeMenuType.create(DigesterMenu::createClientMenu));
	public static final RegistryObject<MenuType<BioForgeMenu>> BIO_FORGE = MENUS.register("bio_forge", () -> IForgeMenuType.create(BioForgeMenu::createClientMenu));

	@OnlyIn(Dist.CLIENT)
	static final Set<Class<? extends Screen>> SCREENS = new HashSet<>();

	@OnlyIn(Dist.CLIENT)
	public static boolean isBiomancyScreen(@Nullable Screen screen) {
		return screen != null && SCREENS.stream().anyMatch(screenClass -> screenClass.isInstance(screen));
	}

	@OnlyIn(Dist.CLIENT)
	private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(RegistryObject<MenuType<M>> registryObject, MenuScreens.ScreenConstructor<M, U> factory, Class<U> clazz) {
		MenuScreens.register(registryObject.get(), factory);
		SCREENS.add(clazz);
	}

	@OnlyIn(Dist.CLIENT)
	static void registerMenuScreens() {
		registerMenuScreen(DECOMPOSER, DecomposerScreen::new, DecomposerScreen.class);
		registerMenuScreen(BIO_LAB, BioLabScreen::new, BioLabScreen.class);
		registerMenuScreen(GLAND, GlandScreen::new, GlandScreen.class);
		registerMenuScreen(STORAGE_SAC, StorageSacScreen::new, StorageSacScreen.class);
		registerMenuScreen(GULGE, GulgeScreen::new, GulgeScreen.class);
		registerMenuScreen(FLESHKIN_CHEST, FleshkinChestScreen::new, FleshkinChestScreen.class);
		registerMenuScreen(DIGESTER, DigesterScreen::new, DigesterScreen.class);
		registerMenuScreen(BIO_FORGE, BioForgeScreen::new, BioForgeScreen.class);
	}

	@OnlyIn(Dist.CLIENT)
	static void registerTooltipComponents() {
		MinecraftForgeClient.registerTooltipComponentFactory(TabTooltipComponent.class, TabTooltipClientComponent::new);
		MinecraftForgeClient.registerTooltipComponentFactory(HrTooltipComponent.class, ClientHrTooltipComponent::new);
	}

}
