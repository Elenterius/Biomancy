package com.github.elenterius.biomancy.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Tadpole;

import java.util.function.Predicate;

public enum MobMaturity {
	ANY(entity -> true),
	BABY(entity -> entity instanceof LivingEntity livingEntity && livingEntity.isBaby() || entity instanceof Tadpole),
	ADULT(entity -> entity instanceof LivingEntity livingEntity && !livingEntity.isBaby());

	private final Predicate<Entity> predicate;

	MobMaturity(Predicate<Entity> predicate) {
		this.predicate = predicate;
	}

	public static MobMaturity fromOrdinal(int ordinal) {
		if (ordinal < 0 || ordinal >= values().length) return ANY;
		return values()[ordinal];
	}

	public boolean test(Entity entity) {
		return predicate.test(entity);
	}

}
