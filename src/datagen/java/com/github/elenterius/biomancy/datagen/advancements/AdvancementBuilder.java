package com.github.elenterius.biomancy.datagen.advancements;

import com.github.elenterius.biomancy.datagen.lang.LangProvider;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public class AdvancementBuilder {

	private final Advancement.Builder internalBuilder;
	private final LangProvider lang;

	private final String modId;
	private final String id;
	private final String titleTranslationKey;
	private final String descriptionTranslationKey;
	private String title = null;
	private String description = null;

	private ItemStack icon = ItemStack.EMPTY;
	private ResourceLocation background = null;
	private FrameType frameType = FrameType.TASK;
	private boolean showToast = false;
	private boolean announceToChat = false;
	private boolean hidden = false;
	private boolean empty = false;

	private AdvancementBuilder(String modId, final String id, LangProvider lang) {
		this.modId = modId;
		this.id = id;
		this.lang = lang;
		internalBuilder = Advancement.Builder.advancement();
		titleTranslationKey = "advancements.%s.%s.title".formatted(modId, id);
		descriptionTranslationKey = "advancements.%s.%s.description".formatted(modId, id);
	}

	public static AdvancementBuilder create(String modId, String id, LangProvider lang) {
		return new AdvancementBuilder(modId, id, lang);
	}

	public AdvancementBuilder parent(Advancement advancement) {
		internalBuilder.parent(advancement);
		return this;
	}

	public AdvancementBuilder title(String text) {
		title = text;
		return this;
	}

	public AdvancementBuilder description(String text) {
		description = text;
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
		hidden = true;
		return this;
	}

	public AdvancementBuilder empty() {
		empty = true;
		hidden();
		impossible();
		return this;
	}

	public AdvancementBuilder impossible() {
		return addCriterion("impossible", new ImpossibleTrigger.TriggerInstance());
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
		ResourceLocation registryName = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.asItem()));
		internalBuilder.addCriterion("has_" + registryName.getPath(), ModAdvancementProvider.hasItems(item));
		return this;
	}

	public AdvancementBuilder requirements(RequirementsStrategy strategy) {
		internalBuilder.requirements(strategy);
		return this;
	}

	public AdvancementBuilder rewardsDefaultRecipe(ItemLike result) {
		internalBuilder.rewards(AdvancementRewards.Builder.recipe(RecipeBuilder.getDefaultRecipeId(result)));
		return this;
	}

	public AdvancementBuilder rewardsExperience(int amount) {
		internalBuilder.rewards(AdvancementRewards.Builder.experience(amount));
		return this;
	}

	public AdvancementBuilder rewards(AdvancementRewards.Builder rewardsBuilder) {
		internalBuilder.rewards(rewardsBuilder);
		return this;
	}

	public AdvancementBuilder rewards(AdvancementRewards rewards) {
		internalBuilder.rewards(rewards);
		return this;
	}

	public Advancement save(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) throws IllegalStateException {
		return save(consumer, fileHelper, modId);
	}

	public Advancement save(Consumer<Advancement> consumer, ExistingFileHelper fileHelper, String category) throws IllegalStateException {
		if (empty) {
			internalBuilder.display(icon, ComponentUtil.empty(), ComponentUtil.empty(), background, frameType, showToast, announceToChat, hidden);
			return internalBuilder.save(consumer, createRL(category + "/" + id), fileHelper);
		}

		if (title == null || title.isBlank()) throw new IllegalStateException("Missing title for advancement " + createRL(category + "/" + id));
		if (description == null || description.isBlank()) throw new IllegalStateException("Missing description for advancement " + createRL(category + "/" + id));

		lang.add(titleTranslationKey, title);
		lang.add(descriptionTranslationKey, description);
		internalBuilder.display(icon, ComponentUtil.translatable(titleTranslationKey), ComponentUtil.translatable(descriptionTranslationKey), background, frameType, showToast, announceToChat, hidden);
		return internalBuilder.save(consumer, createRL(category + "/" + id), fileHelper);
	}

	private ResourceLocation createRL(String path) {
		return new ResourceLocation(modId, path);
	}

}
