package com.github.elenterius.biomancy.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public final class WorldUtil {

	private WorldUtil() {}

	public static LazyOptional<IItemHandler> getItemHandler(ServerLevel level, BlockPos pos, Direction direction) {
		BlockState state = level.getBlockState(pos);
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity != null) {
				return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
			}
		}
		return LazyOptional.empty();
	}

	public static void dropItemStack(Level level, double x, double y, double z, Direction facing, float force, ItemStack stack) {
		y -= facing.getAxis() == Direction.Axis.Y ? 0.125f : 0.15625f;
		x += facing.getStepX() * 0.125f;
		z += facing.getStepZ() * 0.125f;

		ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
		itemEntity.setDefaultPickUpDelay();
		double dX = facing.getStepX() * force; //level.random.nextGaussian() * 0.0075f
		float dY = facing.getStepY() * force;
		float dZ = facing.getStepZ() * force;
		itemEntity.setDeltaMovement(dX, dY, dZ);
		level.addFreshEntity(itemEntity);
	}

}
