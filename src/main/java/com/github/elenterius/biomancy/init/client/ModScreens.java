package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.client.gui.tooltip.HrTooltipClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.StorageSacTooltipClientComponent;
import com.github.elenterius.biomancy.client.gui.tooltip.TabTooltipClientComponent;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.StorageSacTooltipComponent;
import com.github.elenterius.biomancy.tooltip.TabTooltipComponent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public final class ModScreens {

	public static final Set<Class<? extends Screen>> SCREENS = new HashSet<>();

	private ModScreens() {}


	public static boolean isBiomancyScreen(@Nullable Screen screen) {
		return screen != null && SCREENS.stream().anyMatch(screenClass -> screenClass.isInstance(screen));
	}

	private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(RegistryObject<MenuType<M>> registryObject, MenuScreens.ScreenConstructor<M, U> factory, Class<U> clazz) {
		MenuScreens.register(registryObject.get(), factory);
		SCREENS.add(clazz);
	}

	public static void registerMenuScreens() {
		registerMenuScreen(ModMenuTypes.DECOMPOSER, DecomposerScreen::new, DecomposerScreen.class);
		registerMenuScreen(ModMenuTypes.BIO_LAB, BioLabScreen::new, BioLabScreen.class);
		registerMenuScreen(ModMenuTypes.GLAND, GlandScreen::new, GlandScreen.class);
		registerMenuScreen(ModMenuTypes.STORAGE_SAC, StorageSacScreen::new, StorageSacScreen.class);
		registerMenuScreen(ModMenuTypes.GULGE, GulgeScreen::new, GulgeScreen.class);
		registerMenuScreen(ModMenuTypes.FLESHKIN_CHEST, FleshkinChestScreen::new, FleshkinChestScreen.class);
		registerMenuScreen(ModMenuTypes.DIGESTER, DigesterScreen::new, DigesterScreen.class);
		registerMenuScreen(ModMenuTypes.BIO_FORGE, BioForgeScreen::new, BioForgeScreen.class);
	}

	public static void registerTooltipComponents() {
		MinecraftForgeClient.registerTooltipComponentFactory(TabTooltipComponent.class, TabTooltipClientComponent::new);
		MinecraftForgeClient.registerTooltipComponentFactory(HrTooltipComponent.class, HrTooltipClientComponent::new);
		MinecraftForgeClient.registerTooltipComponentFactory(StorageSacTooltipComponent.class, StorageSacTooltipClientComponent::new);
	}

}
