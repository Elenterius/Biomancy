package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.function.Predicate;

public final class BiofuelUtil {

	private BiofuelUtil() {}

	public static short DEFAULT_FUEL_VALUE = 200;
	public static byte NUTRIENT_PASTE_MULTIPLIER = 1;
	public static byte NUTRIENT_BAR_MULTIPLIER = 6;

	public static final Predicate<ItemStack> VALID_FUEL_ITEMS = stack -> stack.getItem() == ModItems.NUTRIENT_PASTE.get() || stack.getItem() == ModItems.NUTRIENT_BAR.get();

	public static final Predicate<ItemStack> VALID_FUEL_CONTAINERS = stack -> FluidUtil.getFluidContained(stack)
			.filter(fluidStack -> fluidStack.getFluid().isEquivalentTo(ModFluids.NUTRIENT_SLURRY.get()))
			.isPresent();

	public static final Predicate<Fluid> VALID_FLUID = fluid -> fluid.isEquivalentTo(ModFluids.NUTRIENT_SLURRY.get());

	public static final Predicate<FluidStack> VALID_FLUID_STACK = fluidStack -> fluidStack.getFluid().isEquivalentTo(ModFluids.NUTRIENT_SLURRY.get());

	public static boolean isItemValidFuel(ItemStack stackIn) {
		return VALID_FUEL_ITEMS.test(stackIn) || VALID_FUEL_CONTAINERS.test(stackIn);
	}

	public static float getItemFuelValue(ItemStack stackIn) {
		Item item = stackIn.getItem();
		if (item == ModItems.NUTRIENT_BAR.get()) return DEFAULT_FUEL_VALUE * NUTRIENT_BAR_MULTIPLIER;
		if (item == ModItems.NUTRIENT_PASTE.get()) return DEFAULT_FUEL_VALUE * NUTRIENT_PASTE_MULTIPLIER;
		return 0;
	}

}
