package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.integration.compat.ModsCompatHandler;
import com.github.elenterius.biomancy.integration.compat.pehkui.IPehkuiHelper;
import com.github.elenterius.biomancy.mixin.AgeableMobAccessor;
import com.github.elenterius.biomancy.mixin.ArmorStandAccessor;
import com.github.elenterius.biomancy.mixin.SlimeAccessor;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class GrowthSerum extends Serum {

	public GrowthSerum(int color) {
		super(color);
	}

	private static void resizeMob(Mob mob) {
		mob.setBaby(false);
		if (mob instanceof AgeableMob ageableMob) {
			AgeableMobAccessor accessor = (AgeableMobAccessor) ageableMob;
			if (accessor.biomancy_getForcedAge() != 0) {
				accessor.biomancy_setForcedAge(0); //unset forced age
			}
		}
	}

	private static void resizeArmorStand(ArmorStand armorStand) {
		((ArmorStandAccessor) armorStand).biomancy_setSmall(false);
	}

	private static void resizeFleshBlob(FleshBlob fleshBlob) {
		byte blobSize = fleshBlob.getBlobSize();
		if (blobSize < 10) {
			fleshBlob.setBlobSize((byte) (blobSize + 1), false);
		}
	}

	private static void resizeSlime(@Nullable LivingEntity source, Slime slime) {
		int slimeSize = slime.getSize();
		if (slimeSize < 25) {
			((SlimeAccessor) slime).biomancy_setSlimeSize(slimeSize + 1, false);
		}
		else {
			slime.hurt(DamageSource.explosion(source), slime.getHealth()); //"explode" slime
		}
	}

	private static void resizeWithPehkui(LivingEntity target) {
		IPehkuiHelper pehkuiHelper = ModsCompatHandler.getPehkuiHelper();
		if (pehkuiHelper.isResizable(target)) {
			float currentScale = pehkuiHelper.getScale(target);
			if (currentScale < 1.5f) {
				pehkuiHelper.setScale(target, Mth.clamp(currentScale * 1.5f, 0.5f, 1.5f));
			}
		}
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return target instanceof Slime || target instanceof FleshBlob || target.isBaby() || ModsCompatHandler.getPehkuiHelper().isResizable(target);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Slime slime) {
			resizeSlime(source, slime);
		}
		else if (target instanceof FleshBlob fleshBlob) {
			resizeFleshBlob(fleshBlob);
		}
		else if (target.isBaby()) {
			if (target instanceof Mob mob) { //includes animals, zombies, piglins, etc...
				resizeMob(mob);
			}
			else if (target instanceof ArmorStand armorStand) {
				resizeArmorStand(armorStand);
			}
		}
		else {
			resizeWithPehkui(target);
		}
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return ModsCompatHandler.getPehkuiHelper().isResizable(targetSelf);
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		resizeWithPehkui(targetSelf);
	}

}
