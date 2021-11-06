package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.item.CopycatFluteItem;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

//voice replicator
public class VoiceBoxTileEntity extends SimpleSyncedTileEntity {

	public static final Predicate<ItemStack> VALID_FLUTE_ITEM = stack -> stack.getItem() instanceof CopycatFluteItem;

	private final SimpleInventory<?> inventory;

	public VoiceBoxTileEntity() {
		super(ModTileEntityTypes.VOICE_BOX_TILE.get());
		inventory = SimpleInventory.createServerContents(1, ish -> HandlerBehaviors.filterInput(ish, VALID_FLUTE_ITEM), player -> false, this::setChanged);
	}

	public ItemStack getCopycatFlute() {
		return inventory.getItem(0);
	}

	public void setCopycatFlute(ItemStack stack) {
		inventory.setItem(0, stack);
		setChanged();
	}

	public boolean playVoice(float volume, float pitch) {
		if (level == null || level.isClientSide) return false;

		ItemStack stack = inventory.getItem(0);
		if (!stack.isEmpty() && VALID_FLUTE_ITEM.test(stack)) {
			BlockPos pos = getBlockPos();
			double x = pos.getX() + 0.5d;
			double y = pos.getY() + 0.5d;
			double z = pos.getZ() + 0.5d;
			return ((CopycatFluteItem) stack.getItem()).playVoice(stack, level, x, y, z, volume, pitch);
		}
		return false;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.put("Inventory", inventory.serializeNBT());
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		inventory.deserializeNBT(nbt.getCompound("Inventory"));
	}

	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropContents(world, pos, inventory);
	}

	@Override
	public void invalidateCaps() {
		inventory.getOptionalItemHandlerWithBehavior().invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventory.getOptionalItemHandlerWithBehavior().cast();
		}
		return super.getCapability(cap, side);
	}

}
