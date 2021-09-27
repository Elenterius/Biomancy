package com.github.elenterius.biomancy.reagent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AbsorptionReagent extends Reagent {

	public AbsorptionReagent(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		return addAbsorption(target);
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		return addAbsorption(targetSelf);
	}

	private boolean addAbsorption(LivingEntity target) {
		if (!target.level.isClientSide) {
			target.setAbsorptionAmount(target.getAbsorptionAmount() + 8);
		}
		return true;
	}

}
