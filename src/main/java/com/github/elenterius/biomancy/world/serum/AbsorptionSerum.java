package com.github.elenterius.biomancy.world.serum;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class AbsorptionSerum extends Serum {

	public AbsorptionSerum(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundTag tag, @Nullable LivingEntity source, Level level, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return addAbsorption(target);
	}

	@Override
	public boolean affectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return addAbsorption(targetSelf);
	}

	private boolean addAbsorption(LivingEntity target) {
		if (!target.level.isClientSide) {
			target.setAbsorptionAmount(target.getAbsorptionAmount() + 8);
		}
		return true;
	}

}
