package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.mixin.accessor.TadpoleAccessor;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LevelEvent;
import org.jetbrains.annotations.Nullable;

public class AgeingSerum extends BasicSerum {

	public AgeingSerum(int color) {
		super(color);
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return (target instanceof Mob && target.isBaby()) || target instanceof Guardian || target instanceof Tadpole;
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Guardian guardian) {
			convertToElderGuardian(level, guardian);
		}
		else if (target instanceof Tadpole tadpole) {
			convertToFrog(level, tadpole);
		}
		else if (target instanceof Mob mob) {
			MobUtil.convertToAdult(mob);
		}
	}

	private void convertToFrog(ServerLevel level, Tadpole tadpole) {
		((TadpoleAccessor) tadpole).biomancy$AgeUp();
		if (!tadpole.isSilent()) {
			level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, tadpole.blockPosition(), 0);
		}
	}

	private void convertToElderGuardian(ServerLevel level, Guardian guardian) {
		MobUtil.convertMobTo(level, guardian, EntityType.ELDER_GUARDIAN, true, (oldGuardian, elderGuardian) -> {
			if (!oldGuardian.isSilent()) {
				level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, oldGuardian.blockPosition(), 0);
			}
		});
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		//do nothing
	}

}
