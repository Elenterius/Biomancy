package com.github.elenterius.biomancy.world.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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

	public static void setAttributeBaseValue(LivingEntity livingEntity, Attribute attribute, double value) {
		AttributeInstance instance = livingEntity.getAttribute(attribute);
		if (instance != null) instance.setBaseValue(value);
		else BiomancyMod.LOGGER.warn(LOG_MARKER, "Tried to set the value of a missing Attribute: {}, {}", attribute, livingEntity);
	}

}
