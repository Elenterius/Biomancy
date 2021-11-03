package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.util.UserAuthorization;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class OwnableTileEntityDelegator extends SimpleSyncedTileEntity implements IOwnableTile {

	private BlockPos delegatePos = BlockPos.ZERO;
	private boolean isValid = false;

	public OwnableTileEntityDelegator() {
		super(ModTileEntityTypes.TILE_DELEGATOR.get());
	}

	public BlockPos getDelegatePos() {
		return delegatePos;
	}

	@Nullable
	public TileEntity getDelegate() {
		if (level != null && isValid && !remove) {
			TileEntity tileEntity = level.getBlockEntity(delegatePos);
			if (tileEntity != null && (tileEntity == this || tileEntity.isRemoved())) { //catch self reference
				setDelegate(null);
				return null;
			}
			if (tileEntity == null) {
				isValid = false;
			}
			return tileEntity;
		}
		return null;
	}

	public void setDelegate(@Nullable TileEntity tileEntity) {
		if (tileEntity == this || tileEntity == null) { //prevent self reference
			delegatePos = BlockPos.ZERO;
			isValid = false;
		}
		if (tileEntity != null) {
			delegatePos = tileEntity.getBlockPos();
			isValid = true;
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);

		isValid = false;
		if (nbt.contains("DelegatePos")) {
			delegatePos = BlockPos.of(nbt.getLong("DelegatePos"));
			isValid = true;
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt = super.save(nbt);

		if (isValid) {
			nbt.putLong("DelegatePos", delegatePos.asLong());
		}
		return nbt;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove) {
			TileEntity delegate = getDelegate();
			if (delegate != null && !delegate.isRemoved()) {
				return delegate.getCapability(cap, side);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void setOwner(UUID uuid) {
		TileEntity delegate = getDelegate();
		if (delegate instanceof IOwnableTile) {
			((IOwnableTile) delegate).setOwner(uuid);
		}
	}

	@Override
	public Optional<UUID> getOwner() {
		TileEntity delegate = getDelegate();
		if (delegate instanceof IOwnableTile) {
			return ((IOwnableTile) delegate).getOwner();
		}
		return Optional.empty();
	}

	@Override
	public void removeOwner() {
		TileEntity delegate = getDelegate();
		if (delegate instanceof IOwnableTile) {
			((IOwnableTile) delegate).removeOwner();
		}
	}

	@Override
	public HashMap<UUID, UserAuthorization.AuthorityLevel> getUserAuthorityLevelMap() {
		TileEntity delegate = getDelegate();
		if (delegate instanceof IOwnableTile) {
			return ((IOwnableTile) delegate).getUserAuthorityLevelMap();
		}
		return new HashMap<>(8);
	}

	@Override
	public ITextComponent getDefaultName() {
		TileEntity delegate = getDelegate();
		if (delegate instanceof IOwnableTile) {
			return ((IOwnableTile) delegate).getDefaultName();
		}
		return new StringTextComponent("unknown");
	}
}
