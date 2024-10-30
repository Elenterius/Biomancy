package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class WorkbenchRecipeBuilder {

	private WorkbenchRecipeBuilder() {}

	public static ShapedBuilder shaped(RecipeCategory category, ItemLike result) {
		return shaped(category, result, 1);
	}

	public static ShapedBuilder shaped(RecipeCategory category, ItemLike result, int count) {
		return new ShapedBuilder(category, result, count);
	}

	public static ShapelessBuilder shapeless(RecipeCategory category, ItemLike result) {
		return shapeless(category, result, 1);
	}

	public static ShapelessBuilder shapeless(RecipeCategory category, ItemLike result, int count) {
		return new ShapelessBuilder(category, result, count);
	}

	public static ShapedBuilder wall(RecipeCategory category, ItemLike result, ItemLike ingredient) {
		return wall(category, result, Ingredient.of(ingredient)).unlockedBy(ingredient);
	}

	public static ShapedBuilder wall(RecipeCategory category, ItemLike result, Ingredient ingredient) {
		return shaped(category, result, 6).define('#', ingredient).pattern("###").pattern("###");
	}

	public static ShapedBuilder slab(RecipeCategory category, ItemLike result, ItemLike ingredient) {
		return slab(category, result, Ingredient.of(ingredient)).unlockedBy(ingredient);
	}

	public static ShapedBuilder slab(RecipeCategory category, ItemLike result, Ingredient ingredient) {
		return shaped(category, result, 6).define('#', ingredient).pattern("###");
	}

	public static ShapedBuilder polished(RecipeCategory category, ItemLike result, ItemLike ingredient) {
		return polished(category, result, Ingredient.of(ingredient)).unlockedBy(ingredient);
	}

	public static ShapedBuilder polished(RecipeCategory category, ItemLike result, Ingredient ingredient) {
		return shaped(category, result, 4).define('S', ingredient).pattern("SS").pattern("SS");
	}

	public static ShapedBuilder stairs(RecipeCategory category, ItemLike result, ItemLike ingredient) {
		return polished(category, result, Ingredient.of(ingredient)).unlockedBy(ingredient);
	}

	public static ShapedBuilder stairs(RecipeCategory category, ItemLike result, Ingredient ingredient) {
		return shaped(category, result, 4).define('#', ingredient).pattern("#  ").pattern("## ").pattern("###");
	}

	private static String getItemName(ItemLike itemLike) {
		ResourceLocation key = ForgeRegistries.ITEMS.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	private static String getTagName(TagKey<Item> tag) {
		return tag.location().getPath();
	}

	public static final class ShapedBuilder implements RecipeBuilder<ShapedBuilder> {

		private final ShapedRecipeBuilder internalBuilder;

		private ShapedBuilder(RecipeCategory category, ItemLike result, int count) {
			internalBuilder = new ShapedRecipeBuilder(category, result, count);
		}

		public ShapedBuilder define(Character pSymbol, TagKey<Item> tag) {
			internalBuilder.define(pSymbol, tag);
			return this;
		}

		public ShapedBuilder define(Character symbol, ItemLike item) {
			internalBuilder.define(symbol, item);
			return this;
		}

		public ShapedBuilder define(Character symbol, Ingredient ingredient) {
			internalBuilder.define(symbol, ingredient);
			return this;
		}

		public ShapedBuilder pattern(String pattern) {
			internalBuilder.pattern(pattern);
			return this;
		}

		public ShapedBuilder group(@Nullable String groupName) {
			internalBuilder.group(groupName);
			return this;
		}

		public ShapedBuilder showNotification(boolean flag) {
			internalBuilder.showNotification(flag);
			return this;
		}

		public ShapedBuilder unlockedBy(String name, CriterionTriggerInstance trigger) {
			internalBuilder.unlockedBy(name, trigger);
			return this;
		}

		@Override
		public void save(Consumer<FinishedRecipe> consumer, @Nullable RecipeCategory category) {
			save(consumer, BiomancyMod.createRL(getItemName(internalBuilder.getResult())));
		}

		public void save(Consumer<FinishedRecipe> consumer, ResourceLocation recipeId) {
			internalBuilder.save(consumer, recipeId.withPrefix("crafting/"));
		}

	}

	public static final class ShapelessBuilder implements RecipeBuilder<ShapelessBuilder> {

		private final ShapelessRecipeBuilder internalBuilder;

		private ShapelessBuilder(RecipeCategory category, ItemLike result, int count) {
			internalBuilder = new ShapelessRecipeBuilder(category, result, count);
		}

		public ShapelessBuilder requires(TagKey<Item> tag) {
			internalBuilder.requires(tag);
			return this;
		}

		public ShapelessBuilder requires(ItemLike item) {
			internalBuilder.requires(item);
			return this;
		}

		public ShapelessBuilder requires(ItemLike item, int quantity) {
			internalBuilder.requires(item, quantity);
			return this;
		}

		public ShapelessBuilder requires(Ingredient ingredient) {
			internalBuilder.requires(ingredient);
			return this;
		}

		public ShapelessBuilder requires(Ingredient ingredient, int quantity) {
			internalBuilder.requires(ingredient, quantity);
			return this;
		}

		public ShapelessBuilder group(@Nullable String groupName) {
			internalBuilder.group(groupName);
			return this;
		}

		@Override
		public ShapelessBuilder unlockedBy(String name, CriterionTriggerInstance trigger) {
			internalBuilder.unlockedBy(name, trigger);
			return this;
		}

		@Override
		public void save(Consumer<FinishedRecipe> consumer, @Nullable RecipeCategory category) {
			save(consumer, BiomancyMod.createRL(getItemName(internalBuilder.getResult())));
		}

		public void save(Consumer<FinishedRecipe> consumer, ResourceLocation recipeId) {
			internalBuilder.save(consumer, recipeId.withPrefix("crafting/"));
		}

	}

}
