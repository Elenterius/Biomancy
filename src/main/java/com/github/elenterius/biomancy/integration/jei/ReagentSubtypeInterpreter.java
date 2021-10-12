package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.reagent.Reagent;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

public class ReagentSubtypeInterpreter implements ISubtypeInterpreter {

	public static final ReagentSubtypeInterpreter INSTANCE = new ReagentSubtypeInterpreter();

	private ReagentSubtypeInterpreter() {}

	@Override
	public String apply(ItemStack stack) {
		if (!stack.hasTag()) {
			return ISubtypeInterpreter.NONE;
		}

		Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			return reagent.getTranslationKey();
		}

		return ISubtypeInterpreter.NONE;
	}

}
