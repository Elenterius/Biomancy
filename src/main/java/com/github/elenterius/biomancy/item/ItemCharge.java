package com.github.elenterius.biomancy.item;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public interface ItemCharge {
	String CHARGE_KEY = "Charge";

	default boolean consumeCharge(ItemStack container, int amount) {
		int charge = getCharge(container);
		if (charge < amount) return false;
		setCharge(container, charge - amount);
		return true;
	}

	default boolean addCharge(ItemStack container, int amount) {
		int charge = getCharge(container);
		int maxCharge = getMaxCharge(container);
		if (charge + amount > maxCharge) return false;
		setCharge(container, charge + amount);
		return true;
	}

	int getMaxCharge(ItemStack container);

	void onChargeChanged(ItemStack container, int oldValue, int newValue);

	default int getCharge(ItemStack container) {
		return container.getOrCreateTag().getInt(CHARGE_KEY);
	}

	default boolean hasCharge(ItemStack container) {
		return getCharge(container) > 0;
	}

	default void setCharge(ItemStack container, int amount) {
		int maxCharge = getMaxCharge(container);
		int oldValue = getCharge(container);
		int newValue = Mth.clamp(amount, 0, maxCharge);
		container.getOrCreateTag().putInt(CHARGE_KEY, newValue);
		onChargeChanged(container, oldValue, newValue);
	}

	default float getChargePct(ItemStack container) {
		return getCharge(container) / (float) getMaxCharge(container);
	}

}
