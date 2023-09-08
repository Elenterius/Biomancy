package com.github.elenterius.biomancy.integration.modonomicon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

public sealed interface ModonomiconHelper permits ModonomiconHelper.EmptyModonomiconHelper, ModonomiconHelperImpl {

	ModonomiconHelper EMPTY = new ModonomiconHelper.EmptyModonomiconHelper();

	boolean openBook(ResourceLocation bookId);

	boolean isBookScreenActive(ResourceLocation bookId);

	final class EmptyModonomiconHelper implements ModonomiconHelper {

		@Override
		public boolean openBook(ResourceLocation bookId) {
			return false;
		}

		@Override
		public boolean isBookScreenActive(ResourceLocation bookId) {
			if (FMLEnvironment.dist.isClient()) {
				return Minecraft.getInstance().screen instanceof AdvancementsScreen;
			}
			return false;
		}
	}

}
