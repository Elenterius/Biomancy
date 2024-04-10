package com.github.elenterius.biomancy.api.nutrients;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Experimental
public final class FluidNutrients {

	private static final Map<ResourceLocation, FluidToFuelConversion> FLUIDS = new HashMap<>();

	private FluidNutrients() {}

	public static void registerFuel(FluidType fluidType, FluidToFuelConversion func) {
		ResourceLocation key = ForgeRegistries.FLUID_TYPES.get().getKey(fluidType);
		FLUIDS.put(key, func);
	}

	public static void registerFuel(Fluid fluid, FluidToFuelConversion func) {
		ResourceLocation key = ForgeRegistries.FLUID_TYPES.get().getKey(fluid.getFluidType());
		FLUIDS.put(key, func);
	}

	public static void registerFuel(RegistryObject<FluidType> fluidTypeHolder, FluidToFuelConversion func) {
		ResourceLocation key = fluidTypeHolder.getId();
		FLUIDS.put(key, func);
	}

	public static void registerFuel(ResourceLocation fluidTypeRegistryKey, FluidToFuelConversion func) {
		FLUIDS.put(fluidTypeRegistryKey, func);
	}

	public static boolean isValidFuel(FluidStack fluidStack) {
		ResourceLocation key = ForgeRegistries.FLUID_TYPES.get().getKey(fluidStack.getFluid().getFluidType());
		return FLUIDS.containsKey(key);
	}

	public static @Nullable FluidToFuelConversion getFuelConversion(FluidStack fluidStack) {
		ResourceLocation key = ForgeRegistries.FLUID_TYPES.get().getKey(fluidStack.getFluid().getFluidType());
		return FLUIDS.get(key);
	}

}
