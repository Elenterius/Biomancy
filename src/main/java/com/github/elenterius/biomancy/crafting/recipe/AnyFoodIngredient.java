package com.github.elenterius.biomancy.crafting.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class AnyFoodIngredient extends AbstractIngredient {

	private static final Predicate<FoodProperties> NUTRITION_PREDICATE = foodProperties -> foodProperties != null && foodProperties.getNutrition() > 0;

	private @Nullable ItemStack[] stacks = null;

	public AnyFoodIngredient() {}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack[] getItems() {
		resolve();
		return stacks;
	}

	private void resolve() {
		if (stacks == null) {
			stacks = ForgeRegistries.ITEMS.getValues().stream()
					.filter(Item::isEdible)
					.map(ItemStack::new)
					.filter(stack -> NUTRITION_PREDICATE.test(stack.getFoodProperties(null)))
					.toArray(ItemStack[]::new);
		}
	}

	@Override
	protected void invalidate() {
		stacks = null;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null) return false;
		if (stack.isEmpty()) return false;
		if (!stack.isEdible()) return false;

		return NUTRITION_PREDICATE.test(stack.getFoodProperties(null));
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", Objects.requireNonNull(CraftingHelper.getID(Serializer.INSTANCE)).toString());
		return json;
	}

	public static class Serializer implements IIngredientSerializer<AnyFoodIngredient> {

		public static final Serializer INSTANCE = new Serializer();

		@Override
		public AnyFoodIngredient parse(FriendlyByteBuf buffer) {
			return new AnyFoodIngredient();
		}

		@Override
		public AnyFoodIngredient parse(JsonObject json) {
			return new AnyFoodIngredient();
		}

		@Override
		public void write(FriendlyByteBuf buffer, AnyFoodIngredient ingredient) {}
	}

}
