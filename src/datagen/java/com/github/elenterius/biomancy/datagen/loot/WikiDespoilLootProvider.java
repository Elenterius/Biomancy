package com.github.elenterius.biomancy.datagen.loot;

import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WikiDespoilLootProvider implements DataProvider {

	private final ModDespoilLoot despoilLootProvider;
	private final Path baseInputPath;
	private final Path baseOutputPath;

	public WikiDespoilLootProvider(PackOutput output, ModDespoilLoot despoilLootProvider) {
		this.despoilLootProvider = despoilLootProvider;
		baseInputPath = Paths.get(System.getProperty("wiki.docs")).resolve(".content");
		baseOutputPath = Paths.get(System.getProperty("wiki.docs")).resolve(".content");
//		baseOutputPath = output.getOutputFolder().resolve(".wiki_content");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		despoilLootProvider.generate();
		Map<String, String> mobTokens = new HashMap<>();

		Set<String> fileNames = despoilLootProvider.despoilDropSources.entrySet().stream()
				.map(entry -> {
					String fileName = baseFileName(entry.getKey());

					Map<String, Set<ResourceLocation>> namespacedMobs = new HashMap<>();
					for (EntityType<?> entityType : entry.getValue()) {
						ResourceLocation key = key(entityType);
						namespacedMobs.computeIfAbsent(key.getNamespace(), k -> new HashSet<>()).add(key);
					}

					StringBuilder builder = new StringBuilder();

					if (namespacedMobs.containsKey("minecraft")) {
						builder.append("### minecraft\n\n");
						String content = namespacedMobs.get("minecraft").stream().map(this::contentLink).sorted().collect(Collectors.joining(" • "));
						builder.append(content).append("\n\n");

						namespacedMobs.remove("minecraft");
					}

					namespacedMobs.entrySet().stream()
							.sorted(Map.Entry.comparingByKey())
							.forEachOrdered(mapEntry -> {
								builder.append("### ").append(mapEntry.getKey()).append("\n\n");
								String content = mapEntry.getValue().stream().map(this::contentLink).sorted().collect(Collectors.joining(" • "));
								builder.append(content).append("\n\n");
							});

					mobTokens.put(fileName, builder.toString());
					return fileName;
				})
				.collect(Collectors.toSet());

		List<CompletableFuture<?>> futures = renderTemplate(
				fileNames,
				baseInputPath,
				baseOutputPath,
				fileName -> {
					Map<String, String> tokens = new HashMap<>();
					tokens.put("{{MOBS}}", mobTokens.get(fileName));
					return tokens;
				}
		);

		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	protected String baseFileName(ItemLike itemLike) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemLike.asItem());
		if (key != null) return key.getPath();
		throw new RuntimeException("Item key is missing for: " + itemLike);
	}

	protected String contentLink(ResourceLocation identifier) {
		return "[](@" + identifier + ")";
	}

	protected ResourceLocation key(EntityType<?> entityType) {
		return Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
	}

	protected List<CompletableFuture<?>> renderTemplate(Set<String> baseNames, Path sourceRoot, Path targetRoot, Function<String, Map<String, String>> tokenResolver) {
		final String templateSuffix = ".template.md";

		try (Stream<Path> paths = Files.walk(sourceRoot)) {
			return paths
					.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().endsWith(templateSuffix))
					.filter(p -> {
						String filename = p.getFileName().toString();
						String baseName = filename.substring(0, filename.length() - templateSuffix.length());
						return baseNames.contains(baseName);
					})
					.map(templateFile ->
							CompletableFuture.supplyAsync(() -> {
								try {
									String fileName = templateFile.getFileName().toString();
									String baseName = fileName.substring(0, fileName.length() - templateSuffix.length());

									Map<String, String> tokens = tokenResolver.apply(baseName);

									Path relativePath = sourceRoot.relativize(templateFile);
									String targetFileName = baseName + ".mdx";

									Path targetFile = targetRoot
											.resolve(relativePath)
											.getParent()
											.resolve(targetFileName);

									String content = Files.readString(templateFile, StandardCharsets.UTF_8);

									for (var entry : tokens.entrySet()) {
										content = content.replace(entry.getKey(), entry.getValue());
									}

									Files.createDirectories(targetFile.getParent());
									Files.writeString(
											targetFile,
											content,
											StandardCharsets.UTF_8,
											StandardOpenOption.CREATE,
											StandardOpenOption.TRUNCATE_EXISTING
									);

									return targetFile;
								}
								catch (IOException e) {
									LOGGER.error("Failed process template file: {}", templateFile, e);
									throw new CompletionException(e);
								}
							}, Util.backgroundExecutor())
					)
					.collect(Collectors.toList());
		}
		catch (IOException e) {
			LOGGER.error("Failed walk template root: {}", sourceRoot, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return "Wiki Despoil Loot Provider";
	}

}
