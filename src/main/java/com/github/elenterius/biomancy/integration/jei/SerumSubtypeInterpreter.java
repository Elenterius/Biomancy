package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.world.serum.Serum;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class SerumSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {

	public static final SerumSubtypeInterpreter INSTANCE = new SerumSubtypeInterpreter();

	private SerumSubtypeInterpreter() {}

	@Override
	public String apply(ItemStack stack, UidContext context) {
		if (!stack.hasTag()) {
			return IIngredientSubtypeInterpreter.NONE;
		}

		Serum reagent = Serum.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			return reagent.getTranslationKey();
		}

		return IIngredientSubtypeInterpreter.NONE;
	}

}
