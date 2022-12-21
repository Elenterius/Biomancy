package com.github.elenterius.biomancy.integration;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.integration.compat.create.CreateCompat;
import com.github.elenterius.biomancy.integration.compat.pehkui.IPehkuiHelper;
import com.github.elenterius.biomancy.integration.compat.pehkui.PehkuiCompat;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModsCompatHandler {

	static final Marker LOG_MARKER = MarkerManager.getMarker(ModsCompatHandler.class.getSimpleName());
	static IPehkuiHelper PEHKUI_HELPER = IPehkuiHelper.EMPTY;

	private ModsCompatHandler() {}

	public static void onBiomancyInit(final IEventBus eventBus) {
		if (ModList.get().isLoaded("pehkui")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "init Pehkui compat...");
			PehkuiCompat.init(helper -> PEHKUI_HELPER = helper);
		}
	}

	public static void onBiomancyCommonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			if (ModList.get().isLoaded("create")) {
				BiomancyMod.LOGGER.info(LOG_MARKER, "setup Create compat...");
				CreateCompat.onPostSetup();
			}
		});
	}

	public static void onBiomancyClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			//			if (ModList.get().isLoaded("jeresources")) {
			//				BiomancyMod.LOGGER.info(LOG_MARKER, "setup JER plugin...");
			//				BiomancyJerPlugin.onClientPostSetup();
			//			}
		});
	}

	public static IPehkuiHelper getPehkuiHelper() {
		return PEHKUI_HELPER;
	}

}
