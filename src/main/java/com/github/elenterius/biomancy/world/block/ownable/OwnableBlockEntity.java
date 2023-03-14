package com.github.elenterius.biomancy.world.block.ownable;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.world.block.entity.SimpleSyncedBlockEntity;
import com.github.elenterius.biomancy.world.ownable.IOwnable;
import com.github.elenterius.biomancy.world.permission.IRestrictedInteraction;
import com.github.elenterius.biomancy.world.permission.UserType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OwnableBlockEntity extends SimpleSyncedBlockEntity implements IOwnable, IRestrictedInteraction {

	public static final int MAX_USERS = 10;
	private final HashMap<UUID, UserType> users = new HashMap<>(6);
	@Nullable
	private UUID ownerId;

	public OwnableBlockEntity(BlockPos pos, BlockState state) {
		this(ModBlockEntities.OWNABLE_BE.get(), pos, state);
	}

	protected OwnableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public UserType getUserType(UUID userId) {
		if (isOwner(userId)) return UserType.OWNER;
		return users.getOrDefault(userId, UserType.NONE);
	}

	@Override
	public boolean setUserType(UUID userId, UserType userType) {
		if (users.size() >= MAX_USERS && !users.containsKey(userId)) return false;
		users.put(userId, userType);
		syncToClient();
		return true;
	}

	@Override
	public void removeUser(UUID userId) {
		if (users.containsKey(userId)) {
			users.remove(userId);
			syncToClient();
		}
	}

	@Override
	public Optional<UUID> getOptionalOwnerUUID() {
		return Optional.ofNullable(ownerId);
	}

	@Override
	public void setOwner(UUID userId) {
		ownerId = userId;
		syncToClient();
		setChanged();
	}

	@Override
	public boolean hasOwner() {
		return ownerId != null;
	}

	@Override
	public boolean isOwner(UUID userId) {
		if (ownerId == null) return false;
		return userId.equals(ownerId);
	}

	@Override
	public void removeOwner() {
		ownerId = null;
		syncToClient();
		setChanged();
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		if (ownerId != null) tag.putUUID("OwnerUUID", ownerId);

		if (users.size() > 0) {
			ListTag listNBT = new ListTag();
			for (Map.Entry<UUID, UserType> user : users.entrySet()) {
				CompoundTag userTag = new CompoundTag();
				userTag.putUUID("UserUUID", user.getKey());
				user.getValue().serialize(userTag);
				listNBT.add(userTag);
			}
			tag.put("UserList", listNBT);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		ownerId = tag.hasUUID("OwnerUUID") ? tag.getUUID("OwnerUUID") : null;

		users.clear();
		if (tag.contains("UserList")) {
			ListTag nbtList = tag.getList("UserList", Tag.TAG_COMPOUND);
			for (int i = 0; i < nbtList.size(); i++) {
				CompoundTag userTag = nbtList.getCompound(i);
				UUID userUUID = userTag.getUUID("UserUUID");
				UserType authority = UserType.deserialize(userTag);
				users.put(userUUID, authority);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (ownerId != null) tag.putUUID("OwnerUUID", ownerId);

		if (users.size() > 0) {
			ListTag listNBT = new ListTag();
			for (Map.Entry<UUID, UserType> user : users.entrySet()) {
				CompoundTag userTag = new CompoundTag();
				userTag.putUUID("UserUUID", user.getKey());
				user.getValue().serialize(userTag);
				listNBT.add(userTag);
			}
			tag.put("UserList", listNBT);
		}
	}

}
