package com.github.elenterius.biomancy.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindItemGoal extends Goal {

	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = (itemEntity) -> !itemEntity.cannotPickup() && itemEntity.isAlive();

	protected final CreatureEntity creature;
	protected final float searchDistance;
	protected final Predicate<ItemEntity> itemFilter;

	public FindItemGoal(CreatureEntity creatureIn) {
		this(creatureIn, 8f, ITEM_ENTITY_FILTER);
	}

	public FindItemGoal(CreatureEntity creatureIn, float searchDistanceIn) {
		this(creatureIn, searchDistanceIn, ITEM_ENTITY_FILTER);
	}

	public FindItemGoal(CreatureEntity creatureIn, float searchDistanceIn, Predicate<ItemEntity> itemFilterIn) {
		creature = creatureIn;
		searchDistance = searchDistanceIn;
		itemFilter = itemFilterIn;
		setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean shouldExecute() {
		boolean handIsEmpty = creature.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
		if (!handIsEmpty) {
			return false;
		}
		else if (creature.getAttackTarget() == null && creature.getRevengeTarget() == null) {
			if (creature.getRNG().nextInt(10) != 0) return false;
			return !findItems(searchDistance, itemFilter).isEmpty();
		}

		return false;
	}

	@Override
	public void tick() {
		ItemStack heldStack = creature.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if (heldStack.isEmpty()) {
			List<ItemEntity> list = findItems(searchDistance, itemFilter);
			if (!list.isEmpty()) creature.getNavigator().tryMoveToEntityLiving(list.get(0), 1.2f);
		}
	}

	@Override
	public void startExecuting() {
		List<ItemEntity> list = findItems(searchDistance, itemFilter);
		if (!list.isEmpty()) {
			creature.getNavigator().tryMoveToEntityLiving(list.get(0), 1.2f);
		}
	}

	public List<ItemEntity> findItems(double distance, Predicate<ItemEntity> filter) {
		return creature.world.getEntitiesWithinAABB(ItemEntity.class, creature.getBoundingBox().grow(distance, distance, distance), filter);
	}
}
