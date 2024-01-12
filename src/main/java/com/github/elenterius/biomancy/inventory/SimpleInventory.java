package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.inventory.itemhandler.FixedSizeItemStackHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class SimpleInventory extends BaseInventory<ItemStackHandler> {

	SimpleInventory(int slotAmount) {
		itemHandler = new FixedSizeItemStackHandler(slotAmount) {
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
	}

	SimpleInventory(int slotAmount, Notify markDirtyNotifier) {
		this(slotAmount);
		this.markDirtyNotifier = markDirtyNotifier;
	}

	SimpleInventory(int slotAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static SimpleInventory createServerContents(int slotAmount, Notify markDirtyNotifier) {
		return new SimpleInventory(slotAmount, markDirtyNotifier);
	}

	public static SimpleInventory createServerContents(int slotAmount, Predicate<Player> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory(slotAmount, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static SimpleInventory createClientContents(int slotAmount) {
		return new SimpleInventory(slotAmount);
	}

}
