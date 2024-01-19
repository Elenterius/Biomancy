package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class InsomniaCureSerum extends BasicSerum {

	protected static final int PHANTOM_SPAWN_THRESHOLD = 72_000;

	public InsomniaCureSerum(int color) {
		super(color);
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return target instanceof Player;
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof ServerPlayer player) affectPlayerSelf(tag, player);
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
//		if (!targetSelf.level.isClientSide && !targetSelf.level.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) return false;

		int ticksSinceRest = getTimeSinceRest(targetSelf);
		if (ticksSinceRest <= PHANTOM_SPAWN_THRESHOLD * 0.833f) {
			if (!targetSelf.level().isClientSide) {
				targetSelf.displayClientMessage(TextComponentUtil.getFailureMsgText("not_sleepy"), true);
			}
			return false;
		}

		return true;
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		targetSelf.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)); //reset insomnia
		applyDrowsyEffect(targetSelf);
	}

	private static void applyDrowsyEffect(LivingEntity livingEntity) {
		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.DROWSY.get(), 4 * 60 * 20, 0, false, false, true));
		livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 10 * 20, 0, false, false, false));
		livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 20, 0, false, false, false));
	}

	private int getTimeSinceRest(Player player) {
		StatsCounter statsCounter = player.level().isClientSide ? ((LocalPlayer) player).getStats() : ((ServerPlayer) player).getStats();
		return Mth.clamp(statsCounter.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
	}

}
