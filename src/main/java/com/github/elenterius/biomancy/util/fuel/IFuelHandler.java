package com.github.elenterius.biomancy.util.fuel;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public interface IFuelHandler {

	int getFuelAmount();

	int getMaxFuelAmount();

	void setFuelAmount(int amount);

	void addFuelAmount(int amount);

	boolean isValidFuel(ItemStack stack);

	int getFuelValue(ItemStack stack);

	default int getFuelCost(int craftingTicks) {
		return 1;
	}

	default ItemStack addFuel(ItemStack stack) {
		if (stack.isEmpty()) return stack;
		if (!isValidFuel(stack)) return stack;

		int currFuelAmount = getFuelAmount();
		if (currFuelAmount >= getMaxFuelAmount()) return stack;

		int fuelValue = getFuelValue(stack);
		if (fuelValue <= 0) return stack;

		int neededCount = Mth.floor(Math.max(0, getMaxFuelAmount() - currFuelAmount) / (float) fuelValue);
		int consumeCount = Math.min(stack.getCount(), neededCount);
		if (consumeCount > 0) {
			int amount = Mth.clamp(currFuelAmount + fuelValue * consumeCount, 0, getMaxFuelAmount());
			setFuelAmount(amount);
			return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - consumeCount);
		}
		return stack;
	}
}

