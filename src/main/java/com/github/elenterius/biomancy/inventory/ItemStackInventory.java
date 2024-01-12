package com.github.elenterius.biomancy.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackInventory extends BaseInventory<ItemStackHandler> {

	private final ItemStack cachedInventoryHost;
	private final InventorySerializer serializer;

	ItemStackInventory(int slots, int maxSlotSize, ItemStack inventoryHost, InventorySerializer serializer) {
		this.serializer = serializer;
		itemHandler = new ItemStackHandler(slots) {
			@Override
			public int getSlotLimit(int slot) {
				return maxSlotSize;
			}

			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				setChanged();
			}
		};
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
		cachedInventoryHost = inventoryHost;
	}

	public static ItemStackInventory createServerContents(int slots, int maxSlotSize, ItemStack inventoryHost, InventorySerializer inventorySerializer) {
		ItemStackInventory inventory = new ItemStackInventory(slots, maxSlotSize, inventoryHost, inventorySerializer);
		inventory.deserializeFromHost();
		return inventory;
	}

	public static ItemStackInventory createServerContents(int slots, int maxSlotSize, ItemStack inventoryHost) {
		ItemStackInventory inventory = new ItemStackInventory(slots, maxSlotSize, inventoryHost, InventorySerializer.DEFAULT);
		inventory.deserializeFromHost();
		return inventory;
	}

	public static ItemStackInventory createClientContents(int slots, int maxSlotSize, ItemStack inventoryHost) {
		return new ItemStackInventory(slots, maxSlotSize, inventoryHost, InventorySerializer.DEFAULT);
	}

	private void serializeToHost() {
		serializer.serialize(cachedInventoryHost.getOrCreateTag(), this);
	}

	private void deserializeFromHost() {
		serializer.deserialize(cachedInventoryHost.getOrCreateTag(), this);
	}

	@Override
	public void setChanged() {
		serializeToHost();
		super.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		if (cachedInventoryHost.isEmpty()) return false;
		return super.stillValid(player);
	}

	@Override
	public ItemStackHandler getItemHandler() {
		deserializeFromHost(); //prime cheese
		return super.getItemHandler();
	}

	@Override
	public LazyOptional<IItemHandler> getOptionalItemHandler() {
		deserializeFromHost(); //prime cheese
		//we now get the inventory from the ItemStack NBT, this makes it available on the client as well if someone gets the cap
		return super.getOptionalItemHandler();
	}

	public interface InventorySerializer {
		String NBT_KEY = "Inventory";
		InventorySerializer DEFAULT = new InventorySerializer() {};
		InventorySerializer BLOCK_ENTITY_TAG = new InventorySerializer() {
			public static final String BLOCK_ENTITY_KEY = "BlockEntityTag";

			@Override
			public void serialize(CompoundTag store, INBTSerializable<CompoundTag> serializable) {
				CompoundTag blockTag = store.getCompound(BLOCK_ENTITY_KEY);
				blockTag.put(NBT_KEY, serializable.serializeNBT());
				store.put(BLOCK_ENTITY_KEY, blockTag);
			}

			@Override
			public void deserialize(CompoundTag store, INBTSerializable<CompoundTag> deserializable) {
				CompoundTag blockTag = store.getCompound(BLOCK_ENTITY_KEY);
				deserializable.deserializeNBT(blockTag.getCompound(NBT_KEY));
			}

			@Override
			public CompoundTag unwrap(CompoundTag store) {
				CompoundTag blockTag = store.getCompound(BLOCK_ENTITY_KEY);
				return blockTag.getCompound(NBT_KEY);
			}
		};

		default void serialize(CompoundTag store, INBTSerializable<CompoundTag> serializable) {
			store.put(NBT_KEY, serializable.serializeNBT());
		}

		default void deserialize(CompoundTag store, INBTSerializable<CompoundTag> deserializable) {
			deserializable.deserializeNBT(store.getCompound(NBT_KEY));
		}

		default CompoundTag unwrap(CompoundTag store) {
			return store.getCompound(NBT_KEY);
		}
	}

}
