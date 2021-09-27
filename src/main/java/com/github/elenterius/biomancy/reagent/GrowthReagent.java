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
			if (igrowable.isValidBonemealTarget(world, pos, state, world.isClientSide)) {
				if (!world.isClientSide && world instanceof ServerWorld) {
					Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
					if (property.isPresent()) {
						IntegerProperty ageProperty = property.get();
						int age = state.getValue(ageProperty);
						int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
						if (age < maxAge) {
							world.setBlock(pos, state.setValue(ageProperty, maxAge), Constants.BlockFlags.BLOCK_UPDATE);
							world.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
						}
					}
					else {
						igrowable.performBonemeal((ServerWorld) world, world.random, pos, state); //fall back
					}
				}
				return true;
			}
		}
		else if (block == Blocks.DIRT) {
			if (!world.isClientSide) {
				BlockState stateAbove = world.getBlockState(pos.above());
				if (world.getLightEmission(pos.above()) >= 4 && stateAbove.getLightBlock(world, pos.above()) <= 2) {
					world.setBlockAndUpdate(pos, Blocks.GRASS.defaultBlockState());
					world.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
			}
			return true;
		}
		else if (block == Blocks.SUGAR_CANE) {
			if (SugarCaneUtil.canGrow(world, pos, state)) {
				if (!world.isClientSide && world instanceof ServerWorld) {
					SugarCaneUtil.grow((ServerWorld) world, world.random, pos, state);
					world.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
				return true;
			}
		}
		else if (block == Blocks.CACTUS) {
			if (CactusUtil.canGrow(world, pos, state)) {
				if (!world.isClientSide && world instanceof ServerWorld) {
					CactusUtil.grow((ServerWorld) world, world.random, pos, state);
					world.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
				return true;
			}
		}
		else if (block instanceof IPlantable) { //e.g. nether wart
			if (!world.isClientSide) {
				Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
				if (property.isPresent()) {
					IntegerProperty ageProperty = property.get();
					int age = state.getValue(ageProperty);
					int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
					if (age < maxAge) {
						world.setBlock(pos, state.setValue(ageProperty, maxAge), Constants.BlockFlags.BLOCK_UPDATE);
						world.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
					}
				}
				else if (block.isRandomlyTicking(state) && !world.getBlockTicks().willTickThisTick(pos, block)) {
					world.getBlockTicks().scheduleTick(pos, block, 2);
					world.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, pos, 5);
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof SlimeEntity) {
			if (!target.level.isClientSide) {
				int slimeSize = ((SlimeEntity) target).getSize();
				if (slimeSize < 25) {
					((SlimeEntityAccessor) target).biomancy_setSlimeSize(slimeSize + 1, false);
				}
				else {
					target.hurt(DamageSource.explosion(source), target.getHealth()); //"explode" slime
				}
			}
			return true;
		}
		else if (target instanceof FleshBlobEntity) {
			if (!target.level.isClientSide) {
				byte blobSize = ((FleshBlobEntity) target).getBlobSize();
				if (blobSize < 10) {
					((FleshBlobEntity) target).setBlobSize((byte) (blobSize + 1), false);
				}
			}
			return true;
		}
		else if (target.isBaby()) {
			if (target instanceof MobEntity) { //includes animals, zombies, piglins, etc...
				((MobEntity) target).setBaby(false);
				return !target.isBaby();
			}
			else if (target instanceof ArmorStandEntity) {
//				EntityDataManager dataManager = target.getDataManager();
//				byte status = dataManager.get(ArmorStandEntity.STATUS);
//				dataManager.set(ArmorStandEntity.STATUS, (byte) (status & ~1));
				((ArmorStandEntityAccessor) target).biomancy_setSmall(false);
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
