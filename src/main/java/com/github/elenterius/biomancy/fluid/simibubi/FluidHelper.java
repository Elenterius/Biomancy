/*
MIT License

Copyright (c) 2019 simibubi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.github.elenterius.biomancy.fluid.simibubi;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * copied on 2021/07/08 from https://github.com/Creators-of-Create/Create/blob/5a031bb99db2292f9bcb2b715e0c047d0266b28e/src/main/java/com/simibubi/create/foundation/fluid/FluidHelper.java
 * modified version
 */
public class FluidHelper {

	public static JsonElement serializeFluidStack(FluidStack stack) {
		JsonObject json = new JsonObject();
		//noinspection ConstantConditions
		json.addProperty("fluid", stack.getFluid().getRegistryName().toString());
		json.addProperty("amount", stack.getAmount());
		if (stack.hasTag()) json.addProperty("nbt", stack.getTag().toString());
		return json;
	}

	public static FluidStack deserializeFluidStack(JsonObject json) {
		ResourceLocation id = new ResourceLocation(JSONUtils.getString(json, "fluid"));
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
		if (fluid == null) throw new JsonSyntaxException("Unknown fluid '" + id + "'");
		int amount = JSONUtils.getInt(json, "amount");
		FluidStack stack = new FluidStack(fluid, amount);

		if (!json.has("nbt")) return stack;

		try {
			JsonElement element = json.get("nbt");
			stack.setTag(JsonToNBT.getTagFromJson(element.isJsonObject() ? BiomancyMod.GSON.toJson(element) : JSONUtils.getString(element, "nbt")));
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

		return stack;
	}

}