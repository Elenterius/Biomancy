package com.github.elenterius.biomancy.datagen.modonomicon;

import com.github.elenterius.biomancy.datagen.translations.ITranslationProvider;
import com.klikli_dev.modonomicon.api.ModonomiconConstants;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBookProvider implements DataProvider {
	protected static final Logger LOGGER = LogUtils.getLogger();

	protected final DataGenerator generator;
	protected final ITranslationProvider lang;
	protected final Map<ResourceLocation, BookModel> bookModels;
	protected final String modId;

	protected AbstractBookProvider(DataGenerator generator, String modId, ITranslationProvider lang) {
		this.modId = modId;
		this.generator = generator;
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
	public void run(CachedOutput cache) throws IOException {
		Path folder = generator.getOutputFolder();

		generate();

		for (BookModel bookModel : bookModels.values()) {
			Path bookPath = getPath(folder, bookModel);
			try {
				DataProvider.saveStable(cache, bookModel.toJson(), bookPath);
			}
			catch (IOException exception) {
				LOGGER.error("Couldn't save book {}", bookPath, exception);
			}

			for (BookCategoryModel bookCategoryModel : bookModel.getCategories()) {
				Path bookCategoryPath = getPath(folder, bookCategoryModel);
				try {
					DataProvider.saveStable(cache, bookCategoryModel.toJson(), bookCategoryPath);
				}
				catch (IOException exception) {
					LOGGER.error("Couldn't save book category {}", bookCategoryPath, exception);
				}

				for (BookEntryModel bookEntryModel : bookCategoryModel.getEntries()) {
					Path bookEntryPath = getPath(folder, bookEntryModel);
					try {
						DataProvider.saveStable(cache, bookEntryModel.toJson(), bookEntryPath);
					}
					catch (IOException exception) {
						LOGGER.error("Couldn't save book entry {}", bookEntryPath, exception);
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Books: " + modId;
	}

}
