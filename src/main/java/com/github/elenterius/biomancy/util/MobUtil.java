package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import com.github.elenterius.biomancy.mixin.AgeableMobAccessor;
import com.github.elenterius.biomancy.mixin.EntityAccessor;
import com.github.elenterius.biomancy.mixin.ServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class MobUtil {

	private static final Marker LOG_MARKER = MarkerManager.getMarker("MobUtil");

	private MobUtil() {}

	public static boolean isCreativePlayer(LivingEntity entity) {
		return entity instanceof Player player && player.getAbilities().instabuild;
	}

	public static boolean isBoss(LivingEntity entity) {
		return isBoss(entity.getType());
	}

	public static boolean isBoss(EntityType<?> entityType) {
		return entityType.is(ModEntityTags.FORGE_BOSSES);
	}

	public static boolean isSkeleton(LivingEntity entity) {
		return entity instanceof AbstractSkeleton || entity instanceof SkeletonHorse || entity.getType().is(EntityTypeTags.SKELETONS);
	}

	public static boolean isWithered(LivingEntity entity) {
		return entity instanceof WitherBoss || entity instanceof WitherSkeleton;
	}

	public static boolean isUndead(LivingEntity entity) {
		return entity.getMobType() == MobType.UNDEAD;
	}

	public static boolean isNotUndead(LivingEntity entity) {
		return !isUndead(entity);
	}

	public static boolean isCloneable(EntityType<?> entityType) {
		return !entityType.is(ModEntityTags.NOT_CLONEABLE);
	}

	public static float getVolume(EntityType<?> entityType) {
		return entityType.getWidth() * entityType.getWidth() * entityType.getHeight();
	}

	public static float getVolume(Entity entity) {
		return entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight();
	}

	/**
	 * @return gravity, positive is downwards force and negative is upwards force
	 */
	public static double getGravity(Entity entity) {
		return entity instanceof LivingEntity livingEntity ? livingEntity.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) : 0.08d;
	}

	//	public static double getGravitationalAcceleration(Entity entity) {
	//		return entity.getDeltaMovement().y;
	//	}

	public static double getWeight(Entity entity) {
		return getVolume(entity) * getGravity(entity);
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
	 * Attempts to turn an adult mob into a baby. Success is not guaranteed.
	 *
	 * @param mob           adult mob
	 * @param disableAgeing prevent {@link AgeableMob} babies from growing into adults
	 */
	public static void convertToBaby(Mob mob, boolean disableAgeing) {
		if (mob.isBaby()) return;

		mob.setBaby(true);
		if (mob instanceof AgeableMob ageableMob) {
			ageableMob.setAge(AgeableMob.BABY_START_AGE);
			if (disableAgeing) {
				((AgeableMobAccessor) ageableMob).biomancy$setForcedAge(AgeableMob.BABY_START_AGE);
			}
		}
	}

	public static void convertToAdult(Mob mob) {
		if (!mob.isBaby()) return;

		mob.setBaby(false);
		if (mob instanceof AgeableMob ageableMob) {
			removeForcedAge(ageableMob);
		}
	}

	public static void removeForcedAge(AgeableMob mob) {
		AgeableMobAccessor accessor = (AgeableMobAccessor) mob;
		if (accessor.biomancy$getForcedAge() != 0) {
			accessor.biomancy$setForcedAge(0); //unset forced age
		}
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

	/**
	 * This should return a position vector that can be used to more safely spawn entities as it should mitigate entities getting stuck in walls.<br>
	 *
	 * @return a position that is offset based on the hit direction and the entity size
	 */
	public static Vec3 getAdjustedSpawnPositionFor(BlockPos pos, Vec3 clickLocation, Direction clickedFace, Entity entity) {
		//check if neighbor blocks are not empty
		BlockPos offsetPos = pos.offset(clickedFace.getNormal());
		int down = entity.level().getBlockState(offsetPos.below()).isAir() ? 0 : 1;
		int up = entity.level().getBlockState(offsetPos.above()).isAir() ? 0 : 1;
		int north = entity.level().getBlockState(offsetPos.north()).isAir() ? 0 : 1;
		int south = entity.level().getBlockState(offsetPos.south()).isAir() ? 0 : 1;
		int west = entity.level().getBlockState(offsetPos.west()).isAir() ? 0 : 1;
		int east = entity.level().getBlockState(offsetPos.east()).isAir() ? 0 : 1;

		float halfBBWidth = entity.getBbWidth() * 0.5f;
		float halfBBHeight = entity.getBbHeight() * 0.5f;
		double x = clickLocation.x;
		double y = clickLocation.y - halfBBHeight; //offset to center entity
		double z = clickLocation.z;

		if (west + east == 2) x = offsetPos.getX() + 0.5d;
		else if (east == 1) {
			x = offsetPos.getX() + 1f - halfBBWidth;
			if (clickLocation.x < x) x = clickLocation.x;
		}
		else if (west == 1) {
			x = offsetPos.getX() + halfBBWidth;
			if (clickLocation.x > x) x = clickLocation.x;
		}

		if (north + south == 2) z = offsetPos.getZ() + 0.5d;
		else if (south == 1) {
			z = offsetPos.getZ() + 1f - halfBBWidth;
			if (clickLocation.z < z) z = clickLocation.z;
		}
		else if (north == 1) {
			z = offsetPos.getZ() + halfBBWidth;
			if (clickLocation.z > z) z = clickLocation.z;
		}

		if (down + up == 2) y = offsetPos.getY() + 0.5d - halfBBHeight;
		else if (down == 1) {
			y = offsetPos.getY();
			if (clickLocation.y - halfBBHeight > y) y = clickLocation.y - halfBBHeight;
		}
		else if (up == 1) {
			y = offsetPos.getY() + 1f - entity.getBbHeight();
			if (clickLocation.y - halfBBHeight < y) y = clickLocation.y - halfBBHeight;
		}

		return new Vec3(x, y, z);
	}

	public static void randomizeUUID(Entity entity) {
		RandomSource randomSource = ((EntityAccessor) entity).biomancy$random();
		entity.setUUID(Mth.createInsecureUUID(randomSource));
	}

	/**
	 * Checks if entity id is unique and not already in queue to be summoned
	 */
	public static boolean isEntityIdUnique(ServerLevel level, Entity entity) {
		return !isEntityIdLoaded(level, entity.getUUID());
	}

	public static boolean isEntityIdLoaded(ServerLevel level, UUID uuid) {
		//noinspection resource
		return ((ServerLevelAccessor) level).biomancy$entityManager().isLoaded(uuid);
	}
}
