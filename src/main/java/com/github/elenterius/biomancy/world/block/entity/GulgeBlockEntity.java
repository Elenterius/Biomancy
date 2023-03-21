package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.world.inventory.GulgeInventory;
import com.github.elenterius.biomancy.world.inventory.menu.GulgeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class GulgeBlockEntity extends SimpleContainerBlockEntity {

	public static final short MAX_ITEM_AMOUNT = 64 * 128;
	private final GulgeInventory gulgeInventory;

	public GulgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		gulgeInventory = GulgeInventory.createServerContents(MAX_ITEM_AMOUNT, this::canPlayerOpenContainer, this::setChanged);
	}

	//	public GulgeBlockEntity(BlockPos pos, BlockState state) {
//		super(ModBlockEntities.GULGE.get(), pos, state);
//		gulgeInventory = GulgeInventory.createServerContents(MAX_ITEM_AMOUNT, this::canPlayerOpenContainer, this::setChanged);
//	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return GulgeMenu.createServerMenu(containerId, playerInventory, gulgeInventory);
	}

	@Override
	public Component getDefaultName() {
		return TextComponentUtil.getTranslationText("container", "gulge");
	}

	public boolean isEmpty() {
		return gulgeInventory.isEmpty();
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (!gulgeInventory.isEmpty()) tag.put("Inventory", gulgeInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		gulgeInventory.deserializeNBT(tag.getCompound("Inventory"));
	}

	@Override
	public void dropContainerContents(Level level, BlockPos pos) {
		//do nothing
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == ModCapabilities.ITEM_HANDLER) {
			return gulgeInventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		gulgeInventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		gulgeInventory.revive();
	}

}
