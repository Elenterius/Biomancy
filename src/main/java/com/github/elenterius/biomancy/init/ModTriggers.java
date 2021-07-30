package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.trigger.EvolutionPoolCreatedTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public final class ModTriggers {

	public static final EvolutionPoolCreatedTrigger EVOLUTION_POOL_CREATED = new EvolutionPoolCreatedTrigger();

	private ModTriggers() {}

	public static void register() {
		CriteriaTriggers.register(EVOLUTION_POOL_CREATED);
	}

}