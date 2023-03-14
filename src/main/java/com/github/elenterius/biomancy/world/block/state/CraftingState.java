package com.github.elenterius.biomancy.world.block.state;

import net.minecraft.nbt.CompoundTag;

public enum CraftingState {
	NONE(0), IN_PROGRESS(1), CANCELED(2), COMPLETED(3);

	public static final String NBT_KEY = "CraftingState";
	final byte id;

	CraftingState(int id) {
		this.id = (byte) id;
	}

	public static CraftingState fromId(byte id) {
		return switch (id) {
			case 1 -> IN_PROGRESS;
			case 2 -> CANCELED;
			case 3 -> COMPLETED;
			default -> NONE;
		};
	}

	public static void toNBT(CompoundTag tag, CraftingState state) {
		tag.putByte(NBT_KEY, state.id);
	}

	public static CraftingState fromNBT(CompoundTag tag) {
		return CraftingState.fromId(tag.getByte(NBT_KEY));
	}

	public boolean isCanceled() {
		return this == CANCELED;
	}

}
