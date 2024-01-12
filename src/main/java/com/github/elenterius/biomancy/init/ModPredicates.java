package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.advancements.predicate.FoodItemPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;

public final class ModPredicates {

	private ModPredicates() {}

	public static void registerItemPredicates() {
		ItemPredicate.register(FoodItemPredicate.ID, FoodItemPredicate::deserializeFromJson);
	}

}
