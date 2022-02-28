package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.item.LarynxItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class VoiceBoxBlockEntity extends BlockEntity {

	public static final Predicate<ItemStack> VALID_ITEM = stack -> stack.getItem() instanceof LarynxItem;

	private final BehavioralInventory<?> inventory;

	public VoiceBoxBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.VOICE_BOX.get(), pos, state);
		inventory = BehavioralInventory.createServerContents(1, ish -> HandlerBehaviors.filterInput(ish, VALID_ITEM), player -> false, this::setChanged);
	}

	public ItemStack getStoredItemStack() {
		return inventory.getItem(0);
	}

	public void setStoredItemStack(ItemStack stack) {
		inventory.setItem(0, stack);
		setChanged();
	}

	public boolean playVoice(float volume, float pitch) {
		if (level == null || level.isClientSide) return false;

		ItemStack stack = inventory.getItem(0);
		if (!stack.isEmpty() && VALID_ITEM.test(stack)) {
			BlockPos pos = getBlockPos();
			double x = pos.getX() + 0.5d;
			double y = pos.getY() + 0.5d;
			double z = pos.getZ() + 0.5d;
			return ((LarynxItem) stack.getItem()).playVoice(stack, level, x, y, z, volume, pitch);
		}
		return false;
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Inventory", inventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
	}

	public void dropAllInvContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, inventory);
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

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

}
