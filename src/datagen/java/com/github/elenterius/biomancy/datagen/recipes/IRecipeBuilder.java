package com.github.elenterius.biomancy.datagen.recipes;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface IRecipeBuilder {

	static String getRecipeFolderName(@Nullable RecipeCategory category, String modId) {
		return category != null ? category.getFolderName() : modId;
	}

	private InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).build());
	}

	private InventoryChangeTrigger.TriggerInstance has(TagKey<Item> tag) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
	}

	private InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... predicates) {
		return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicates);
	}

	private String getItemName(ItemLike itemLike) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	private String getTagName(TagKey<Item> tag) {
		return tag.location().getPath();
	}

	IRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterionTrigger);

	default IRecipeBuilder unlockedBy(String name, ItemPredicate predicate) {
		return unlockedBy(name, inventoryTrigger(predicate));
	}

	default IRecipeBuilder unlockedBy(ItemLike itemLike, CriterionTriggerInstance criterionTrigger) {
		return unlockedBy("has_" + getItemName(itemLike), criterionTrigger);
	}

	default IRecipeBuilder unlockedBy(ItemLike itemLike) {
		return unlockedBy("has_" + getItemName(itemLike), has(itemLike));
	}

	default IRecipeBuilder unlockedBy(RegistryObject<? extends Item> itemHolder) {
		Item item = itemHolder.get();
		return unlockedBy("has_" + getItemName(item), has(item));
	}

	default IRecipeBuilder unlockedBy(TagKey<Item> tag, CriterionTriggerInstance criterionTrigger) {
		return unlockedBy("has_" + getTagName(tag), criterionTrigger);
	}

	default IRecipeBuilder unlockedBy(TagKey<Item> tag) {
		return unlockedBy("has_" + getTagName(tag), has(tag));
	}

	default void save(Consumer<FinishedRecipe> consumer) {
		save(consumer, null);
	}

	void save(Consumer<FinishedRecipe> consumer, @Nullable RecipeCategory category);

}
