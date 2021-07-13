package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.GulgeContainer;
import com.github.elenterius.biomancy.inventory.GulgeInventory;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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

public class GulgeTileEntity extends OwnableTileEntity implements INamedContainerProvider {

	public static final short MAX_ITEM_AMOUNT = 32_000;
	private final GulgeInventory gulgeInventory;

	public GulgeTileEntity() {
		super(ModTileEntityTypes.GULGE.get());
		gulgeInventory = GulgeInventory.createServerContents(MAX_ITEM_AMOUNT, this::canPlayerOpenInv, this::markDirty);
		gulgeInventory.setOpenInventoryConsumer(this::onOpenInventory);
		gulgeInventory.setCloseInventoryConsumer(this::onCloseInventory);
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return GulgeContainer.createServerContainer(screenId, playerInventory, gulgeInventory);
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

	public boolean isEmpty() {
		return gulgeInventory.isEmpty();
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.put("Inventory", gulgeInventory.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		gulgeInventory.deserializeNBT(nbt.getCompound("Inventory"));
	}

	@Override
	public CompoundNBT writeToItemBlockEntityTag(CompoundNBT nbt) {
		super.writeToItemBlockEntityTag(nbt);
		if (!gulgeInventory.isEmpty()) nbt.put("Inventory", gulgeInventory.serializeNBT());
		return nbt;
	}

	@Override
	public void invalidateCaps() {
		gulgeInventory.getOptionalItemStackHandler().invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return gulgeInventory.getOptionalItemStackHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "gulge");
	}
}
