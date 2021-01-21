package com.github.elenterius.blightlings.tileentity.state;

import net.minecraft.nbt.CompoundNBT;

public enum CraftingState {
	NONE(0), IN_PROGRESS(1), CANCELED(2), COMPLETED(3);

	final byte id;

	CraftingState(int id) {
		this.id = (byte) id;
	}

	public boolean isCanceled() {
		return this == CANCELED;
	}

	public static CraftingState fromId(byte id) {
		if (id < 0 || id >= values().length) return NONE;
		switch (id) {
			case 1:
				return IN_PROGRESS;
			case 2:
				return CANCELED;
			case 3:
				return COMPLETED;
			case 0:
			default:
				return NONE;
		}
	}

	public static void serialize(CompoundNBT nbt, CraftingState state) {
		nbt.putByte("CraftingState", state.id);
	}

	public static CraftingState deserialize(CompoundNBT nbt) {
		return CraftingState.fromId(nbt.getByte("CraftingState"));
	}
}
