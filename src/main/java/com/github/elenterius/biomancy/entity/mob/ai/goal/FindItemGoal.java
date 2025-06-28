package com.github.elenterius.biomancy.entity.mob.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItemGoal extends Goal {

	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = itemEntity -> itemEntity.isAlive() && !itemEntity.hasPickUpDelay();

	protected final Mob mob;
	protected final double searchDistance;
	protected final double speedModifier;
	protected final Predicate<ItemEntity> itemFilter;

	public FindItemGoal(Mob mob) {
		this(mob, 8d, 1.2d, ITEM_ENTITY_FILTER);
	}

	public FindItemGoal(Mob mob, double searchDistance) {
		this(mob, searchDistance, 1.2d, ITEM_ENTITY_FILTER);
	}

	public FindItemGoal(Mob mob, double searchDistance, Predicate<ItemEntity> itemFilter) {
		this(mob, searchDistance, 1.2d, itemFilter);
	}

	public FindItemGoal(Mob mob, double searchDistance, double speedModifier, Predicate<ItemEntity> itemFilter) {
		this.mob = mob;
		this.searchDistance = searchDistance;
		this.speedModifier = speedModifier;
		this.itemFilter = itemFilter;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (!mob.canPickUpLoot()) return false;

		if (mob.getTarget() == null && mob.getLastHurtByMob() == null) {
			if (mob.getRandom().nextInt(10) != 0) return false;
			return findItem(searchDistance, itemFilter) != null;
		}

		return false;
	}

	@Override
	public void tick() {
		ItemEntity itemEntity = findItem(searchDistance, itemFilter);
		if (itemEntity != null) {
			mob.getNavigation().moveTo(itemEntity, speedModifier);
		}
	}

	@Override
	public void start() {
		ItemEntity itemEntity = findItem(searchDistance, itemFilter);
		if (itemEntity != null) {
			mob.getNavigation().moveTo(itemEntity, speedModifier);
		}
	}

	@Nullable
	public ItemEntity findItem(double distance, Predicate<ItemEntity> filter) {
		if (!mob.canPickUpLoot()) return null;

		List<ItemEntity> itemEntities = mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(distance, distance, distance), filter);
		ItemEntity result = null;
		double maxDistSqr = Double.MAX_VALUE;

		for (ItemEntity itemEntity : itemEntities) {
			if (mob.wantsToPickUp(itemEntity.getItem())) {
				double distSqr = itemEntity.distanceToSqr(mob);
				if (distSqr < maxDistSqr) {
					maxDistSqr = distSqr;
					result = itemEntity;
				}
			}
		}

		return result;
	}

}
