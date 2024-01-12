package com.github.elenterius.biomancy.permission;

import net.minecraft.nbt.CompoundTag;

public enum UserType {
	NONE(0), DEFAULT(1), ADMIN(2), OWNER(3);

	static final UserType[] SORTED_LEVELS = new UserType[]{NONE, DEFAULT, ADMIN, OWNER};
	private final byte level;

	UserType(int accessLevel) {
		this.level = (byte) accessLevel;
	}

	public static UserType fromId(byte level) {
		if (level < 0 || level > SORTED_LEVELS.length - 1) return NONE;
		return SORTED_LEVELS[level];
	}

	public static UserType deserialize(CompoundTag nbt) {
		return UserType.fromId(nbt.getByte("AccessLevel"));
	}

	public boolean isUserLevel() {
		return level > NONE.level;
	}

	public boolean isAdminLevel() {
		return level >= ADMIN.level;
	}

	public void serialize(CompoundTag nbt) {
		nbt.putByte("AccessLevel", level);
	}

	public byte getAccessLevel() {
		return level;
	}

}
