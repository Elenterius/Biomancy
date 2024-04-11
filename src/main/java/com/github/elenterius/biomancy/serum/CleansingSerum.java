package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.mixin.ZombieVillagerMixinAccessor;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;

public class CleansingSerum extends BasicSerum {

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
				((ZombieVillagerMixinAccessor) target).biomancy$cureZombie((ServerLevel) target.level());
			}
		}
		else if (target instanceof WitherSkeleton skeleton) {
			MobUtil.convertMobTo((ServerLevel) target.level(), skeleton, EntityType.SKELETON);
		}

		if (target instanceof AgeableMob ageableMob) {
			MobUtil.removeForcedAge(ageableMob);
		}

		if (target instanceof Player player) {
			player.causeFoodExhaustion(40);
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

		targetSelf.causeFoodExhaustion(40);
	}

	private void clearPotionEffects(LivingEntity target) {
		removeAllEffects(target); //this is a replacement for LivingEntity.removeAllEffects()
	}

	private void removeAllEffects(LivingEntity target) {
		List<MobEffectInstance> activeEffects = List.copyOf(target.getActiveEffects());

		for (MobEffectInstance activeEffect : activeEffects) {
			MobEffect effect = activeEffect.getEffect();
			if (!ModMobEffectTags.isNotRemovableWithCleansingSerum(effect)) {
				target.removeEffect(effect);
			}
		}
	}

	private void clearAbsorption(LivingEntity target) {
		target.setAbsorptionAmount(0);
	}

}
