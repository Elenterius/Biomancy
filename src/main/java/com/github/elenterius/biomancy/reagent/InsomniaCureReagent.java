package com.github.elenterius.biomancy.reagent;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InsomniaCureReagent extends Reagent {

	public InsomniaCureReagent(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof PlayerEntity) {
			return affectPlayerSelf(nbt, (PlayerEntity) target);
		}
		return false;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
//		if (!targetSelf.world.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) return false; // doesn't work on client side anyways

		if (!targetSelf.world.isRemote) {
			ServerStatisticsManager statisticsManager = ((ServerPlayerEntity) targetSelf).getStats();
			int timeSinceRest = MathHelper.clamp(statisticsManager.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
			if (timeSinceRest > 20 * 60) {
				targetSelf.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)); //reset insomnia
				return true;
			}
		}
		else {
			StatisticsManager statisticsManager = ((ClientPlayerEntity) targetSelf).getStats();
			int timeSinceRest = MathHelper.clamp(statisticsManager.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
			if (timeSinceRest > 20 * 60) return true;

			targetSelf.sendStatusMessage(new TranslationTextComponent("msg.biomancy.not_sleepy").mergeStyle(TextFormatting.RED), true);
		}

		return false;
	}

}
