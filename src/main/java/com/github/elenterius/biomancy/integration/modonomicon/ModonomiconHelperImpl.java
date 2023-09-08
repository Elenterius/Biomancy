package com.github.elenterius.biomancy.integration.modonomicon;

import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.client.gui.book.BookOverviewScreen;
import com.klikli_dev.modonomicon.client.gui.book.BookScreenWithButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

final class ModonomiconHelperImpl implements ModonomiconHelper {

	@Override
	public boolean openBook(ResourceLocation bookId) {
		BookGuiManager.get().openBook(bookId);
		return true;
	}

	@Override
	public boolean isBookScreenActive(ResourceLocation bookId) {
		if (FMLEnvironment.dist.isClient()) {
			return isBookScreenActiveInternal(bookId);
		}
		return false;
	}

	private boolean isBookScreenActiveInternal(ResourceLocation bookId) {
		Screen screen = Minecraft.getInstance().screen;

		if (screen instanceof BookOverviewScreen bookOverviewScreen) {
			return bookOverviewScreen.getBook().getId().equals(bookId);
		}

		if (screen instanceof BookScreenWithButtons bookScreen) {
			return bookScreen.getBook().getId().equals(bookId);
		}

		return false;
	}

}
