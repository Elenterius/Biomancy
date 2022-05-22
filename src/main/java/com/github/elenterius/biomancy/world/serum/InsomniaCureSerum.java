package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.util.TextComponentUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class InsomniaCureSerum extends Serum {

	public InsomniaCureSerum(int color) {
		super(color);
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return target instanceof Player;
	}

	@Override
	public void affectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Player player) {
			affectPlayerSelf(tag, player);
		}
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, Player targetSelf) {
//		if (!targetSelf.world.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) return false; // doesn't work on client side anyways

		if (!targetSelf.level.isClientSide) {
			ServerStatsCounter statsCounter = ((ServerPlayer) targetSelf).getStats();
			int timeSinceRest = Mth.clamp(statsCounter.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
			if (timeSinceRest > 20 * 60) {
				targetSelf.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)); //reset insomnia
			}
		}
		else {
			StatsCounter statsCounter = ((LocalPlayer) targetSelf).getStats();
			int timeSinceRest = Mth.clamp(statsCounter.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
			if (timeSinceRest > 20 * 60) return;

			targetSelf.displayClientMessage(TextComponentUtil.getFailureMsgText("not_sleepy"), true);
		}
	}

}
