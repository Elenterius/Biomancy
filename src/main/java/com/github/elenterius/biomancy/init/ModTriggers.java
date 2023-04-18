package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.advancements.trigger.SacrificedItemTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public final class ModTriggers {

	public static final SacrificedItemTrigger SACRIFICED_ITEM_TRIGGER = new SacrificedItemTrigger();

	private ModTriggers() {}

	public static void register() {
		CriteriaTriggers.register(SACRIFICED_ITEM_TRIGGER);
	}

}