package com.github.elenterius.biomancy.integration;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.integration.alexscaves.AlexsCavesCompat;
import com.github.elenterius.biomancy.integration.create.CreateCompat;
import com.github.elenterius.biomancy.integration.modonomicon.ModonomiconHelper;
import com.github.elenterius.biomancy.integration.modonomicon.ModonomiconIntegration;
import com.github.elenterius.biomancy.integration.pehkui.PehkuiHelper;
import com.github.elenterius.biomancy.integration.pehkui.PehkuiIntegration;
import com.github.elenterius.biomancy.integration.tetra.TetraCompat;
import com.github.elenterius.biomancy.integration.tetra.TetraHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;

public final class ModsCompatHandler {

	static final Marker LOG_MARKER = MarkerManager.getMarker(ModsCompatHandler.class.getSimpleName());
	static PehkuiHelper PEHKUI_HELPER = PehkuiHelper.EMPTY;
	static ModonomiconHelper MODONOMICON_HELPER = ModonomiconHelper.EMPTY;
	static TetraHelper TETRA_HELPER = TetraHelper.EMPTY;

	private ModsCompatHandler() {}

	public static void onBiomancyInit(final IEventBus eventBus) {
		if (ModList.get().isLoaded("pehkui")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "Initialize Pehkui compat...");
			PehkuiIntegration.init(helper -> PEHKUI_HELPER = helper);
		}

		if (ModList.get().isLoaded("modonomicon")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "Initialize Modonomicon integration...");
			ModonomiconIntegration.init(helper -> MODONOMICON_HELPER = helper);
		}

		if (ModList.get().isLoaded("tetra")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "Initialize Modonomicon integration...");
			TetraCompat.init(helper -> TETRA_HELPER = helper);
		}
	}

	public static void onBiomancyCommonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			if (ModList.get().isLoaded("create")) {
				String versionString = ModList.get().getModFileById("create").versionString();
				int[] splitVersion = Arrays.stream(versionString.split("\\."))
						.mapToInt(ModsCompatHandler::parseVersionNumber)
						.toArray();

				int major = splitVersion[0];
				int minor = splitVersion[1];
				int patch = splitVersion[2];

				if (major > 0 || (major == 0 && minor > 5) || (major == 0 && minor == 5 && patch >= 1)) {
					BiomancyMod.LOGGER.info(LOG_MARKER, "Setup Create compat...");
					CreateCompat.onPostSetup();
				}
				else {
					BiomancyMod.LOGGER.warn(LOG_MARKER, "Found outdated version of Create (< 0.5.1). Skipping compatibility setup for Create!");
				}
			}

			if (ModList.get().isLoaded("tetra")) {
				BiomancyMod.LOGGER.info(LOG_MARKER, "Setup Tetra compat...");
				TetraCompat.onPostSetup();
			}

			if (ModList.get().isLoaded("alexscaves")) {
				BiomancyMod.LOGGER.info(LOG_MARKER, "Setup Alex's Caves compat...");
				AlexsCavesCompat.onPostSetup();
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

	public static PehkuiHelper getPehkuiHelper() {
		return PEHKUI_HELPER;
	}

	public static ModonomiconHelper getModonomiconHelper() {
		return MODONOMICON_HELPER;
	}

	public static TetraHelper getTetraHelper() {
		return TETRA_HELPER;
	}

}
