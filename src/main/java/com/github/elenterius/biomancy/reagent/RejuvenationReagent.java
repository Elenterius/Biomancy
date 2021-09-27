package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.mixin.ArmorStandEntityAccessor;
import com.github.elenterius.biomancy.mixin.SlimeEntityAccessor;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RejuvenationReagent extends Reagent {

	public RejuvenationReagent(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof IGrowable) {
			// reverse plant growth

			Block block = state.getBlock();
			if (block == Blocks.GRASS) {
				if (!world.isClientSide) {
					world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
				}
				return true;
			}
			else if (!(block instanceof DoublePlantBlock)) {
				BlockState defaultState = block.defaultBlockState();
				if (state != defaultState) {
					if (!world.isClientSide) {
						world.setBlockAndUpdate(pos, defaultState);
					}
					return true;
				}
			}
		}
		else if (state.getBlock() == ModBlocks.NECROTIC_FLESH_BLOCK.get()) {
			if (!world.isClientSide) {
				world.setBlockAndUpdate(pos, ModBlocks.FLESH_BLOCK.get().defaultBlockState());
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof SlimeEntity) // & magma cube
		{
			if (!target.level.isClientSide) {
				int slimeSize = ((SlimeEntity) target).getSize();
				if (slimeSize > 1) {
					((SlimeEntityAccessor) target).biomancy_setSlimeSize(slimeSize - 1, false);
				}
			}
			return true;
		}
		else if (target instanceof FleshBlobEntity) {
			if (!target.level.isClientSide) {
				byte blobSize = ((FleshBlobEntity) target).getBlobSize();
				if (blobSize > 1) {
					((FleshBlobEntity) target).setBlobSize((byte) (blobSize - 1), false);
				}
			}
			return true;
		}
		else if (!target.isBaby()) {
			if (target instanceof MobEntity) { // includes animals, villagers, zombies, etc..
				((MobEntity) target).setBaby(true);
				return target.isBaby(); //validate it was successful
			}
			else if (target instanceof ArmorStandEntity) {
//				EntityDataManager dataManager = target.getDataManager();
//				byte status = dataManager.get(ArmorStandEntity.STATUS);
//				dataManager.set(ArmorStandEntity.STATUS, (byte) (status | 1)); //inverse = (byte)(status & ~1)
				((ArmorStandEntityAccessor) target).biomancy_setSmall(true);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		return false;
	}

}
