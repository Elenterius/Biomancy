package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.ChewerContainer;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.recipe.ChewerRecipe;
import com.github.elenterius.biomancy.recipe.RecipeType;
import com.github.elenterius.biomancy.tileentity.state.ChewerStateData;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChewerTileEntity extends BFMachineTileEntity<ChewerRecipe, ChewerStateData> {

	public static final int FUEL_SLOTS = 1;
	public static final int EMPTY_BUCKET_SLOTS = 1;
	public static final int INPUT_SLOTS = 1;
	public static final int OUTPUT_SLOTS = 1;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 2;
	public static final RecipeType.ItemStackRecipeType<ChewerRecipe> RECIPE_TYPE = ModRecipes.CHEWER_RECIPE_TYPE;

	private final ChewerStateData stateData = new ChewerStateData();
	private final SimpleInventory<?> fuelInventory;
	private final SimpleInventory<?> emptyBucketInventory;
	private final SimpleInventory<?> inputInventory;
	private final SimpleInventory<?> outputInventory;

	public ChewerTileEntity() {
		super(ModTileEntityTypes.CHEWER.get());
		fuelInventory = SimpleInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterBiofuel, this::canPlayerOpenInv, this::setChanged);
		emptyBucketInventory = SimpleInventory.createServerContents(EMPTY_BUCKET_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
		inputInventory = SimpleInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = SimpleInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
	}

	@Override
	protected ChewerStateData getStateData() {
		return stateData;
	}

	@Override
	protected FluidTank getFuelTank() {
		return stateData.fuelTank;
	}

	@Override
	public int getMaxFuelAmount() {
		return MAX_FUEL;
	}

	@Override
	public int getFuelCost() {
		return FUEL_COST;
	}

	@Override
	protected SimpleInventory getEmptyBucketInventory() {
		return emptyBucketInventory;
	}

	@Override
	public ItemStack getStackInFuelSlot() {
		return fuelInventory.getItem(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		fuelInventory.setItem(0, stack);
	}

	@Override
	protected boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft) {
		return outputInventory.doesItemStackFit(0, stackToCraft);
	}

	@Override
	protected boolean craftRecipe(ChewerRecipe recipeToCraft, World world) {
		ItemStack result = recipeToCraft.assemble(inputInventory);
		if (!result.isEmpty() && outputInventory.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputInventory.getContainerSize(); idx++) {
				inputInventory.removeItem(idx, 1);
			}
			outputInventory.insertItemStack(0, result);
			setChanged();
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	protected ChewerRecipe resolveRecipeFromInput(World world) {
		return RECIPE_TYPE.getRecipeFromInventory(world, inputInventory).orElse(null);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "chewer");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return ChewerContainer.createServerContainer(screenId, playerInv, fuelInventory, emptyBucketInventory, inputInventory, outputInventory, stateData);
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropContents(world, pos, fuelInventory);
		InventoryHelper.dropContents(world, pos, emptyBucketInventory);
		InventoryHelper.dropContents(world, pos, inputInventory);
		InventoryHelper.dropContents(world, pos, outputInventory);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		stateData.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelInventory.serializeNBT());
		nbt.put("EmptyBucketSlots", emptyBucketInventory.serializeNBT());
		nbt.put("InputSlots", inputInventory.serializeNBT());
		nbt.put("OutputSlots", outputInventory.serializeNBT());
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		stateData.deserializeNBT(nbt);
		fuelInventory.deserializeNBT(nbt.getCompound("FuelSlots"));
		emptyBucketInventory.deserializeNBT(nbt.getCompound("EmptyBucketSlots"));
		inputInventory.deserializeNBT(nbt.getCompound("InputSlots"));
		outputInventory.deserializeNBT(nbt.getCompound("OutputSlots"));

		if (fuelInventory.getContainerSize() != FUEL_SLOTS || inputInventory.getContainerSize() != INPUT_SLOTS
				|| outputInventory.getContainerSize() != OUTPUT_SLOTS || emptyBucketInventory.getContainerSize() != EMPTY_BUCKET_SLOTS) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public void invalidateCaps() {
		fuelInventory.getOptionalItemStackHandler().invalidate();
		emptyBucketInventory.getOptionalItemStackHandler().invalidate();
		inputInventory.getOptionalItemStackHandler().invalidate();
		outputInventory.getOptionalItemStackHandler().invalidate();
		stateData.getOptionalFuelHandler().invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove) {
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (side == Direction.UP) return inputInventory.getOptionalItemStackHandler().cast();
				if (side == null || side == Direction.DOWN) return outputInventory.getOptionalItemStackHandler().cast();
				return fuelInventory.getOptionalItemStackHandler().cast();
			}
			else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return stateData.getOptionalFuelHandler().cast();
			}
		}
		return super.getCapability(cap, side);
	}
}
