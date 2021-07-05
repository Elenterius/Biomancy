package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class DigesterRecipe extends AbstractBioMechanicalRecipe implements IFluidRecipe {

	private final Ingredient ingredient;
	private final FluidStack result;
	private final Byproduct byproduct;
	private final ItemStack byproductStack;

	public DigesterRecipe(ResourceLocation registryKey, FluidStack resultIn, @Nullable Byproduct byproductIn, int craftingTime, Ingredient ingredientIn) {
		super(registryKey, craftingTime);
		ingredient = ingredientIn;
		result = resultIn;
		byproduct = byproductIn;
		byproductStack = byproductIn != null ? byproductIn.getItemStack() : new ItemStack(ModItems.DIGESTATE.get());
	}

	@Override
	public boolean areRecipesEqual(AbstractBioMechanicalRecipe other, boolean relaxed) {
		if (!(other instanceof IFluidRecipe)) return false;

		boolean flag = getId().equals(other.getId());
		if (!relaxed && !getFluidOutput().isFluidEqual(((IFluidRecipe) other).getFluidOutput()) && !ItemHandlerHelper.canItemStacksStack(getRecipeOutput(), other.getRecipeOutput())) {
			return false;
		}
		return flag;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return ingredient.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return ItemStack.EMPTY.copy();
	}

	@Override
	public FluidStack getFluidResult() {
		return result.copy();
	}

	@Override
	public FluidStack getFluidOutput() {
		return result;
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return byproductStack;
	}

	@Nullable
	public Byproduct getByproduct() {
		return byproduct;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(ingredient);
		return list;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.DIGESTER_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipes.DIGESTER_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DigesterRecipe> {

		private static Ingredient readIngredient(JsonObject jsonObj) {
			if (JSONUtils.isJsonArray(jsonObj, "ingredient")) return Ingredient.deserialize(JSONUtils.getJsonArray(jsonObj, "ingredient"));
			else return Ingredient.deserialize(JSONUtils.getJsonObject(jsonObj, "ingredient"));
		}

		private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

		private static FluidStack readFluid(JsonObject jsonObj) {
			ResourceLocation fluidName = new ResourceLocation(JSONUtils.getString(jsonObj, "fluid"));
			Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
			if (fluid == null) throw new JsonSyntaxException("Fluid cannot be empty");
			FluidStack stack = new FluidStack(fluid, JSONUtils.getInt(jsonObj, "amount", 1));

			if (jsonObj.has("nbt")) {
				try
				{
					JsonElement element = jsonObj.get("nbt");
					CompoundNBT nbt;
					if(element.isJsonObject()) nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
					else nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));
					stack.setTag(nbt);
				}
				catch (CommandSyntaxException exception)
				{
					throw new JsonSyntaxException("Invalid NBT Entry: " + exception);
				}
			}

			return stack;
		}

		@Override
		public DigesterRecipe read(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = readIngredient(json);
			FluidStack resultStack = readFluid(JSONUtils.getJsonObject(json, "result"));
			Byproduct byproduct = json.has("byproduct") ? Byproduct.deserialize(JSONUtils.getJsonObject(json, "byproduct")) : null;
			int time = JSONUtils.getInt(json, "time", 100);
			return new DigesterRecipe(recipeId, resultStack, byproduct, time, ingredient);
		}

		@Nullable
		@Override
		public DigesterRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			FluidStack resultStack = FluidStack.readFromPacket(buffer);
			int time = buffer.readInt();
			Ingredient ingredient = Ingredient.read(buffer);

			boolean hasByproduct = buffer.readBoolean();
			Byproduct byproduct = hasByproduct ? Byproduct.read(buffer) : null;

			return new DigesterRecipe(recipeId, resultStack, byproduct, time, ingredient);
		}

		@Override
		public void write(PacketBuffer buffer, DigesterRecipe recipe) {
			//server side
			recipe.result.writeToPacket(buffer);
			buffer.writeInt(recipe.getCraftingTime());
			recipe.ingredient.write(buffer);

			boolean hasByproduct = recipe.byproduct != null;
			buffer.writeBoolean(hasByproduct);
			if (hasByproduct) recipe.byproduct.write(buffer);
		}
	}
}
