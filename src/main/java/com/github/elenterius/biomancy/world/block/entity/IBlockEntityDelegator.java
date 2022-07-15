package com.github.elenterius.biomancy.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface IBlockEntityDelegator {

	BlockPos getDelegatePos();

	@Nullable
	BlockEntity getDelegate();

	void setDelegate(@Nullable BlockEntity blockEntity);

}
