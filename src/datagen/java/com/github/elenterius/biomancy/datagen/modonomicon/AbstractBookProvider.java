package com.github.elenterius.biomancy.datagen.modonomicon;

import com.github.elenterius.biomancy.datagen.lang.LangProvider;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractBookProvider implements DataProvider {
	protected static final Logger LOGGER = LogUtils.getLogger();

	protected final PackOutput packOutput;
	protected final LangProvider lang;
	protected final Map<ResourceLocation, BookModel> bookModels;
	protected final String modId;

	protected AbstractBookProvider(PackOutput packOutput, String modId, LangProvider lang) {
		this.modId = modId;
		this.packOutput = packOutput;
		this.lang = lang;
		bookModels = new HashMap<>();
	}

	protected abstract void generate() throws IOException;

	protected ResourceLocation modLoc(String name) {
		return new ResourceLocation(modId, name);
	}

	protected BookModel add(BookModel bookModel) {
		if (bookModels.containsKey(bookModel.getId())) throw new IllegalStateException("Duplicate book " + bookModel.getId());
		bookModels.put(bookModel.getId(), bookModel);
		return bookModel;
	}

	private Path getPath(Path path, BookModel bookModel) {
		ResourceLocation id = bookModel.getId();
		return path.resolve("data/" + id.getNamespace() + "/" + ModonomiconConstants.Data.MODONOMICON_DATA_PATH + "/" + id.getPath() + "/book.json");
	}

	private Path getPath(Path path, BookCategoryModel bookCategoryModel) {
		ResourceLocation id = bookCategoryModel.getId();
		return path.resolve("data/" + id.getNamespace() +
				"/" + ModonomiconConstants.Data.MODONOMICON_DATA_PATH + "/" + bookCategoryModel.getBook().getId().getPath() +
				"/categories/" + id.getPath() + ".json");
	}

	private Path getPath(Path path, BookEntryModel bookEntryModel) {
		ResourceLocation id = bookEntryModel.getId();
		return path.resolve("data/" + id.getNamespace() +
				"/" + ModonomiconConstants.Data.MODONOMICON_DATA_PATH + "/" + bookEntryModel.getCategory().getBook().getId().getPath() +
				"/entries/" + id.getPath() + ".json");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		Path folder = packOutput.getOutputFolder();

		try {
			generate();
		}
		catch (IOException e) {
			return CompletableFuture.allOf();
		}

		List<CompletableFuture<?>> completableFutures = new ArrayList<>();

		for (BookModel bookModel : bookModels.values()) {
			Path bookPath = getPath(folder, bookModel);
			completableFutures.add(DataProvider.saveStable(cache, bookModel.toJson(), bookPath));

			for (BookCategoryModel bookCategoryModel : bookModel.getCategories()) {
				Path bookCategoryPath = getPath(folder, bookCategoryModel);
				completableFutures.add(DataProvider.saveStable(cache, bookCategoryModel.toJson(), bookCategoryPath));

				for (BookEntryModel bookEntryModel : bookCategoryModel.getEntries()) {
					Path bookEntryPath = getPath(folder, bookEntryModel);
					completableFutures.add(DataProvider.saveStable(cache, bookEntryModel.toJson(), bookEntryPath));
				}
			}
		}

		return CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return "Books: " + modId;
	}

}
