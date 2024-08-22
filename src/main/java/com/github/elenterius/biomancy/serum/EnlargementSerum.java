package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.integration.pehkui.PehkuiHelper;
import com.github.elenterius.biomancy.mixin.accessor.ArmorStandAccessor;
import com.github.elenterius.biomancy.mixin.accessor.SlimeAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class EnlargementSerum extends BasicSerum {

	public EnlargementSerum(int color) {
		super(color);
	}

	private void resizeArmorStand(ArmorStand armorStand) {
		((ArmorStandAccessor) armorStand).biomancy$setSmall(false);
	}

	private void resizeFleshBlob(@Nullable LivingEntity source, FleshBlob fleshBlob) {
		byte blobSize = fleshBlob.getBlobSize();
		if (blobSize < 10) {
			fleshBlob.setBlobSize((byte) (blobSize + 1), false);
		}
		else {
			DamageSource explosionDamage = fleshBlob.level().damageSources().explosion(source, fleshBlob);
			fleshBlob.hurt(explosionDamage, fleshBlob.getHealth()); //"explode" fleshBob
		}
	}

	private void resizeSlime(@Nullable LivingEntity source, Slime slime) {
		int slimeSize = slime.getSize();
		if (slimeSize < 25) {
			((SlimeAccessor) slime).biomancy$setSlimeSize(slimeSize + 1, false);
		}
		else {
			DamageSource explosionDamage = slime.level().damageSources().explosion(source, slime);
			slime.hurt(explosionDamage, slime.getHealth()); //"explode" slime
		}
	}

	private void resizeWithPehkui(LivingEntity target) {
		PehkuiHelper pehkuiHelper = ModsCompatHandler.getPehkuiHelper();
		float currentScale = pehkuiHelper.getScale(target);
		if (currentScale < 2f) {
			pehkuiHelper.setScale(target, Mth.clamp(currentScale + 0.25f, 0.25f, 2f));
		}
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return target instanceof Mob || target instanceof Player;
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof Slime slime) {
			resizeSlime(source, slime);
		}
		else if (target instanceof FleshBlob fleshBlob) {
			resizeFleshBlob(source, fleshBlob);
		}
		else if (target instanceof ArmorStand armorStand && armorStand.isSmall()) {
			resizeArmorStand(armorStand);
		}
		else {
			resizeWithPehkui(target);
		}
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return true;
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		resizeWithPehkui(targetSelf);
	}

}
