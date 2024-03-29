package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.tags.ModDamageTypeTags;
import com.github.elenterius.biomancy.util.CombatUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.UUID;

public class CorrosiveEffect extends StatusEffect {

	public CorrosiveEffect(MobEffectCategory category, int color) {
		super(category, color, false);
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		int effectLevel = amplifier + 1;
		boolean isWet = livingEntity.isInWaterRainOrBubble();
		float conversionProbability = 0.1f + 0.05f * effectLevel + (isWet ? 0.15f : 0);

		if (livingEntity.level() instanceof ServerLevel serverLevel && livingEntity.level().random.nextFloat() < conversionProbability) {
			DamageSource lastDamageSource = livingEntity.getLastDamageSource();
			if (lastDamageSource != null && lastDamageSource.is(ModDamageTypeTags.FORGE_IS_ACID)) {
				convertZombieToSkeleton(serverLevel, livingEntity);
			}
		}

		if (!livingEntity.isAlive()) return;

		float damage = 2 * effectLevel + (isWet ? 0.5f : 0);
		CombatUtil.hurtWithAcid(livingEntity, damage);

		if (livingEntity.level().random.nextFloat() < 0.85f && livingEntity.level() instanceof ServerLevel serverLevel) {
			float xz = livingEntity.getBbWidth() * 0.25f;
			float y = livingEntity.getBbHeight() * 0.25f;
			serverLevel.sendParticles(ModParticleTypes.FALLING_ACID.get(), livingEntity.getX(), livingEntity.getY(0.5f), livingEntity.getZ(), 4, xz, y, xz, 0);
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return (duration + 1) % 8 == 0;
	}

	private boolean convertZombieToSkeleton(ServerLevel level, LivingEntity livingEntity) {
		if (livingEntity instanceof Zombie zombie && ForgeEventFactory.canLivingConvert(zombie, EntityType.SKELETON, timer -> {})) {
			Skeleton skeleton = zombie.convertTo(EntityType.SKELETON, true); // create new entity with same settings & equipment and remove old entity
			if (skeleton != null) {
				//skeleton.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), MobSpawnType.CONVERSION, null, null);
				skeleton.invulnerableTime = 60;
				ForgeEventFactory.onLivingConvert(zombie, skeleton);
				if (!zombie.isSilent()) {
					level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, zombie.blockPosition(), 0);
				}
				return true;
			}
		}
		else if (livingEntity instanceof ZombieHorse zombieHorse && ForgeEventFactory.canLivingConvert(zombieHorse, EntityType.SKELETON_HORSE, timer -> {})) {
			SkeletonHorse horse = zombieHorse.convertTo(EntityType.SKELETON_HORSE, true); // create new entity with same settings & equipment and remove old entity
			if (horse != null) {
				//horse.finalizeSpawn(level, level.getCurrentDifficultyAt(zombieHorse.blockPosition()), MobSpawnType.CONVERSION, null, null);
				horse.invulnerableTime = 60;
				UUID owner = zombieHorse.getOwnerUUID();
				if (owner != null) {
					horse.setOwnerUUID(owner);
				}
				horse.setTamed(zombieHorse.isTamed());
				ForgeEventFactory.onLivingConvert(zombieHorse, horse);
				if (!zombieHorse.isSilent()) {
					level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, zombieHorse.blockPosition(), 0);
				}
				return true;
			}
		}

		return false;
	}

}
