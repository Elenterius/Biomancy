package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;


public final class ModScreens {


	private ModScreens() {}

	static void registerMenuScreens() {
		registerMenuScreen(ModMenuTypes.DECOMPOSER, DecomposerScreen::new);
		registerMenuScreen(ModMenuTypes.BIO_LAB, BioLabScreen::new);
		registerMenuScreen(ModMenuTypes.STORAGE_SAC, StorageSacScreen::new);
		registerMenuScreen(ModMenuTypes.FLESHKIN_CHEST, FleshkinChestScreen::new);
		registerMenuScreen(ModMenuTypes.DIGESTER, DigesterScreen::new);
		registerMenuScreen(ModMenuTypes.BIO_FORGE, BioForgeScreen::new);
	}

	private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(RegistryObject<MenuType<M>> registryObject, MenuScreens.ScreenConstructor<M, U> factory) {
		MenuScreens.register(registryObject.get(), factory);
	}

}
