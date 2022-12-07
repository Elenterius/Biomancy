package com.github.elenterius.biomancy.integration.compat;

import com.github.elenterius.biomancy.integration.compat.create.CreateCompat;
import com.github.elenterius.biomancy.integration.compat.pehkui.IPehkuiHelper;
import com.github.elenterius.biomancy.integration.compat.pehkui.PehkuiCompat;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class ModsCompatHandler {

	static IPehkuiHelper PEHKUI_HELPER = IPehkuiHelper.EMPTY;

	private ModsCompatHandler() {}

	public static void onBiomancyInit(final IEventBus eventBus) {
		if (ModList.get().isLoaded("pehkui")) {
			PehkuiCompat.init(helper -> PEHKUI_HELPER = helper);
		}
	}

	public static void onBiomancySetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			if (ModList.get().isLoaded("create")) {
				CreateCompat.onPostSetup();
			}
		});
	}

	public static IPehkuiHelper getPehkuiHelper() {
		return PEHKUI_HELPER;
	}

}
