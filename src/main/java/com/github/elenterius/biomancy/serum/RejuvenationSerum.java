package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.entity.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LevelEvent;

import javax.annotation.Nullable;

public class RejuvenationSerum extends Serum {

	public RejuvenationSerum(int color) {
		super(color);
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return (target instanceof Mob && !target.isBaby()) || target instanceof ElderGuardian;
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof ElderGuardian elderGuardian) {
			convertToGuardian(level, elderGuardian);
		}
		if (target instanceof Mob mob) {
			MobUtil.convertToBaby(mob, true); // includes animals, villagers, zombies, etc..
		}
	}

	private void convertToGuardian(ServerLevel level, ElderGuardian elderGuardian) {
		MobUtil.convertMobTo(level, elderGuardian, EntityType.GUARDIAN, true, (oldElderGuardian, guardian) -> {
			if (!oldElderGuardian.isSilent()) {
				level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, oldElderGuardian.blockPosition(), 0);
			}
		});
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		//do nothing;
	}

}
