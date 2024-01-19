package com.github.elenterius.biomancy.entity.ai.goal;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItemGoal extends Goal {

	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive();

	protected final Mob mob;
	protected final float searchDistance;
	protected final Predicate<ItemEntity> itemFilter;

	public FindItemGoal(Mob mob) {
		this(mob, 8f, ITEM_ENTITY_FILTER);
	}

	public FindItemGoal(Mob mob, float searchDistance) {
		this(mob, searchDistance, ITEM_ENTITY_FILTER);
	}

	public FindItemGoal(Mob mob, float searchDistance, Predicate<ItemEntity> itemFilter) {
		this.mob = mob;
		this.searchDistance = searchDistance;
		this.itemFilter = itemFilter;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		boolean handIsEmpty = mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
		if (!handIsEmpty) {
			return false;
		}
		else if (mob.getTarget() == null && mob.getLastHurtByMob() == null) {
			if (mob.getRandom().nextInt(10) != 0) return false;
			return !findItems(searchDistance, itemFilter).isEmpty();
		}

		return false;
	}

	@Override
	public void tick() {
		ItemStack heldStack = mob.getItemBySlot(EquipmentSlot.MAINHAND);
		if (heldStack.isEmpty()) {
			List<ItemEntity> list = findItems(searchDistance, itemFilter);
			if (!list.isEmpty()) mob.getNavigation().moveTo(list.get(0), 1.2f);
		}
	}

	@Override
	public void start() {
		List<ItemEntity> list = findItems(searchDistance, itemFilter);
		if (!list.isEmpty()) {
			mob.getNavigation().moveTo(list.get(0), 1.2f);
		}
	}

	public List<ItemEntity> findItems(double distance, Predicate<ItemEntity> filter) {
		return mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(distance, distance, distance), filter);
	}

}
