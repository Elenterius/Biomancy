package com.github.elenterius.biomancy.world.block.storagesac;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.github.elenterius.biomancy.world.block.entity.SimpleContainerBlockEntity;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.StorageSacMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StorageSacBlockEntity extends SimpleContainerBlockEntity {

	public static final int SLOTS = 3 * 5;
	public static final String TOP5_BY_COUNT_KEY = "Top5ByCount";
	public static final String INVENTORY_KEY = "Inventory";
	private final BehavioralInventory<?> inventory;
	protected final ItemStackCounter itemCounter = new ItemStackCounter();

	private List<ItemStackCounter.CountedItem> top5ItemsByCount = List.of();

	public StorageSacBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.STORAGE_SAC.get(), pos, state);
		inventory = BehavioralInventory.createServerContents(SLOTS, HandlerBehaviors::denyItemWithFilledInventory, this::canPlayerOpenContainer, this::onContentsChanged);
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return StorageSacMenu.createServerMenu(containerId, playerInventory, inventory);
	}

	public boolean canPlayerOpenInv(Player player) {
		if (level == null || level.getBlockEntity(worldPosition) != this) return false;
		return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) < 8d * 8d;
	}

	@Override
	public Component getDefaultName() {
		return TextComponentUtil.getTranslationText("container", "sac");
	}

	public List<ItemStackCounter.CountedItem> getItemsForRendering() {
		return top5ItemsByCount;
	}

	protected void countAllItems() {
		itemCounter.clear();
		itemCounter.accountStacks(inventory);
		top5ItemsByCount = itemCounter.getItemCountSorted(5, false);
	}

	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	protected void onContentsChanged() {
		countAllItems();
		setChanged();
		syncToClient();
	}

	protected void syncToClient() {
		if (level == null) return;
		if (level.isClientSide) return;
		BlockState state = getBlockState();
		level.sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS); //trigger sync to client using the data from getUpdateTag()
	}

	@Override
	public CompoundTag getUpdateTag() {
		//serialize data for sync to client
		CompoundTag tag = new CompoundTag();
		tag.put(TOP5_BY_COUNT_KEY, serializeTop5());
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		//handle received data on the client side from level chunk load
		super.handleUpdateTag(tag);
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put(INVENTORY_KEY, inventory.serializeNBT());
		//		tag.put(TOP5_BY_COUNT_KEY, serializeTop5()); //serialize for block destruction
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (tag.contains(INVENTORY_KEY)) {
			inventory.deserializeNBT(tag.getCompound(INVENTORY_KEY));
			countAllItems();
		}

		if (tag.contains(TOP5_BY_COUNT_KEY)) {
			top5ItemsByCount = deserializeTop5(tag.getCompound(TOP5_BY_COUNT_KEY));
		}
	}

	public List<ItemStackCounter.CountedItem> deserializeTop5(CompoundTag store) {
		List<ItemStackCounter.CountedItem> items = new ArrayList<>();
		ListTag list = store.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag itemTag = list.getCompound(i);
			ItemStack stack = ItemStack.of(itemTag);
			if (!stack.isEmpty()) {
				int amount = itemTag.getInt("Amount");
				stack.setCount(Mth.clamp(amount, 1, 3));
				items.add(new ItemStackCounter.CountedItem(stack, amount));
			}
		}
		return items;
	}

	public CompoundTag serializeTop5() {
		ListTag list = new ListTag();

		for (ItemStackCounter.CountedItem countedItem : top5ItemsByCount) {
			CompoundTag tag = new CompoundTag();
			countedItem.stack().save(tag);
			tag.putInt("Amount", countedItem.amount());
			list.add(tag);
		}

		CompoundTag store = new CompoundTag();
		store.put("Items", list);

		return store;
	}

	@Override
	public void dropContainerContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, inventory);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == ModCapabilities.ITEM_HANDLER) {
			return inventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		inventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		inventory.revive();
	}

}
