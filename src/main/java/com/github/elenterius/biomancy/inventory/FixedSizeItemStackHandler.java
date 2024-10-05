package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.GiftSacItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class FixedSizeItemStackHandler extends ItemStackHandler implements SerializableItemHandler {

	public FixedSizeItemStackHandler(int size) {
		super(size);
	}

	@Override
	public CompoundTag serializeNBT() {
		ListTag list = new ListTag();

		for (int i = 0; i < stacks.size(); i++) {
			if (stacks.get(i).isEmpty()) continue;

			CompoundTag itemTag = new CompoundTag();
			itemTag.putInt("Slot", i);
			stacks.get(i).save(itemTag);
			list.add(itemTag);
		}

		CompoundTag tag = new CompoundTag();
		tag.put("Items", list);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		setSize(stacks.size()); //fixed size

		ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
		boolean isOverloaded = list.size() > stacks.size();

		if (!isOverloaded) {
			for (int i = 0; i < list.size(); i++) {
				CompoundTag itemTag = list.getCompound(i);
				int slotIndex = itemTag.getInt("Slot");

				if (slotIndex >= 0 && slotIndex < stacks.size()) {
					stacks.set(slotIndex, ItemStack.of(itemTag));
				}
			}
		}
		else {
			List<CompoundTag> overloadedItems = new ArrayList<>();
			int maxIndex = stacks.size() - 1;

			for (int i = 0; i < list.size(); i++) {
				CompoundTag itemTag = list.getCompound(i);
				int slotIndex = itemTag.getInt("Slot");

				if (slotIndex >= 0 && slotIndex < maxIndex) {
					stacks.set(slotIndex, ItemStack.of(itemTag));
				}
				else {
					overloadedItems.add(itemTag.copy());
				}
			}

			ItemStack giftSac = GiftSacItem.createFromItemTags(ModItems.GIFT_SAC.get(), overloadedItems);
			stacks.set(maxIndex, giftSac);
		}

		onLoad();
	}

}
