package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ModModelProvider implements DataProvider {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator generator;

	public ModModelProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(BiomancyMod.MOD_ID) + " Model Definitions";
	}

	@Override
	public void run(HashCache cache) throws IOException {
		Map<ResourceLocation, Supplier<JsonElement>> modelSupplier = Maps.newHashMap();
		BiConsumer<ResourceLocation, Supplier<JsonElement>> biConsumer = (id, jsonSupplier) -> {
			Supplier<JsonElement> supplier = modelSupplier.put(id, jsonSupplier);
			if (supplier != null) {
				throw new IllegalStateException("Duplicate model definition for " + id);
			}
		};

		new ModItemModelGenerator(biConsumer).run();
		saveCollection(cache, generator.getOutputFolder(), modelSupplier, ModModelProvider::createModelPath);
	}

	private static Path createModelPath(Path path, ResourceLocation id) {
		return path.resolve("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json");
	}

	private <T> void saveCollection(HashCache cache, Path rootPath, Map<T, ? extends Supplier<JsonElement>> objectToJson, BiFunction<Path, T, Path> objPathResolver) {
		objectToJson.forEach((type, supplier) -> {
			Path path = objPathResolver.apply(rootPath, type);
			try {
				DataProvider.save(GSON, cache, supplier.get(), path);
			} catch (Exception exception) {
				BiomancyMod.LOGGER.error("Couldn't save {}", path, exception);
			}
		});
	}

}
