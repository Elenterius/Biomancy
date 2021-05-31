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
				if (!world.isRemote) {
					world.setBlockState(pos, Blocks.DIRT.getDefaultState());
				}
				return true;
			}
			else if (!(block instanceof DoublePlantBlock)) {
				BlockState defaultState = block.getDefaultState();
				if (state != defaultState) {
					if (!world.isRemote) {
						world.setBlockState(pos, defaultState);
					}
					return true;
				}
			}
		}
		else if (state.getBlock() == ModBlocks.NECROTIC_FLESH_BLOCK.get()) {
			if (!world.isRemote) {
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK.get().getDefaultState());
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof SlimeEntity) // & magma cube
		{
			if (!target.world.isRemote) {
				int slimeSize = ((SlimeEntity) target).getSlimeSize();
				if (slimeSize > 1) {
					((SlimeEntityAccessor) target).callSetSlimeSize(slimeSize - 1, false);
				}
			}
			return true;
		}
		else if (target instanceof FleshBlobEntity) {
			if (!target.world.isRemote) {
				byte blobSize = ((FleshBlobEntity) target).getBlobSize();
				if (blobSize > 1) {
					((FleshBlobEntity) target).setBlobSize((byte) (blobSize - 1), false);
				}
			}
			return true;
		}
		else if (!target.isChild()) {
			if (target instanceof MobEntity) { // includes animals, villagers, zombies, etc..
				((MobEntity) target).setChild(true);
				return target.isChild(); //validate it was successful
			}
			else if (target instanceof ArmorStandEntity) {
//				EntityDataManager dataManager = target.getDataManager();
//				byte status = dataManager.get(ArmorStandEntity.STATUS);
//				dataManager.set(ArmorStandEntity.STATUS, (byte) (status | 1)); //inverse = (byte)(status & ~1)
				((ArmorStandEntityAccessor) target).callSetSmall(true);
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
