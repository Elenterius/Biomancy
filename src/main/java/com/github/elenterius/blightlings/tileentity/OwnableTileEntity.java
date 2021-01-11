package com.github.elenterius.blightlings.tileentity;

import com.github.elenterius.blightlings.util.UserAuthorization;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.INameable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public abstract class OwnableTileEntity extends SimpleSyncedTileEntity implements IOwnableTile, INameable {

	private final ArrayList<UserAuthorization> userList = new ArrayList<>();
	private ITextComponent customName = null;
	@Nullable
	private UUID owner;

	public OwnableTileEntity(TileEntityType<?> entityType) {
		super(entityType);
	}

	public boolean canPlayerAccess(PlayerEntity player) {
		if (world == null || world.getTileEntity(pos) != this) return false;
		return player.getDistanceSq(Vector3d.copyCentered(pos)) < 8d * 8d && (player.isCreative() || isPlayerAuthorized(player));
	}

	public boolean isPlayerAuthorized(PlayerEntity player) {
		if (hasOwner()) {
			UUID playerUUID = player.getUniqueID();
			if (!isOwner(playerUUID)) {
				boolean isAuthorized = false;
				for (UserAuthorization userAuthorization : userList) {
					if (userAuthorization.getUser().equals(playerUUID)) {
						isAuthorized = userAuthorization.getAuthorityLevel() > 0;
						break;
					}
				}
				return isAuthorized;
			}
		}
		return true;
	}

	@Override
	public Optional<UUID> getOwner() {
		return Optional.ofNullable(owner);
	}

	@Override
	public void setOwner(UUID uuid) {
		owner = uuid;
	}

	@Override
	public void removeOwner() {
		owner = null;
	}

	@Override
	public boolean hasOwner() {
		return owner != null;
	}

	@Override
	public ITextComponent getDisplayName() {
		return getName();
	}

	@Override
	public ITextComponent getName() {
		return customName != null ? customName : getDefaultName();
	}

	@Nullable
	@Override
	public ITextComponent getCustomName() {
		return customName;
	}

	public void setCustomName(ITextComponent name) {
		customName = name;
	}

	protected abstract ITextComponent getDefaultName();

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if (nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
			customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
		}
		else {
			customName = null;
		}

		if (nbt.hasUniqueId("OwnerUUID")) owner = nbt.getUniqueId("OwnerUUID");
		else owner = null;

		userList.clear();
		if (nbt.contains("UserList")) {
			ListNBT nbtList = nbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < nbtList.size(); i++) {
				userList.add(new UserAuthorization(nbtList.getCompound(i)));
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if (customName != null) nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));

		if (owner != null) nbt.putUniqueId("OwnerUUID", owner);

		if (userList.size() > 0) {
			ListNBT listNBT = new ListNBT();
			for (UserAuthorization userAuthorization : userList) {
				listNBT.add(userAuthorization.serializeNBT());
			}
			nbt.put("UserList", listNBT);
		}
		return nbt;
	}
}
