package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class RecipeUtil {

	private RecipeUtil() {}

	@SuppressWarnings("DataFlowIssue")
	public static JsonObject writeItemStack(ItemStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("id", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
		if (stack.getCount() > 1) json.addProperty("count", stack.getCount());
		if (stack.hasTag()) json.addProperty("tag", stack.getTag().toString());
		return json;
	}

	@SuppressWarnings("DataFlowIssue")
	public static ItemStack readItemStack(JsonObject json) {
		String itemName = GsonHelper.getAsString(json, "id");
		Item item = CraftingHelper.getItem(itemName, false);
		if (json.has("tag")) {
			CompoundTag tag = CraftingHelper.getNBT(json.get("tag"));
			CompoundTag tmp = new CompoundTag();

			if (tag.contains("ForgeCaps")) {
				tmp.put("ForgeCaps", tag.get("ForgeCaps"));
				tag.remove("ForgeCaps");
			}

			tmp.put("tag", tag);
			tmp.putString("id", itemName);
			tmp.putInt("Count", GsonHelper.getAsInt(json, "count", 1));

			return ItemStack.of(tmp);
		}
		return new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
	}

	@SuppressWarnings("deprecation")
	public static void writeItem(FriendlyByteBuf buffer, @Nullable Item item) {
		if (item == null || item == Items.AIR) {
			buffer.writeBoolean(false);
		}
		else {
			buffer.writeBoolean(true);
			buffer.writeId(BuiltInRegistries.ITEM, item);
		}
	}

	@SuppressWarnings("deprecation")
	public static @Nullable Item readItem(FriendlyByteBuf buffer) {
		return !buffer.readBoolean() ? null : buffer.readById(BuiltInRegistries.ITEM);
	}

	public static Ingredient readIngredient(JsonObject json, String memberName) {
		return Ingredient.fromJson((GsonHelper.isArrayNode(json, memberName) ? GsonHelper.getAsJsonArray(json, memberName) : GsonHelper.getAsJsonObject(json, memberName)));
	}

	public static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
		NonNullList<Ingredient> list = NonNullList.create();
		for (int i = 0; i < jsonArray.size(); i++) {
			Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
			if (!ingredient.isEmpty()) {
				list.add(ingredient);
			}
		}
		return list;
	}

	public static List<VariableOutput> readVariableProductionOutputs(JsonArray jsonArray) {
		List<VariableOutput> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(VariableOutput.deserialize(jsonArray.get(i).getAsJsonObject()));
		}
		return list;
	}

	public static List<IngredientStack> readIngredientStacks(JsonArray jsonArray) {
		List<IngredientStack> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); ++i) {
			IngredientStack ingredientStack = IngredientStack.fromJson(jsonArray.get(i).getAsJsonObject());
			if (!ingredientStack.ingredient().isEmpty()) {
				list.add(ingredientStack);
			}
		}
		return list;
	}


	public static List<Ingredient> flattenIngredientStacks(List<IngredientStack> ingredients) {
		List<Ingredient> flatIngredients = new ArrayList<>();
		for (IngredientStack ingredientStack : ingredients) {
			Ingredient ingredient = ingredientStack.ingredient();
			for (int i = 0; i < ingredientStack.count(); i++) {
				flatIngredients.add(ingredient); //insert the same ingredient instances
			}
		}
		return flatIngredients;
	}

}
