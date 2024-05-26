package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public final class CradleCleansingRecipeMaker {

	private CradleCleansingRecipeMaker() {}

	public static List<CraftingRecipe> createRecipes() {
		ItemStack cradle = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();
		CompoundTag tag = new CompoundTag();
		CompoundTag tagProcGen = new CompoundTag();
		MoundShape.ProcGenValues procGenValues = new MoundShape.ProcGenValues(1234L, (byte) 0, (byte) 0, (byte) 1, 250, 66, 0.7f, 0.5f);
		procGenValues.writeTo(tagProcGen);
		tag.put(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY, tagProcGen);
		BlockItem.setBlockEntityData(cradle, ModBlockEntities.PRIMORDIAL_CRADLE.get(), tag);

		List<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(Ingredient.of(cradle));
		ingredients.add(Ingredient.of(ModItems.CLEANSING_SERUM.get()));
		NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));

		ResourceLocation recipeId = ModItems.PRIMORDIAL_CRADLE.getId().withSuffix("_cleansing" + ".cleansed");

		ItemStack result = ModItems.PRIMORDIAL_CRADLE.get().getDefaultInstance();

		return List.of(
				new ShapelessRecipe(recipeId, "", CraftingBookCategory.MISC, result, inputs)
		);
	}

}
