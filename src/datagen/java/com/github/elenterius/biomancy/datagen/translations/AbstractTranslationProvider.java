package com.github.elenterius.biomancy.datagen.translations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Note: The translations are maintained in the order they were added.
 */
public abstract class AbstractTranslationProvider implements DataProvider, ITranslationProvider {
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final Map<String, String> translations = new LinkedHashMap<>();
	private final DataGenerator dataGenerator;
	private final String modId;
	private final String languageLocale;

	protected AbstractTranslationProvider(DataGenerator dataGenerator, String modId, String languageLocale) {
		this.dataGenerator = dataGenerator;
		this.modId = modId;
		this.languageLocale = languageLocale;
	}

	protected abstract void addTranslations();

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void run(HashCache cache) throws IOException {
		//move pre-added translations (modnomicon, etc.) to the end of the translation file
		LinkedHashMap<String, String> preAdded = null;
		if (!translations.isEmpty()) {
			preAdded = new LinkedHashMap<>(translations);
			translations.clear();
		}

		addTranslations(); //adds the main translations

		//append pre-added translations
		if (preAdded != null && !preAdded.isEmpty()) {
			translations.putAll(preAdded);
		}

		if (!translations.isEmpty()) {
			JsonObject json = new JsonObject();
			translations.forEach(json::addProperty);

			Path path = dataGenerator.getOutputFolder().resolve("assets/%s/lang/%s.json".formatted(modId, languageLocale));

			String data = GSON.toJson(json);
			data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data);
			String hash = DataProvider.SHA1.hashUnencodedChars(data).toString();
			if (!Objects.equals(cache.getHash(path), hash) || !Files.exists(path)) {
				Files.createDirectories(path.getParent());
				try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
					bufferedwriter.write(data);
				}
			}

			cache.putNew(path, hash);
		}
	}

	@Override
	public String getName() {
		return "Translation Language: " + languageLocale;
	}

	public void add(String key, String translation) {
		if (translation.contains(key)) throw new IllegalStateException("Duplicate translation key " + key);

		translations.put(key, translation);
	}

	public void addBlock(Supplier<? extends Block> supplier, String name) {
		add(supplier.get(), name);
	}

	public void add(Block block, String name) {
		add(block.getDescriptionId(), name);
	}

	public void addItem(Supplier<? extends Item> supplier, String name) {
		add(supplier.get(), name);
	}

	public void add(Item item, String name) {
		add(item.getDescriptionId(), name);
	}

	public void addItemStack(Supplier<ItemStack> supplier, String name) {
		add(supplier.get(), name);
	}

	public void add(ItemStack stack, String name) {
		add(stack.getDescriptionId(), name);
	}

	public void addEnchantment(Supplier<? extends Enchantment> supplier, String name) {
		add(supplier.get(), name);
	}

	public void add(Enchantment enchantment, String name) {
		add(enchantment.getDescriptionId(), name);
	}

	public void addEffect(Supplier<? extends MobEffect> supplier, String name) {
		add(supplier.get(), name);
	}

	public void add(MobEffect effect, String name) {
		add(effect.getDescriptionId(), name);
	}

	public void addEntityType(Supplier<? extends EntityType<?>> supplier, String name) {
		add(supplier.get(), name);
	}

	public void add(EntityType<?> entityType, String name) {
		add(entityType.getDescriptionId(), name);
	}

	public void add(Component component, String translation) {
		if (!(component instanceof TranslatableComponent translatableComponent)) {
			throw new IllegalArgumentException("Provided component does not contain translatable contents");
		}

		add(translatableComponent.getKey(), translation);
	}

	public void addDeathMessage(DamageSource damageSource, String text) {
		add("death.attack." + damageSource.msgId, text);
	}

	public void addSound(Supplier<SoundEvent> supplier, String text) {
		ResourceLocation rl = supplier.get().getLocation();
		add("sounds.%s.%s".formatted(rl.getNamespace(), rl.getPath()), text);
	}

}
