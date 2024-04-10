package com.github.elenterius.biomancy.util.fuel;

import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.inventory.Notify;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class FuelHandler implements IFuelHandler, INBTSerializable<CompoundTag> {
	private final int maxFuel;
	private final IntUnaryOperator fuelCostModifierFunc;
	private final Predicate<ItemStack> fuelPredicate;
	private final ToIntFunction<ItemStack> fuelValueFunc;
	private final Notify changeNotifier;
	private int fuel;

	public FuelHandler(int maxFuel, IntUnaryOperator fuelCostModifierFunc, Predicate<ItemStack> fuelPredicate, ToIntFunction<ItemStack> fuelValueFunc, Notify changeNotifier) {
		this.maxFuel = maxFuel;
		this.fuelCostModifierFunc = fuelCostModifierFunc;
		this.fuelPredicate = fuelPredicate;
		this.fuelValueFunc = fuelValueFunc;
		this.changeNotifier = changeNotifier;
	}

	public static FuelHandler createNutrientFuelHandler(int maxFuel, Notify changeNotifier) {
		return new FuelHandler(maxFuel, IntUnaryOperator.identity(), Nutrients::isValidFuel, Nutrients::getFuelValue, changeNotifier);
	}

	@Override
	public int getFuelAmount() {
		return fuel;
	}

	@Override
	public void setFuelAmount(int amount) {
		boolean flag = amount != fuel;
		fuel = amount;
		if (flag) setChanged();
	}

	@Override
	public int getMaxFuelAmount() {
		return maxFuel;
	}

	@Override
	public void addFuelAmount(int amount) {
		setFuelAmount(Mth.clamp(fuel + amount, 0, maxFuel));
	}

	@Override
	public boolean isValidFuel(ItemStack stack) {
		return fuelPredicate.test(stack);
	}

	@Override
	public int getFuelCost(int craftingCostNutrients) {
		return fuelCostModifierFunc.applyAsInt(craftingCostNutrients);
	}

	@Override
	public int getFuelValue(ItemStack stack) {
		return fuelValueFunc.applyAsInt(stack);
	}

	public void setChanged() {
		changeNotifier.invoke();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Amount", fuel);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		fuel = tag.getInt("Amount");
	}
}
