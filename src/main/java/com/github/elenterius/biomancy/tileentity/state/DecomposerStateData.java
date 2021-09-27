package com.github.elenterius.biomancy.tileentity.state;

import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.tileentity.ChewerTileEntity;
import com.github.elenterius.biomancy.util.BiofuelUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class DecomposerStateData extends RecipeCraftingStateData<DecomposerRecipe> {

	public static final String NBT_KEY_FUEL = "Fuel";
	public static final int FUEL_ID_INDEX = 2;
	public static final int FUEL_AMOUNT_INDEX = 3;

	public final FluidTank fuelTank = new FluidTank(ChewerTileEntity.MAX_FUEL, BiofuelUtil.VALID_FLUID_STACK);
	private final LazyOptional<IFluidHandler> optionalFluidHandler = HandlerBehaviors.standard(fuelTank);

	public LazyOptional<IFluidHandler> getOptionalFuelHandler() {
		return optionalFluidHandler;
	}

	@Override
	Class<DecomposerRecipe> getRecipeType() {
		return DecomposerRecipe.class;
	}

	@Override
	public void serializeNBT(CompoundNBT nbt) {
		super.serializeNBT(nbt);
		if (!fuelTank.isEmpty()) nbt.put(NBT_KEY_FUEL, fuelTank.writeToNBT(new CompoundNBT()));
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		fuelTank.readFromNBT(nbt.getCompound(NBT_KEY_FUEL));
	}

	@Override
	public int get(int index) {
		validateIndex(index);
		if (index == TIME_INDEX) return timeElapsed;
		else if (index == TIME_FOR_COMPLETION_INDEX) return timeForCompletion;
		else if (index == FUEL_ID_INDEX) {
			ForgeRegistry<Fluid> reg = (ForgeRegistry<Fluid>) ForgeRegistries.FLUIDS;
			return reg.getID(fuelTank.getFluid().getFluid());
		}
		else if (index == FUEL_AMOUNT_INDEX) return fuelTank.getFluidAmount();
		else return 0;
	}

	@Override
	public void set(int index, int value) {
		validateIndex(index);
		if (index == TIME_INDEX) timeElapsed = value;
		else if (index == TIME_FOR_COMPLETION_INDEX) timeForCompletion = value;
		else if (index == FUEL_ID_INDEX) {
			ForgeRegistry<Fluid> reg = (ForgeRegistry<Fluid>) ForgeRegistries.FLUIDS;
			Fluid fluid = reg.getValue(value);
			fuelTank.setFluid(new FluidStack(fluid, fuelTank.getFluidAmount()));
		}
		else if (index == FUEL_AMOUNT_INDEX) {
			if (fuelTank.getFluid().getRawFluid() != Fluids.EMPTY) {
				fuelTank.getFluid().setAmount(value);
			}
		}
	}

	@Override
	public int getCount() {
		return 4;
	}
}
