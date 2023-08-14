package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.entity.FoodEater;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class EatFoodItemGoal<T extends PathfinderMob & FoodEater> extends Goal {

	protected static final int EATING_TICKS = 20 * 2 + 4;
	private final T mob;
	private final float eatChance;
	private int eatTimer;

	public EatFoodItemGoal(T mob) {
		this(mob, 0.01f);
	}

	public EatFoodItemGoal(T mob, float eatChance) {
		this.mob = mob;
		this.eatChance = eatChance;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
	}

	@Override
	public boolean canUse() {
		if (mob.getRandom().nextFloat() < eatChance) return hasEdibleFood();
		return false;
	}

	protected boolean hasEdibleFood() {
		return mob.getFoodItem().isEdible();
	}

	@Override
	public boolean canContinueToUse() {
		return eatTimer > 0 && hasEdibleFood();
	}

	@Override
	public void start() {
		eatTimer = adjustedTickDelay(mob.getFoodItem().getUseDuration() * 2);
		mob.getNavigation().stop();
		mob.setEating(true);
	}

	@Override
	public void stop() {
		eatTimer = 0;
		mob.setEating(false);
	}

	@Override
	public void tick() {
		eatTimer = Math.max(0, eatTimer - 1);

		if (eatTimer == adjustedTickDelay(4)) {
			if (hasEdibleFood()) {
				ItemStack stack = mob.getFoodItem();
				FoodProperties food = stack.getFoodProperties(mob);
				ItemStack eatenStack = stack.finishUsingItem(mob.level, mob);
				if (!eatenStack.isEmpty()) {
					mob.setFoodItem(eatenStack);
				}
				mob.ate(food);
			}
			mob.setEating(false);
		}
	}

}
