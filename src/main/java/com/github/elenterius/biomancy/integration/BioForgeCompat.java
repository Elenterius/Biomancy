package com.github.elenterius.biomancy.integration;

import net.minecraftforge.fml.ModList;

public final class BioForgeCompat {

	private BioForgeCompat() {}

	public static boolean isRecipeCollectionOverwriteEnabled() {
		return isNerbLoaded();
	}

	private static boolean isNerbLoaded() {
		return ModList.get().isLoaded("nerb");
	}

}
