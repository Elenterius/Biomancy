package com.github.elenterius.biomancy.datagen.advancements;

import com.github.elenterius.biomancy.datagen.translations.EnglishTranslationProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;
import java.util.function.Consumer;

public class AdvancementBuilder {

	private final Advancement.Builder internalBuilder;
	private final String modId;
	private final String id;
	private final EnglishTranslationProvider.AdvancementTranslations.AdvancementTranslation translation;
	private ItemStack icon = ItemStack.EMPTY;
	private ResourceLocation background = null;
	private FrameType frameType = FrameType.TASK;
	private boolean showToast = false;
	private boolean announceToChat = false;
	private boolean hidden = false;

	private AdvancementBuilder(String modId, final String id) {
		this.modId = modId;
		this.id = id;
		internalBuilder = Advancement.Builder.advancement();
		translation = EnglishTranslationProvider.AdvancementTranslations.TRANSLATIONS.stream().filter(t -> id.equals(t.id())).findFirst().orElseThrow(() -> new IllegalStateException("translation is missing for advancement '%s'".formatted(id)));
	}

	public static AdvancementBuilder create(String modId, String id) {
		return new AdvancementBuilder(modId, id);
	}

	public AdvancementBuilder parent(Advancement advancement) {
		internalBuilder.parent(advancement);
		return this;
	}

	public AdvancementBuilder icon(ItemLike item) {
		icon = new ItemStack(item);
		return this;
	}

	public AdvancementBuilder icon(ItemStack stack) {
		icon = stack;
		return this;
	}

	public AdvancementBuilder background(ResourceLocation texture) {
		this.background = texture;
		return this;
	}

	public AdvancementBuilder background(String texture) {
		return background(createRL(texture));
	}

	public AdvancementBuilder frameType(FrameType type) {
		frameType = type;
		return this;
	}

	public AdvancementBuilder showToast() {
		showToast = true;
		return this;
	}

	public AdvancementBuilder announceToChat() {
		announceToChat = true;
		return this;
	}

	public AdvancementBuilder hidden() {
		this.hidden = true;
		return this;
	}

	public AdvancementBuilder addCriterion(String key, CriterionTriggerInstance triggerInstance) {
		internalBuilder.addCriterion(key, triggerInstance);
		return this;
	}

	public AdvancementBuilder addHasCriterion(TagKey<Item> tag) {
		ResourceLocation registryName = tag.location();
		internalBuilder.addCriterion("has_" + registryName.getPath(), ModAdvancementProvider.hasTag(tag));
		return this;
	}

	public AdvancementBuilder addHasCriterion(ItemLike item) {
		ResourceLocation registryName = Objects.requireNonNull(item.asItem().getRegistryName());
		internalBuilder.addCriterion("has_" + registryName.getPath(), ModAdvancementProvider.hasItems(item));
		return this;
	}

	public Advancement save(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
		return save(consumer, fileHelper, modId);
	}

	public Advancement save(Consumer<Advancement> consumer, ExistingFileHelper fileHelper, String category) {
		internalBuilder.display(icon, translation.getTitle(), translation.getDescription(), background, frameType, showToast, announceToChat, hidden);
		return internalBuilder.save(consumer, createRL(category + "/" + id), fileHelper);
	}

	private ResourceLocation createRL(String path) {
		return new ResourceLocation(modId, path);
	}

}
