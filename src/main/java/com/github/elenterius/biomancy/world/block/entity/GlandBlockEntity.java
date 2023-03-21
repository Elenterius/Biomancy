package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.world.inventory.BehavioralInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.HandlerBehaviors;
import com.github.elenterius.biomancy.world.inventory.menu.GlandMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class GlandBlockEntity extends SimpleContainerBlockEntity {

	public static final int OUTPUT_SLOTS = DecomposerRecipe.MAX_OUTPUTS;
	private final BehavioralInventory<?> outputInventory;

	public GlandBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenContainer, this::setChanged);
	}

	//	public GlandBlockEntity(BlockPos pos, BlockState state) {
//		super(ModBlockEntities.GLAND.get(), pos, state);
//		outputInventory = BehavioralInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenContainer, this::setChanged);
//	}

	public Component getDefaultName() {
		return TextComponentUtil.getTranslationText("container", "gland");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return GlandMenu.createServerMenu(containerId, playerInventory, outputInventory);
	}

	public ItemStack insertItemStack(ItemStack stack) {
		return outputInventory.insertItemStack(stack);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("OutputSlots", outputInventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		outputInventory.deserializeNBT(tag.getCompound("OutputSlots"));
	}

	@Override
	public void dropContainerContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, outputInventory);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == ModCapabilities.ITEM_HANDLER) {
			return outputInventory.getOptionalItemHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		outputInventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		outputInventory.revive();
	}

}
