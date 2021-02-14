package com.github.elenterius.biomancy.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class UserAuthorization implements INBTSerializable<CompoundNBT> {

	private UUID userUUID;
	private AuthorityLevel authority;

	public UserAuthorization(CompoundNBT nbt) {
		deserializeNBT(nbt);
	}

	public UserAuthorization(UUID userUUID) {
		this(userUUID, AuthorityLevel.USER);
	}

	public UserAuthorization(UUID userUUID, AuthorityLevel type) {
		this.userUUID = userUUID;
		authority = type;
	}

	public UUID getUser() {
		return userUUID;
	}

	public AuthorityLevel getAuthority() {
		return authority;
	}

	public void setAuthorityLevel(AuthorityLevel level) {
		authority = level;
	}

	public int getAuthorityLevel() {
		return authority.getLevel();
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putUniqueId("UserUUID", userUUID);
		authority.serialize(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		userUUID = nbt.getUniqueId("UserUUID");
		authority = AuthorityLevel.deserialize(nbt);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserAuthorization) {
			UserAuthorization other = (UserAuthorization) obj;
			return userUUID.equals(other.userUUID) && authority == other.authority;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + userUUID.toString() + "," + authority + ")";
	}

	public enum AuthorityLevel {
		NONE(0), USER(1), ADMIN(2), OWNER(3);

		static AuthorityLevel[] SORTED_LEVELS = new AuthorityLevel[]{NONE, USER, ADMIN, OWNER};
		private final byte level;

		AuthorityLevel(int level) {
			this.level = (byte) level;
		}

		public static AuthorityLevel fromId(byte level) {
			if (level < 0 || level >= SORTED_LEVELS.length) return NONE;
			return SORTED_LEVELS[level];
		}

		public static AuthorityLevel deserialize(CompoundNBT nbt) {
			return AuthorityLevel.fromId(nbt.getByte("AuthorityLevel"));
		}

		public boolean isUserLevel() {
			return level > NONE.level;
		}

		public boolean isAdminLevel() {
			return level >= ADMIN.level;
		}

		public void serialize(CompoundNBT nbt) {
			nbt.putByte("AuthorityLevel", level);
		}

		public byte getLevel() {
			return level;
		}
	}

}
