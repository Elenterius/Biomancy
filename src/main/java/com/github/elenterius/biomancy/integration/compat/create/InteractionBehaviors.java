package com.github.elenterius.biomancy.integration.compat.create;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.simibubi.create.AllInteractionBehaviours;

final class InteractionBehaviors {

	private InteractionBehaviors() {}

	static void register() {
		AllInteractionBehaviours.registerBehaviour(ModBlocks.FLESH_DOOR.get(), new FleshDoorMovingInteraction());
		AllInteractionBehaviours.registerBehaviour(ModBlocks.FLESH_IRIS_DOOR.get(), new IrisDoorMovingInteraction());
	}

}
