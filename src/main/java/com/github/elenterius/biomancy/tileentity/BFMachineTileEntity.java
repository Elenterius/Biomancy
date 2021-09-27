package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.recipe.AbstractProductionRecipe;
import com.github.elenterius.biomancy.tileentity.state.RecipeCraftingStateData;
import com.github.elenterius.biomancy.util.BiofuelUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * machine that uses solid & fluid biofuel as fuel and stores it inside a fluid tank
 */
public abstract class BFMachineTileEntity<R extends AbstractProductionRecipe, S extends RecipeCraftingStateData<R>> extends MachineTileEntity<R, S> {

	public BFMachineTileEntity(TileEntityType<?> entityType) {
		super(entityType);
	}

	protected abstract FluidTank getFuelTank();

	@Override
	public int getFuelAmount() {
		return getFuelTank().getFluidAmount();
	}

	@Override
	public void setFuelAmount(int newAmount) {
		if (getFuelTank().isEmpty()) {
			getFuelTank().setFluid(new FluidStack(ModFluids.NUTRIENT_SLURRY.get(), newAmount));
		}
		else getFuelTank().getFluid().setAmount(newAmount);
	}

	@Override
	public void addFuelAmount(int addAmount) {
		if (getFuelTank().isEmpty()) {
			if (addAmount > 0) setFuelAmount(addAmount);
		}
		else getFuelTank().getFluid().grow(addAmount);
	}

	@Override
	public boolean isItemValidFuel(ItemStack stack) {
		return BiofuelUtil.isItemValidFuel(stack);
	}

	@Override
	public float getItemFuelValue(ItemStack stackIn) {
		return BiofuelUtil.getItemFuelValue(stackIn);
	}

	protected abstract SimpleInventory getEmptyBucketInventory();

	@Override
	public void refuel() {
		int fluidAmount = getFuelTank().getFluidAmount();
		int maxFluidAmount = getFuelTank().getCapacity();
		if (fluidAmount < maxFluidAmount) {
			ItemStack stack = getStackInFuelSlot();
			if (stack.isEmpty()) return;

			if (BiofuelUtil.VALID_FUEL_ITEMS.test(stack)) {
				ItemStack remainder = addFuel(stack);
				if (remainder.getCount() != stack.getCount()) {
					setStackInFuelSlot(remainder);
					setChanged();
				}
			}
			else if (BiofuelUtil.VALID_FUEL_CONTAINERS.test(stack)) {
				FluidActionResult fluidAction = FluidUtil.tryEmptyContainerAndStow(stack, getFuelTank(), getEmptyBucketInventory().getItemHandler(), maxFluidAmount - fluidAmount, null, true);
				if (fluidAction.isSuccess()) {
					setStackInFuelSlot(fluidAction.getResult());
					setChanged();
				}
			}
			else { //the slot doesn't contain a valid fuel item
				//we try to put it into the output slot for empty fluid containers
				ItemStack remainder = getEmptyBucketInventory().insertItemStack(0, stack);
				setStackInFuelSlot(remainder);
				setChanged();
			}
		}
	}

	/**
	 * handles only solid fuel
	 */
	@Override
	public ItemStack addFuel(ItemStack stackIn) {
		return super.addFuel(stackIn);
	}

}
