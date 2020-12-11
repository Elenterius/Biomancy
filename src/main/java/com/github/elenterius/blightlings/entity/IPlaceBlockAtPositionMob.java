package com.github.elenterius.blightlings.entity;

import com.github.elenterius.blightlings.util.BlockPlacementTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;

import javax.annotation.Nullable;

public interface IPlaceBlockAtPositionMob {

    boolean tryToPlaceBlockAtPosition(BlockRayTraceResult rayTraceResult);

    boolean hasPlaceableBlock();

    void setPlacementBlock(ItemStack stack);

    @Nullable
    BlockPos getBlockPlacementPos();

    @Nullable
    BlockPlacementTarget getBlockPlacementTarget();

    void setBlockPlacementTarget(@Nullable BlockPlacementTarget placementTarget);
}
