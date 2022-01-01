package com.github.elenterius.biomancy.world.entity;

import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public final class MobUtil {

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

}
