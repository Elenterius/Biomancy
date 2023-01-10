package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.mixin.ZombieVillagerMixinAccessor;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public class CleansingSerum extends Serum {

	public CleansingSerum(int color) {
		super(color);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		clearPotionEffects(target);
		clearAbsorption(target);
		resetPehkuiSize(target);

		if (target instanceof ZombieVillager) {
			if (ForgeEventFactory.canLivingConvert(target, EntityType.VILLAGER, timer -> {})) {
				((ZombieVillagerMixinAccessor) target).biomancy_cureZombie((ServerLevel) target.level);
			}
		}
		else if (target instanceof WitherSkeleton skeleton) {
			MobUtil.convertMobTo((ServerLevel) target.level, skeleton, EntityType.SKELETON);
		}

		if (target instanceof FleshBlob fleshBlob) {
			fleshBlob.clearStoredDNA();
		}
	}

	private void resetPehkuiSize(LivingEntity target) {
		ModsCompatHandler.getPehkuiHelper().resetSize(target);
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		clearPotionEffects(targetSelf);
		clearAbsorption(targetSelf);
		resetPehkuiSize(targetSelf);
	}

	private void clearPotionEffects(LivingEntity target) {
		target.removeAllEffects();
	}

	private void clearAbsorption(LivingEntity target) {
		target.setAbsorptionAmount(0);
	}

}
