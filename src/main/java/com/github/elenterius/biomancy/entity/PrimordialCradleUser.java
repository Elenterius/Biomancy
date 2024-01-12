package com.github.elenterius.biomancy.entity;

import com.github.elenterius.biomancy.entity.ai.goal.FindItemGoal;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface PrimordialCradleUser {

	Set<Item> SPECIAL_ITEMS_TO_HOLD = Set.of(ModItems.LIVING_FLESH.get(), ModItems.CREATOR_MIX.get(), Items.ROTTEN_FLESH, Items.SPIDER_EYE);
	Predicate<ItemEntity> SPECIAL_ITEM_ENTITY_FILTER = itemEntity -> {
		if (!FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity)) return false;

		ItemStack stack = itemEntity.getItem();
		if (SPECIAL_ITEMS_TO_HOLD.contains(stack.getItem())) return true;
		return stack.isEdible() && Optional.ofNullable(stack.getFoodProperties(null)).map(FoodProperties::isMeat).orElse(false);
	};

	ItemStack getTributeItemForCradle();

	boolean hasTributeForCradle();

}
