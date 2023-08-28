package com.github.elenterius.biomancy.integration;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.integration.compat.pehkui.IPehkuiHelper;
import com.github.elenterius.biomancy.integration.compat.pehkui.PehkuiCompat;
import com.github.elenterius.biomancy.integration.create.CreateCompat;
import com.github.elenterius.biomancy.integration.modonomicon.IModonomiconHelper;
import com.github.elenterius.biomancy.integration.modonomicon.ModonomiconIntegration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;

public final class ModsCompatHandler {

	static final Marker LOG_MARKER = MarkerManager.getMarker(ModsCompatHandler.class.getSimpleName());
	static IPehkuiHelper PEHKUI_HELPER = IPehkuiHelper.EMPTY;
	static IModonomiconHelper MODONOMICON_HELPER = IModonomiconHelper.EMPTY;

	private ModsCompatHandler() {}

	public static void onBiomancyInit(final IEventBus eventBus) {
		if (ModList.get().isLoaded("pehkui")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "Initialize Pehkui compat...");
			PehkuiCompat.init(helper -> PEHKUI_HELPER = helper);
		}

		if (ModList.get().isLoaded("modonomicon")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "Initialize Modonomicon integration...");
			ModonomiconIntegration.init(helper -> MODONOMICON_HELPER = helper);
		}
	}

	public static void onBiomancyCommonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			if (ModList.get().isLoaded("create")) {
				String versionString = ModList.get().getModFileById("create").versionString();
				int[] splitVersion = Arrays.stream(versionString.split("\\."))
						.mapToInt(ModsCompatHandler::parseVersionNumber)
						.toArray();

				//0.5.1.b-30
				if (splitVersion[1] >= 5 && splitVersion[2] >= 1) {
					BiomancyMod.LOGGER.info(LOG_MARKER, "Setup Create compat...");
					CreateCompat.onPostSetup();
				}
				else {
					BiomancyMod.LOGGER.warn(LOG_MARKER, "Found outdated version of Create (< 0.5.1). Skipping compatibility setup for Create!");
				}
			}
		});
	}

	private static int parseVersionNumber(String s) {
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	public static void onBiomancyClientSetup(final FMLClientSetupEvent event) {
		//		event.enqueueWork(() -> {
			//			if (ModList.get().isLoaded("jeresources")) {
			//				BiomancyMod.LOGGER.info(LOG_MARKER, "setup JER plugin...");
			//				BiomancyJerPlugin.onClientPostSetup();
			//			}
		//		});
	}

	public static IPehkuiHelper getPehkuiHelper() {
		return PEHKUI_HELPER;
	}

	public static IModonomiconHelper getModonomiconHelper() {
		return MODONOMICON_HELPER;
	}

}
