package com.github.elenterius.biomancy.api.nutrients;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Experimental
public interface NutrientsContainerItem {

	String NUTRIENTS_TAG_KEY = "biomancy:nutrients";

	Predicate<ItemStack> NEED_NUTRIENTS_PREDICATE = stack -> stack.getItem() instanceof NutrientsContainerItem item && item.getNutrients(stack) < item.getMaxNutrients(stack);

	default void decreaseNutrients(ItemStack container, int decrement) {
		increaseNutrients(container, -decrement);
	}

	default void increaseNutrients(ItemStack container, int increment) {
		int nutrients = getNutrients(container);
		setNutrients(container, nutrients + increment);
	}

	default boolean consumeNutrients(ItemStack container, int amount) {
		int nutrients = getNutrients(container);
		if (nutrients < amount) return false;
		setNutrients(container, nutrients - amount);
		return true;
	}

	default boolean addNutrients(ItemStack container, int amount) {
		int nutrients = getNutrients(container);
		int maxNutrients = getMaxNutrients(container);
		if (nutrients + amount > maxNutrients) return false;
		setNutrients(container, nutrients + amount);
		return true;
	}

	int getMaxNutrients(ItemStack container);

	void onNutrientsChanged(ItemStack container, int oldValue, int newValue);

	default int getNutrients(ItemStack container) {
		return container.getOrCreateTag().getInt(NUTRIENTS_TAG_KEY);
	}

	default boolean hasNutrients(ItemStack container) {
		return getNutrients(container) > 0;
	}

	default void setNutrients(ItemStack container, int amount) {
		int maxNutrients = getMaxNutrients(container);
		int oldValue = getNutrients(container);
		int newValue = Mth.clamp(amount, 0, maxNutrients);
		container.getOrCreateTag().putInt(NUTRIENTS_TAG_KEY, newValue);
		onNutrientsChanged(container, oldValue, newValue);
	}

	default float getNutrientsPct(ItemStack container) {
		return getNutrients(container) / (float) getMaxNutrients(container);
	}

	boolean isValidNutrientsResource(ItemStack container, ItemStack resource);

	int getNutrientsResourceValue(ItemStack container, ItemStack resource);

	default ItemStack insertNutrients(ItemStack container, ItemStack resource) {
		if (resource.isEmpty()) return resource;
		if (!isValidNutrientsResource(container, resource)) return resource;

		final int nutrients = getNutrients(container);
		int maxNutrients = getMaxNutrients(container);
		if (nutrients >= maxNutrients) return resource;

		int resourceValue = getNutrientsResourceValue(container, resource);
		if (resourceValue <= 0) return resource;

		int neededAmount = Mth.floor(Math.max(0, maxNutrients - nutrients) / (float) resourceValue);
		if (neededAmount > 0) {
			setNutrients(container, nutrients + resourceValue);
			return ItemHandlerHelper.copyStackWithSize(resource, resource.getCount() - 1);
		}

		return resource;
	}

	default boolean handleOverrideStackedOnOther(ItemStack livingTool, Slot slot, ClickAction action, Player player) {
		if (livingTool.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

		if (!slot.getItem().isEmpty()) {
			ItemStack potentialResource = slot.getItem();
			ItemStack remainder = insertNutrients(livingTool, potentialResource);
			int insertedAmount = potentialResource.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				return !slot.safeTake(insertedAmount, insertedAmount, player).isEmpty();
			}
		}

		return false;
	}

	default boolean handleOverrideOtherStackedOnMe(ItemStack livingTool, ItemStack potentialResource, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (livingTool.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

		if (!potentialResource.isEmpty()) {
			ItemStack remainder = insertNutrients(livingTool, potentialResource);
			int insertedAmount = potentialResource.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				potentialResource.shrink(insertedAmount);
				return true;
			}
		}

		return false;
	}

}
