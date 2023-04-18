package com.github.elenterius.biomancy.advancements.trigger;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SacrificedItemTrigger extends SimpleCriterionTrigger<SacrificedItemTrigger.TriggerInstance> {

	private static final ResourceLocation ID = BiomancyMod.createRL("sacrificed_item");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public SacrificedItemTrigger.TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
		ItemPredicate itempredicate = ItemPredicate.fromJson(json.get("item"));
		return new SacrificedItemTrigger.TriggerInstance(entityPredicate, itempredicate);
	}

	public void trigger(ServerPlayer player, ItemStack stack) {
		trigger(player, triggerInstance -> triggerInstance.matches(stack));
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		private final ItemPredicate itemPredicate;

		public TriggerInstance(EntityPredicate.Composite player, ItemPredicate itemPredicate) {
			super(SacrificedItemTrigger.ID, player);
			this.itemPredicate = itemPredicate;
		}

		public static SacrificedItemTrigger.TriggerInstance sacrificedItems(ItemLike... items) {
			ItemPredicate predicate = ItemPredicate.Builder.item().of(items).build();
			return new SacrificedItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, predicate);
		}

		public static SacrificedItemTrigger.TriggerInstance sacrificedItem(ItemLike item) {
			ItemPredicate predicate = ItemPredicate.Builder.item().of(item).build();
			return new SacrificedItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, predicate);
		}

		public static SacrificedItemTrigger.TriggerInstance sacrificedItem(TagKey<Item> tag) {
			ItemPredicate predicate = ItemPredicate.Builder.item().of(tag).build();
			return new SacrificedItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, predicate);
		}

		public static SacrificedItemTrigger.TriggerInstance sacrificedItem() {
			return new SacrificedItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, ItemPredicate.ANY);
		}

		public boolean matches(ItemStack stack) {
			return itemPredicate.matches(stack);
		}

		@Override
		public JsonObject serializeToJson(SerializationContext conditions) {
			JsonObject jsonObject = super.serializeToJson(conditions);
			jsonObject.add("item", itemPredicate.serializeToJson());
			return jsonObject;
		}

	}

}
