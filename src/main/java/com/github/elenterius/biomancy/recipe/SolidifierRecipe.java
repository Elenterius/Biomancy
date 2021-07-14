package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.fluid.simibubi.FluidIngredient;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class SolidifierRecipe extends AbstractProductionRecipe.FluidInput {

	private final ItemStack result;
	private final FluidIngredient fluidIngredient;

	public SolidifierRecipe(ResourceLocation registryKey, ItemStack resultIn, int craftingTime, FluidIngredient ingredientIn) {
		super(registryKey, craftingTime);
		result = resultIn;
		fluidIngredient = ingredientIn;
	}

	@Override
	public boolean matches(IFluidHandler fluidHandler, World worldIn) {
		return fluidIngredient.test(fluidHandler.getFluidInTank(0));
	}

	@Override
	public ItemStack getFluidCraftingResult() {
		return result.copy();
	}

	@Override
	public FluidIngredient getFluidIngredient() {
		return fluidIngredient;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.SOLIDIFIER_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return ModRecipes.SOLIDIFIER_RECIPE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SolidifierRecipe> {

		@Override
		public SolidifierRecipe read(ResourceLocation recipeId, JsonObject json) {
			FluidIngredient fluidIngredient = FluidIngredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
			ItemStack resultStack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			int time = JSONUtils.getInt(json, "time", 100);
			return new SolidifierRecipe(recipeId, resultStack, time, fluidIngredient);
		}

		@Nullable
		@Override
		public SolidifierRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//client side
			ItemStack resultStack = buffer.readItemStack();
			FluidIngredient fluidIngredient = FluidIngredient.read(buffer);
			int time = buffer.readVarInt();
			return new SolidifierRecipe(recipeId, resultStack, time, fluidIngredient);
		}

		@Override
		public void write(PacketBuffer buffer, SolidifierRecipe recipe) {
			//server side
			buffer.writeItemStack(recipe.result);
			recipe.fluidIngredient.write(buffer);
			buffer.writeVarInt(recipe.getCraftingTime());
		}

	}
}
