package com.github.elenterius.biomancy.reagent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BreedingReagent extends Reagent {

	public BreedingReagent(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (!(target instanceof AnimalEntity)) return false;
		AnimalEntity animal = (AnimalEntity) target;
		int growingAge = animal.getGrowingAge();
		if (growingAge >= 0 && animal.canFallInLove()) {
			if (!animal.world.isRemote) {
				if (growingAge > 0) animal.setGrowingAge(0); //growing age has to be 0 for animals to keep staying in love, else the in love state gets reset
				animal.setInLove(source instanceof PlayerEntity ? (PlayerEntity) source : null);
				animal.setInLove(animal.func_234178_eO_() * 2); //twice as much time to find a breading mate
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		return false;
	}

}
