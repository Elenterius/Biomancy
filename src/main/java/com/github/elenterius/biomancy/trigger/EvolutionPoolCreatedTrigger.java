package com.github.elenterius.biomancy.trigger;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public class EvolutionPoolCreatedTrigger extends AbstractCriterionTrigger<EvolutionPoolCreatedTrigger.Instance> {

	private static final ResourceLocation ID = BiomancyMod.createRL("evolution_pool_created");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	protected EvolutionPoolCreatedTrigger.Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
		return new EvolutionPoolCreatedTrigger.Instance(entityPredicate);
	}

	public void trigger(ServerPlayerEntity player) {
		trigger(player, Instance::test);
	}

	public static class Instance extends CriterionInstance {

		public Instance(EntityPredicate.AndPredicate playerCondition) {
			super(EvolutionPoolCreatedTrigger.ID, playerCondition);
		}

		public static EvolutionPoolCreatedTrigger.Instance create() {
			return new EvolutionPoolCreatedTrigger.Instance(EntityPredicate.AndPredicate.ANY);
		}

		public boolean test() {
			return true;
		}

	}

}
