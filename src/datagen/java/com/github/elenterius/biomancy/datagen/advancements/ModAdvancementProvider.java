package com.github.elenterius.biomancy.datagen.advancements;

import com.github.elenterius.biomancy.advancements.trigger.SacrificedItemTrigger;
import com.github.elenterius.biomancy.datagen.lang.LangProvider;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModAdvancementProvider extends ForgeAdvancementProvider {

	public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper, LangProvider lang) {
		super(output, lookupProvider, fileHelper, List.of(new BiomancyAdvancementsGenerator(lang)));
	}

	protected static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike... items) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(items);
	}

	protected static InventoryChangeTrigger.TriggerInstance hasTag(TagKey<Item> tag) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
	}

	protected static ItemUsedOnLocationTrigger.TriggerInstance hasPlacedBlock(Block block) {
		return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block);
	}

	protected static KilledTrigger.TriggerInstance hasKilledEntity(EntityType<?> entityType) {
		return KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityType).build());
	}

	protected static KilledTrigger.TriggerInstance hasKilledEntityTag(TagKey<EntityType<?>> tag) {
		return KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(tag).build());
	}

	protected static SacrificedItemTrigger.TriggerInstance hasSacrificedItem(ItemLike item) {
		return SacrificedItemTrigger.TriggerInstance.sacrificedItem(item);
	}

	protected static SacrificedItemTrigger.TriggerInstance hasSacrificedTag(TagKey<Item> tag) {
		return SacrificedItemTrigger.TriggerInstance.sacrificedItem(tag);
	}

	protected static TradeTrigger.TriggerInstance hasTradedItems(ItemLike... items) {
		return new TradeTrigger.TriggerInstance(EntityPredicate.wrap(EntityPredicate.ANY), EntityPredicate.wrap(EntityPredicate.ANY), ItemPredicate.Builder.item().of(items).build());
	}

	protected static RecipeUnlockedTrigger.TriggerInstance hasUnlockedDefaultRecipe(ItemLike itemLike) {
		return RecipeUnlockedTrigger.unlocked(RecipeBuilder.getDefaultRecipeId(itemLike));
	}

}
