package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.mixin.ArmorStandAccessor;
import com.github.elenterius.biomancy.mixin.SlimeAccessor;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;

public class RejuvenationSerum extends Serum {

	public RejuvenationSerum(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundTag nbt, @Nullable LivingEntity source, Level world, BlockPos pos, Direction facing) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof IPlantable) {
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
	public boolean affectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Slime slime) { // includes MagmaCube
			if (!slime.level.isClientSide) {
				int slimeSize = slime.getSize();
				if (slimeSize > 1) {
					((SlimeAccessor) slime).biomancy_setSlimeSize(slimeSize - 1, false);
				}
			}
			return true;
		}
		else if (target instanceof FleshBlob fleshBlob) {
			if (!fleshBlob.level.isClientSide) {
				byte blobSize = fleshBlob.getBlobSize();
				if (blobSize > 1) {
					fleshBlob.setBlobSize((byte) (blobSize - 1), false);
				}
			}
			return true;
		}
		else if (!target.isBaby()) {
			if (target instanceof Mob mob) { // includes animals, villagers, zombies, etc..
				mob.setBaby(true);
				return mob.isBaby(); //validate it was successful
			}
			else if (target instanceof ArmorStand) {
				((ArmorStandAccessor) target).biomancy_setSmall(true);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean affectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

}
