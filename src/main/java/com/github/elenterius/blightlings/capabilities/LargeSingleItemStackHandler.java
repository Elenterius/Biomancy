package com.github.elenterius.blightlings.capabilities;

import net.minecraft.nbt.CompoundNBT;

public class LargeSingleItemStackHandler extends SingleItemStackHandler {

	private final short maxItemAmount;

	public LargeSingleItemStackHandler() {
		this(Short.MAX_VALUE);
	}

	public LargeSingleItemStackHandler(short maxItemAmount) {
		this.maxItemAmount = maxItemAmount;
	}

	@Override
	public int getSlotLimit(int slot) {
		return maxItemAmount;
	}

	@Override
	public void serializeItemAmount(CompoundNBT nbt) {
		nbt.putShort("ItemAmount", (short) getAmount());
	}

	@Override
	public int deserializeItemAmount(CompoundNBT nbt) {
		return nbt.getShort("ItemAmount");
	}
}
