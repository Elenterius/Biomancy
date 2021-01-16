package com.github.elenterius.blightlings.tileentity;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModEntityTypes;
import com.github.elenterius.blightlings.inventory.GulgeContainer;
import com.github.elenterius.blightlings.inventory.GulgeContents;
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
	private final GulgeContents gulgeContents;

	public GulgeTileEntity() {
		super(ModEntityTypes.GULGE_TILE.get());
		gulgeContents = GulgeContents.createServerContents(MAX_ITEM_AMOUNT, this::canPlayerAccess, this::markDirty);
		gulgeContents.setOpenInventoryConsumer(this::onOpenInventory);
		gulgeContents.setCloseInventoryConsumer(this::onCloseInventory);
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return GulgeContainer.createServerContainer(screenId, playerInventory, gulgeContents);
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
		return gulgeContents.isEmpty();
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.put("Contents", gulgeContents.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		gulgeContents.deserializeNBT(nbt.getCompound("Contents"));
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return gulgeContents.itemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return BlightlingsMod.getTranslationText("container", "gulge");
	}
}
