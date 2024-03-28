package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlock;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.EssenceItem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public final class BiometricMembraneRecipeMaker {

	private BiometricMembraneRecipeMaker() {}

	public static List<CraftingRecipe> createRecipes() {
		EntityType<Pig> entityType = EntityType.PIG;

		Stream<CraftingRecipe[]> recipePairs = Stream.of(
				createRecipePair(entityType, false, false),
				createRecipePair(entityType, true, false),
				createRecipePair(entityType, false, true),
				createRecipePair(entityType, true, true)
		);

		return recipePairs.flatMap(Stream::of).toList();
	}

	private static CraftingRecipe[] createRecipePair(EntityType<Pig> entityType, boolean isInverted, boolean isUnique) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(Ingredient.of(EssenceItem.fromEntityType(entityType, 1)));
		ingredients.add(Ingredient.of(ModItems.BIOMETRIC_MEMBRANE.get()));

		if (isInverted) {
			ingredients.add(Ingredient.of(Items.REDSTONE_TORCH));
		}

		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));

		UUID entityUUID = isUnique ? UUID.fromString("420faf42-bf42-4b20-af42-c42420e42d42") : null;
		ItemStack membraneStack = BiometricMembraneBlock.createItem(entityType, entityUUID, EssenceItem.getEssenceColors(entityType), isInverted);

		String name = ModItems.BIOMETRIC_MEMBRANE.getId().toLanguageKey();
		String inverted = isInverted ? "inverted." : "";
		String unique = isUnique ? "unique." : "";

		ResourceLocation createRecipeId = BiomancyMod.createRL(name + "." + inverted + unique + entityType.getDescriptionId());
		ResourceLocation resetRecipeId = BiomancyMod.createRL(name + ".reset." + inverted + unique + entityType.getDescriptionId());

		return new CraftingRecipe[]{
				new ShapelessRecipe(createRecipeId, name, CraftingBookCategory.MISC, membraneStack, inputs),
				new ShapelessRecipe(resetRecipeId, name, CraftingBookCategory.MISC, new ItemStack(ModItems.BIOMETRIC_MEMBRANE.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(membraneStack))),
		};
	}

}
