package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.FleshChestContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FleshChestTileEntity extends OwnableTileEntity implements INamedContainerProvider {

	public static final int INV_SLOTS_COUNT = 6 * 9;

	private final SimpleInvContents invContents;

	public FleshChestTileEntity() {
		super(ModTileEntityTypes.FLESH_CHEST.get());
		invContents = SimpleInvContents.createServerContents(INV_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		invContents.setOpenInventoryConsumer(this::onOpenInventory);
		invContents.setCloseInventoryConsumer(this::onCloseInventory);
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return FleshChestContainer.createServerContainer(screenId, playerInventory, invContents);
	}

	public void onOpenInventory(PlayerEntity player) {
//		if (!player.isSpectator()) {
//
//		}
	}

	public void onCloseInventory(PlayerEntity player) {
//		if (!player.isSpectator()) {
//
//		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.put("Inventory", invContents.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		invContents.deserializeNBT(nbt.getCompound("Inventory"));
		if (invContents.getSizeInventory() != INV_SLOTS_COUNT) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public CompoundNBT writeToItemBlockEntityTag(CompoundNBT nbt) {
		super.writeToItemBlockEntityTag(nbt);
		if (!invContents.isEmpty()) nbt.put("Inventory", invContents.serializeNBT());
		return nbt;
	}

	@Override
	public void invalidateCaps() {
		invContents.getOptionalItemStackHandler().invalidate();
		super.invalidateCaps();
	}

	@Nullable
	public IInventory getInventory() {
		return !removed ? invContents : null;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return invContents.getOptionalItemStackHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return BiomancyMod.getTranslationText("container", "bioflesh_chest");
	}

}