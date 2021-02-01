package com.github.elenterius.biomancy.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class UserAuthorization implements INBTSerializable<CompoundNBT> {

	private UUID userUUID;
	private AuthorityType authority;

	public UserAuthorization(CompoundNBT nbt) {
		deserializeNBT(nbt);
	}

	public UserAuthorization(UUID userUUID, AuthorityType type) {
		this.userUUID = userUUID;
		authority = type;
	}

	public UUID getUser() {
		return userUUID;
	}

	public AuthorityType getAuthority() {
		return authority;
	}

	public void setAuthority(AuthorityType type) {
		authority = type;
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
		authority = AuthorityType.deserialize(nbt);
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

	public enum AuthorityType {
		NONE(0), USER(1), ADMIN(2);

		static AuthorityType[] SORTED_LEVELS = new AuthorityType[]{NONE, USER, ADMIN};
		private final byte level;

		AuthorityType(int level) {
			this.level = (byte) level;
		}

		public static AuthorityType fromId(byte level) {
			if (level < 0 || level >= SORTED_LEVELS.length) return NONE;
			return SORTED_LEVELS[level];
		}

		public static AuthorityType deserialize(CompoundNBT nbt) {
			return AuthorityType.fromId(nbt.getByte("AuthorityLevel"));
		}

		public void serialize(CompoundNBT nbt) {
			nbt.putByte("AuthorityLevel", level);
		}

		public byte getLevel() {
			return level;
		}
	}

}
