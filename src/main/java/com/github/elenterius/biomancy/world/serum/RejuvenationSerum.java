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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class RejuvenationSerum extends Serum {

	public RejuvenationSerum(int color) {
		super(color);
	}

	private static void resizeArmorStand(ArmorStandAccessor armorStand) {
		armorStand.biomancy_setSmall(true);
	}

	private static void resizeFleshBlob(FleshBlob fleshBlob) {
		byte blobSize = fleshBlob.getBlobSize();
		if (blobSize > 1) {
			fleshBlob.setBlobSize((byte) (blobSize - 1), false);
		}
	}

	private static void resizeSlime(Slime slime) {
		int slimeSize = slime.getSize();
		if (slimeSize > 1) {
			((SlimeAccessor) slime).biomancy_setSlimeSize(slimeSize - 1, false);
		}
	}

	private static boolean tryToResizeWithPehkui(LivingEntity target) {
		IPehkuiHelper pehkuiHelper = ModsCompatHandler.getPehkuiHelper();
		if (pehkuiHelper.isResizable(target)) {
			float currentScale = pehkuiHelper.getScale(target);
			float minScale = target instanceof Player ? 0.5f : 1f;
			if (currentScale > minScale) {
				pehkuiHelper.setScale(target, Mth.clamp(currentScale * 0.5f, minScale, 1.5f));
				return true;
			}
		}
		return false;
	}

	private static void resizeWithPehkui(LivingEntity target) {
		IPehkuiHelper pehkuiHelper = ModsCompatHandler.getPehkuiHelper();
		if (pehkuiHelper.isResizable(target)) {
			float currentScale = pehkuiHelper.getScale(target);
			if (currentScale > 0.5f) {
				pehkuiHelper.setScale(target, Mth.clamp(currentScale * 0.5f, 0.5f, 1.5f));
			}
		}
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return target instanceof Slime || target instanceof FleshBlob || !target.isBaby() || ModsCompatHandler.getPehkuiHelper().isResizable(target);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Slime slime) { // includes MagmaCube
			resizeSlime(slime);
		}
		else if (target instanceof FleshBlob fleshBlob) {
			resizeFleshBlob(fleshBlob);
		}
		else if (!target.isBaby()) {
			if (target instanceof ArmorStand armorStand) {
				resizeArmorStand((ArmorStandAccessor) armorStand);
			}
			else {
				boolean resizedWithPehkui = tryToResizeWithPehkui(target);

				if (!resizedWithPehkui && target instanceof Mob mob) { // includes animals, villagers, zombies, etc..
					mob.setBaby(true);
					if (target instanceof AgeableMob ageableMob) {
						ageableMob.setAge(AgeableMob.BABY_START_AGE);
						((AgeableMobAccessor) ageableMob).biomancy_setForcedAge(AgeableMob.BABY_START_AGE); //should prevent mobs from growing into adults
					}
				}
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
