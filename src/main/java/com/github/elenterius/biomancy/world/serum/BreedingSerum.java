package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class BreedingSerum extends Serum {

	public BreedingSerum(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean affectBlock(CompoundTag tag, @Nullable LivingEntity source, Level level, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Animal animal && !animal.isBaby()) {
			if (!animal.level.isClientSide) {
				animal.addEffect(new MobEffectInstance(ModMobEffects.LIBIDO.get(), 12 * 20, 1, false, true));
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean affectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

}
