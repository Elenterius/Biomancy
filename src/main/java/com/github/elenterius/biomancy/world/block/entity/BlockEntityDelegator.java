package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public class BlockEntityDelegator extends SimpleSyncedBlockEntity implements IBlockEntityDelegator {

	private BlockPos delegatePos = BlockPos.ZERO;
	private boolean isValid = false;

	public BlockEntityDelegator(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BE_DELEGATOR.get(), pos, state);
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {}

	@Override
	public BlockPos getDelegatePos() {
		return delegatePos;
	}

	@Override
	@Nullable
	public BlockEntity getDelegate() {
		if (level != null && isValid && !remove) {
			BlockEntity blockEntity = level.getBlockEntity(delegatePos);
			if (blockEntity != null && (blockEntity == this || blockEntity.isRemoved())) { //catch self reference
				setDelegate(null);
				return null;
			}
			if (blockEntity == null) {
				isValid = false;
			}
			return blockEntity;
		}
		return null;
	}

	@Override
	public void setDelegate(@Nullable BlockEntity blockEntity) {
		if (blockEntity == this || blockEntity == null) { //prevent self reference
			delegatePos = BlockPos.ZERO;
			isValid = false;
		}
		if (blockEntity != null) {
			delegatePos = blockEntity.getBlockPos();
			isValid = true;
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		isValid = false;
		if (tag.contains("DelegatePos")) {
			delegatePos = BlockPos.of(tag.getLong("DelegatePos"));
			isValid = true;
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (isValid) {
			tag.putLong("DelegatePos", delegatePos.asLong());
		}
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove) {
			BlockEntity delegate = getDelegate();
			if (delegate != null && !delegate.isRemoved()) {
				return delegate.getCapability(cap, side);
			}
		}
		return super.getCapability(cap, side);
	}

}
