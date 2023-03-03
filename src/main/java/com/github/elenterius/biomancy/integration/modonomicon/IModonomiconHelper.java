package com.github.elenterius.biomancy.integration.modonomicon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

public sealed interface IModonomiconHelper permits IModonomiconHelper.EmptyModonomiconHelper, ModonomiconHelperImpl {

	IModonomiconHelper EMPTY = new IModonomiconHelper.EmptyModonomiconHelper();

	boolean openBook(ResourceLocation bookId);

	boolean isBookScreenActive(ResourceLocation bookId);

	final class EmptyModonomiconHelper implements IModonomiconHelper {

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
