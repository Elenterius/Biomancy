package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.mixin.AgeableMobAccessor;
import com.github.elenterius.biomancy.mixin.ArmorStandAccessor;
import com.github.elenterius.biomancy.mixin.SlimeAccessor;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class GrowthSerum extends Serum {

	public GrowthSerum(int color) {
		super(color);
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return target instanceof Slime || target instanceof FleshBlob || target.isBaby();
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Slime slime) {
			int slimeSize = slime.getSize();
			if (slimeSize < 25) {
				((SlimeAccessor) slime).biomancy_setSlimeSize(slimeSize + 1, false);
			}
			else {
				slime.hurt(DamageSource.explosion(source), slime.getHealth()); //"explode" slime
			}
		}
		else if (target instanceof FleshBlob fleshBlob) {
			byte blobSize = fleshBlob.getBlobSize();
			if (blobSize < 10) {
				fleshBlob.setBlobSize((byte) (blobSize + 1), false);
			}
		}
		else if (target.isBaby()) {
			if (target instanceof Mob mob) { //includes animals, zombies, piglins, etc...
				mob.setBaby(false);
				if (target instanceof AgeableMob ageableMob) {
					AgeableMobAccessor accessor = (AgeableMobAccessor) ageableMob;
					if (accessor.biomancy_getForcedAge() != 0) {
						accessor.biomancy_setForcedAge(0); //unset forced age
					}
				}
			}
			else if (target instanceof ArmorStand) {
				((ArmorStandAccessor) target).biomancy_setSmall(false);
			}
		}
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {}

}
