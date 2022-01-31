package com.github.elenterius.biomancy.world.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class MobUtil {

	private static final Marker LOG_MARKER = MarkerManager.getMarker("MobUtil");

	private MobUtil() {}

	public static boolean isBoss(LivingEntity entity) {
		return isBoss(entity.getType());
	}

	public static boolean isBoss(EntityType<?> entityType) {
		return ModTags.EntityTypes.BOSSES.contains(entityType);
	}

	public static boolean isSkeleton(LivingEntity entity) {
		return entity instanceof AbstractSkeleton || entity instanceof SkeletonHorse || EntityTypeTags.SKELETONS.contains(entity.getType());
	}

	public static boolean isUndead(LivingEntity entity) {
		return entity.getMobType() == MobType.UNDEAD;
	}

	public static boolean isNotUndead(LivingEntity entity) {
		return !isUndead(entity);
	}

	public static boolean isCloneable(EntityType<?> entityType) {
		return !ModTags.EntityTypes.NOT_CLONEABLE.contains(entityType);
	}

	public static float getVolume(EntityType<?> entityType) {
		return entityType.getWidth() * entityType.getWidth() * entityType.getHeight();
	}

	public static float getVolume(Entity entity) {
		return entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight();
	}

	public static boolean anyMatch(EntityType<?> target, EntityType<?> a, EntityType<?> b) {
		return a == target || b == target;
	}

	public static void setAttributeBaseValue(LivingEntity livingEntity, Attribute attribute, double value) {
		AttributeInstance instance = livingEntity.getAttribute(attribute);
		if (instance != null) instance.setBaseValue(value);
		else BiomancyMod.LOGGER.warn(LOG_MARKER, "Tried to set the value of a missing Attribute: {}, {}", attribute, livingEntity);
	}

	/**
	 * converts a given mob into a different mob
	 */
	public static <E extends Mob, T extends Mob> boolean convertMobTo(ServerLevel level, E mob, EntityType<T> outcomeMobType) {
		return convertMobTo(level, mob, outcomeMobType, true);
	}

	public static <E extends Mob, T extends Mob> boolean convertMobTo(ServerLevel level, E mob, EntityType<T> outcomeMobType, boolean copyEquipment) {
		return convertMobTo(level, mob, outcomeMobType, copyEquipment, (oldMob, outcome) -> {});
	}

	public static <E extends Mob, T extends Mob> boolean convertMobTo(ServerLevel world, E oldMob, EntityType<T> outcomeMobType, boolean copyEquipment, BiConsumer<E, T> onConvert) {
		if (ForgeEventFactory.canLivingConvert(oldMob, outcomeMobType, timer -> {})) {
			T newMob = oldMob.convertTo(outcomeMobType, copyEquipment);// create new mob with same settings & equipment and remove old entity
			if (newMob != null) {
				newMob.finalizeSpawn(world, world.getCurrentDifficultyAt(oldMob.blockPosition()), MobSpawnType.CONVERSION, null, null);
				newMob.invulnerableTime = 60;
				onConvert.accept(oldMob, newMob);
				ForgeEventFactory.onLivingConvert(oldMob, newMob);
				return true;
			}
		}
		return false;
	}

	/**
	 * converts living entity to other living entity
	 */
	public static boolean convertLivingEntityTo(ServerLevel world, LivingEntity oldEntity, EntityType<?> outcomeType, Predicate<LivingEntity> validEntity) {
		if (oldEntity.isRemoved()) return false;

		Entity entity = outcomeType.create(world);
		if (entity != null) {
			if (entity instanceof LivingEntity livingEntity && validEntity.test(livingEntity)) {
				//noinspection unchecked
				EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>) outcomeType;
				if (ForgeEventFactory.canLivingConvert(oldEntity, entityType, timer -> {})) {
					livingEntity.copyPosition(oldEntity);
					if (world.addFreshEntity(livingEntity)) {
						oldEntity.discard();
						if (livingEntity instanceof Mob mob) {
							mob.finalizeSpawn(world, world.getCurrentDifficultyAt(oldEntity.blockPosition()), MobSpawnType.CONVERSION, null, null);
						}
						livingEntity.invulnerableTime = 60;
						ForgeEventFactory.onLivingConvert(oldEntity, livingEntity);
						return true;
					}
				}
			}
			entity.discard();
		}

		return false;
	}

}
