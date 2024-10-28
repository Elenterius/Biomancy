package com.github.elenterius.biomancy.api.tribute.fluid;

import com.github.elenterius.biomancy.api.tribute.MilliTribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Experimental
public final class FluidTributes {

	private static final Map<ResourceLocation, FluidToTributeConversion> FLUIDS = new HashMap<>();

	private FluidTributes() {}

	@Nullable
	private static ResourceLocation getRegistryKey(Fluid fluid) {
		if (fluid instanceof FlowingFluid flowingFluid) {
			fluid = flowingFluid.getSource();
		}
		return ForgeRegistries.FLUIDS.getKey(fluid);
	}

	public static void register(Fluid fluid, FluidToTributeConversion func) {
		ResourceLocation key = getRegistryKey(fluid);
		if (key == null) throw new IllegalArgumentException("Couldn't find a valid registry key for fluid " + fluid);
		FLUIDS.put(key, func);
	}

	public static void register(RegistryObject<Fluid> fluidHolder, FluidToTributeConversion func) {
		ResourceLocation key = fluidHolder.getId();
		FLUIDS.put(key, func);
	}

	public static void register(ResourceLocation fluidRegistryKey, FluidToTributeConversion func) {
		FLUIDS.put(fluidRegistryKey, func);
	}

	public static boolean isValid(FluidStack fluidStack) {
		ResourceLocation key = getRegistryKey(fluidStack.getFluid());
		return key != null && FLUIDS.containsKey(key);
	}

	public static @Nullable FluidToTributeConversion getConversion(FluidStack fluidStack) {
		ResourceLocation key = getRegistryKey(fluidStack.getFluid());
		if (key == null) return null;
		return FLUIDS.get(key);
	}

}
