package com.github.elenterius.biomancy.block.ownable;

import com.github.elenterius.biomancy.block.base.SimpleContainerBlockEntity;
import com.github.elenterius.biomancy.ownable.Ownable;
import com.github.elenterius.biomancy.permission.Actions;
import com.github.elenterius.biomancy.permission.IRestrictedInteraction;
import com.github.elenterius.biomancy.permission.UserType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class OwnableContainerBlockEntity extends SimpleContainerBlockEntity implements Ownable, IRestrictedInteraction {

	public static final int MAX_USERS = 10;
	private final HashMap<UUID, UserType> users = new HashMap<>(6);
	@Nullable
	private UUID ownerId;

	protected OwnableContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean canPlayerOpenContainer(Player player) {
		return super.canPlayerOpenContainer(player) && isActionAllowed(player, Actions.USE_BLOCK);
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
		setChanged();
		return true;
	}

	@Override
	public void removeUser(UUID userId) {
		if (users.containsKey(userId)) {
			users.remove(userId);
			syncToClient();
			setChanged();
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

	protected void syncToClient() {
		if (level != null && !level.isClientSide) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveForSyncToClient(tag);
		return tag;
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	protected void saveForSyncToClient(CompoundTag tag) {
		if (ownerId != null) tag.putUUID("OwnerUUID", ownerId);

		if (!users.isEmpty()) {
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

		if (!users.isEmpty()) {
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
