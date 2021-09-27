package com.github.elenterius.biomancy.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public interface IItemDecayTracker extends INBTSerializable<LongNBT> {
	/**
	 * @return start time in ticks (game time)
	 */
	long getStartTime();

	/**
	 * @param gameTime in ticks
	 */
	void setStartTime(long gameTime);

	default boolean canDecay(ItemStack stack) {
		return getStartTime() > -1;
	}

	default void onUpdate(ItemStack stack, World world, Entity entity, long halfTime, float decayFactor, boolean forceUpdate) {
		if (!canDecay(stack)) return;
		if (world instanceof ServerWorld && stack.getCount() > 0) {
			if (getStartTime() == 0) {
				long currTime = world.getGameTime();
				setStartTime(currTime);
			}
			else if (world.getGameTime() % 20L == 0L || forceUpdate) {
				int oldCount = stack.getCount();
				performDecayStep(stack, (ServerWorld) world, halfTime, decayFactor);
				onItemDecay(stack, (ServerWorld) world, entity, oldCount, stack.getCount());
			}
		}
	}

	default void performDecayStep(ItemStack stack, ServerWorld world, long halfTime, float decayFactor) {
		if (stack.getCount() > 0) {
			final long currTime = world.getGameTime();
			long elapsedTime = currTime - getStartTime();
			if (elapsedTime > 0 && elapsedTime < currTime) {
				float elapsedHalfTimes = (float) elapsedTime / (float) halfTime;
				int count = MathHelper.clamp((int) Math.round(stack.getCount() * Math.pow(1f - decayFactor, elapsedHalfTimes)), 0, stack.getMaxStackSize());
				if (count != stack.getCount()) {
					setStartTime(currTime); //reset time
					stack.setCount(count);
				}
			}
		}
	}

	void onItemDecay(ItemStack stack, ServerWorld world, Entity entity, int oldCount, int newCount);

	@Override
	default LongNBT serializeNBT() {
		return LongNBT.valueOf(getStartTime());
	}

	@Override
	default void deserializeNBT(LongNBT nbt) {
		setStartTime(nbt.getAsLong());
	}
}
