package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.mixin.ArmorStandEntityAccessor;
import com.github.elenterius.biomancy.mixin.SlimeEntityAccessor;
import com.github.elenterius.biomancy.util.BlockPropertyUtil;
import com.github.elenterius.biomancy.util.CactusUtil;
import com.github.elenterius.biomancy.util.SugarCaneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;

public class GrowthReagent extends Reagent {

	public GrowthReagent(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof IGrowable) // "power" grow plant to maturity
		{
			IGrowable igrowable = (IGrowable) block;
			if (igrowable.canGrow(world, pos, state, world.isRemote)) {
				if (!world.isRemote && world instanceof ServerWorld) {
					Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
					if (property.isPresent()) {
						IntegerProperty ageProperty = property.get();
						int age = state.get(ageProperty);
						int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
						if (age < maxAge) {
							world.setBlockState(pos, state.with(ageProperty, maxAge), Constants.BlockFlags.BLOCK_UPDATE);
							world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
						}
					}
					else {
						igrowable.grow((ServerWorld) world, world.rand, pos, state); //fall back
					}
				}
				return true;
			}
		}
		else if (block == Blocks.DIRT) {
			if (!world.isRemote) {
				BlockState stateAbove = world.getBlockState(pos.up());
				if (world.getLightValue(pos.up()) >= 4 && stateAbove.getOpacity(world, pos.up()) <= 2) {
					world.setBlockState(pos, Blocks.GRASS.getDefaultState());
					world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
			}
			return true;
		}
		else if (block == Blocks.SUGAR_CANE) {
			if (SugarCaneUtil.canGrow(world, pos, state)) {
				if (!world.isRemote && world instanceof ServerWorld) {
					SugarCaneUtil.grow((ServerWorld) world, world.rand, pos, state);
					world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
				return true;
			}
		}
		else if (block == Blocks.CACTUS) {
			if (CactusUtil.canGrow(world, pos, state)) {
				if (!world.isRemote && world instanceof ServerWorld) {
					CactusUtil.grow((ServerWorld) world, world.rand, pos, state);
					world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
				return true;
			}
		}
		else if (block instanceof IPlantable) { //e.g. nether wart
			if (!world.isRemote) {
				Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
				if (property.isPresent()) {
					IntegerProperty ageProperty = property.get();
					int age = state.get(ageProperty);
					int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
					if (age < maxAge) {
						world.setBlockState(pos, state.with(ageProperty, maxAge), Constants.BlockFlags.BLOCK_UPDATE);
						world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
					}
				}
				else if (block.ticksRandomly(state) && !world.getPendingBlockTicks().isTickPending(pos, block)) {
					world.getPendingBlockTicks().scheduleTick(pos, block, 2);
					world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof SlimeEntity) {
			if (!target.world.isRemote) {
				int slimeSize = ((SlimeEntity) target).getSlimeSize();
				if (slimeSize < 25) {
					((SlimeEntityAccessor) target).callSetSlimeSize(slimeSize + 1, false);
				}
				else {
					target.attackEntityFrom(DamageSource.causeExplosionDamage(source), target.getHealth()); //"explode" slime
				}
			}
			return true;
		}
		else if (target instanceof FleshBlobEntity) {
			if (!target.world.isRemote) {
				byte blobSize = ((FleshBlobEntity) target).getBlobSize();
				if (blobSize < 10) {
					((FleshBlobEntity) target).setBlobSize((byte) (blobSize + 1), false);
				}
			}
			return true;
		}
		else if (target.isChild()) {
			if (target instanceof MobEntity) { //includes animals, zombies, piglins, etc...
				((MobEntity) target).setChild(false);
				return !target.isChild();
			}
			else if (target instanceof ArmorStandEntity) {
//				EntityDataManager dataManager = target.getDataManager();
//				byte status = dataManager.get(ArmorStandEntity.STATUS);
//				dataManager.set(ArmorStandEntity.STATUS, (byte) (status & ~1));
				((ArmorStandEntityAccessor) target).callSetSmall(false);
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
