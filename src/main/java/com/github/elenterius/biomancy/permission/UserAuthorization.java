package com.github.elenterius.biomancy.permission;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;
import java.util.UUID;

public class UserAuthorization implements INBTSerializable<CompoundTag> {

	private UUID userUUID;
	private UserType authority;

	public UserAuthorization(CompoundTag nbt) {
		deserializeNBT(nbt);
	}

	public UserAuthorization(UUID userUUID) {
		this(userUUID, UserType.DEFAULT);
	}

	public UserAuthorization(UUID userUUID, UserType type) {
		this.userUUID = userUUID;
		authority = type;
	}

	public UUID getUser() {
		return userUUID;
	}

	public UserType getAuthority() {
		return authority;
	}

	public void setAuthorityLevel(UserType level) {
		authority = level;
	}

	public int getAuthorityLevel() {
		return authority.getAccessLevel();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putUUID("UserUUID", userUUID);
		authority.serialize(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		userUUID = nbt.getUUID("UserUUID");
		authority = UserType.deserialize(nbt);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserAuthorization other) {
			return userUUID.equals(other.userUUID) && authority == other.authority;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userUUID, authority);
	}

	@Override
	public String toString() {
		return "(" + userUUID.toString() + "," + authority + ")";
	}

}
