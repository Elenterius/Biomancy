package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.DigesterContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import com.github.elenterius.biomancy.inventory.itemhandler.behavior.ItemHandlerBehavior;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.recipe.RecipeType;
import com.github.elenterius.biomancy.tileentity.state.DigesterStateData;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class DigesterTileEntity extends MachineTileEntity<DigesterRecipe, DigesterStateData> {

	public static final int FUEL_SLOTS_COUNT = 1;
	public static final int FUEL_OUT_SLOTS_COUNT = 1;
	public static final int INPUT_SLOTS_COUNT = 1;
	public static final int OUTPUT_SLOTS_COUNT = 1;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 1;
	public static final Predicate<ItemStack> VALID_FUEL_ITEM = stack -> {
		if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER) return true;
		return FluidUtil.getFluidContained(stack).map(DigesterTileEntity::isFluidValidFuel).orElse(false);
	};
	public static final RecipeType.ItemStackRecipeType<DigesterRecipe> RECIPE_TYPE = ModRecipes.DIGESTER_RECIPE_TYPE;

	private final DigesterStateData stateData = new DigesterStateData();
	private final SimpleInvContents fuelContents;
	private final SimpleInvContents fuelOutContents;
	private final SimpleInvContents inputContents;
	private final SimpleInvContents outputContents;

	public DigesterTileEntity() {
		super(ModTileEntityTypes.DIGESTER.get());
		fuelContents = SimpleInvContents.createServerContents(FUEL_SLOTS_COUNT, ItemHandlerBehavior::filterBiofuel, this::canPlayerOpenInv, this::markDirty);
		fuelOutContents = SimpleInvContents.createServerContents(FUEL_OUT_SLOTS_COUNT, ItemHandlerBehavior::denyInput, this::canPlayerOpenInv, this::markDirty);
		inputContents = SimpleInvContents.createServerContents(INPUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		outputContents = SimpleInvContents.createServerContents(OUTPUT_SLOTS_COUNT, ItemHandlerBehavior::denyInput, this::canPlayerOpenInv, this::markDirty);
	}

	@Override
	protected DigesterStateData getStateData() {
		return stateData;
	}

	@Override
	public int getFuelAmount() {
		return stateData.waterTank.getFluidAmount();
	}

	@Override
	public void setFuelAmount(int newAmount) {
		if (stateData.waterTank.isEmpty()) {
			stateData.waterTank.setFluid(new FluidStack(Fluids.WATER, newAmount));
		}
		else {
			stateData.waterTank.getFluid().setAmount(newAmount);
		}
	}

	@Override
	public void addFuelAmount(int addAmount) {
		if (stateData.waterTank.isEmpty()) {
			if (addAmount > 0) stateData.waterTank.setFluid(new FluidStack(Fluids.WATER, addAmount));
		}
		else {
			stateData.waterTank.getFluid().grow(addAmount);
		}
	}

	@Override
	public int getMaxFuelAmount() {
		return MAX_FUEL;
	}

	@Override
	public int getFuelCost() {
		return FUEL_COST;
	}

	public static boolean isFluidValidFuel(FluidStack fluidStack) {
		return fluidStack.getFluid() == Fluids.WATER;
	}

	@Override
	public boolean isItemValidFuel(ItemStack stack) {
		return VALID_FUEL_ITEM.test(stack);
	}

	@Override
	public float getItemFuelValue(ItemStack stackIn) {
		return 1;
	}

	@Override
	public ItemStack getStackInFuelSlot() {
		return fuelContents.getStackInSlot(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		fuelContents.setInventorySlotContents(0, stack);
	}

	@Override
	protected boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft) {
		return outputContents.doesItemStackFit(0, stackToCraft);
	}

	@Override
	protected boolean doesFluidFitIntoOutputTank(FluidStack stackToCraft) {
		return stateData.outputTank.fill(stackToCraft, IFluidHandler.FluidAction.SIMULATE) == stackToCraft.getAmount();
	}

	@Override
	protected boolean craftRecipe(DigesterRecipe recipeToCraft, World world) {
		FluidStack result = recipeToCraft.getFluidResult();
		if (!result.isEmpty() && stateData.outputTank.fill(result, IFluidHandler.FluidAction.SIMULATE) == result.getAmount()) {
			for (int idx = 0; idx < inputContents.getSizeInventory(); idx++) {
				inputContents.decrStackSize(idx, 1);
			}
			stateData.outputTank.fill(result, IFluidHandler.FluidAction.EXECUTE);

			Byproduct byproduct = recipeToCraft.getByproduct();
			if (byproduct != null && world.rand.nextFloat() <= byproduct.getChance()) {
				ItemStack stack = byproduct.getItemStack();
				for (int idx = 0; idx < outputContents.getSizeInventory(); idx++) { //index 0 is reserved for the main crafting output
					stack = outputContents.insertItemStack(idx, stack); //update stack with remainder
					if (stack.isEmpty()) break;
				}
			}

			markDirty();
			return true;
		}
		return false;
	}

	@Override
	public void refuel() {
		int fluidAmount = stateData.waterTank.getFluidAmount();
		int maxFluidAmount = stateData.waterTank.getCapacity();
		if (fluidAmount < maxFluidAmount) {
			ItemStack stack = fuelContents.getStackInSlot(0);
			if (stack.isEmpty()) return;

			if (isItemValidFuel(stack)) {
				if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER) {
					ItemStack outStack = fuelOutContents.getStackInSlot(0);
					if (fluidAmount < maxFluidAmount - 333 && (outStack.isEmpty() || outStack.getItem() == Items.GLASS_BOTTLE)) {
						stateData.waterTank.fill(new FluidStack(Fluids.WATER, 334), IFluidHandler.FluidAction.EXECUTE);
						stack.shrink(1);
						if (outStack.isEmpty()) fuelOutContents.setInventorySlotContents(0, new ItemStack(Items.GLASS_BOTTLE));
						else outStack.grow(1);
						markDirty();
					}
				}
				else {
					FluidActionResult fluidAction = FluidUtil.tryEmptyContainerAndStow(stack, stateData.waterTank, fuelOutContents.getItemStackHandler(), maxFluidAmount - fluidAmount, null, true);
					if (fluidAction.isSuccess()) {
						fuelContents.setInventorySlotContents(0, fluidAction.getResult());
						markDirty();
					}
				}
			}
			else {
				ItemStack remainder = fuelOutContents.insertItemStack(0, stack);
				fuelContents.setInventorySlotContents(0, remainder);
				markDirty();
			}
		}
	}

	@Nullable
	@Override
	protected DigesterRecipe resolveRecipeFromInput(World world) {
		return RECIPE_TYPE.getRecipeFromInventory(world, inputContents).orElse(null);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "digester");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DigesterContainer.createServerContainer(screenId, playerInv, fuelContents, fuelOutContents, inputContents, outputContents, stateData);
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropInventoryItems(world, pos, fuelContents);
		InventoryHelper.dropInventoryItems(world, pos, fuelOutContents);
		InventoryHelper.dropInventoryItems(world, pos, inputContents);
		InventoryHelper.dropInventoryItems(world, pos, outputContents);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		stateData.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelContents.serializeNBT());
		nbt.put("FuelOutSlots", fuelOutContents.serializeNBT());
		nbt.put("InputSlots", inputContents.serializeNBT());
		nbt.put("OutputSlots", outputContents.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		stateData.deserializeNBT(nbt);
		fuelContents.deserializeNBT(nbt.getCompound("FuelSlots"));
		fuelOutContents.deserializeNBT(nbt.getCompound("FuelOutSlots"));
		inputContents.deserializeNBT(nbt.getCompound("InputSlots"));
		outputContents.deserializeNBT(nbt.getCompound("OutputSlots"));

		if (fuelContents.getSizeInventory() != FUEL_SLOTS_COUNT || fuelContents.getSizeInventory() != FUEL_OUT_SLOTS_COUNT || inputContents.getSizeInventory() != INPUT_SLOTS_COUNT || outputContents.getSizeInventory() != OUTPUT_SLOTS_COUNT) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public void invalidateCaps() {
		fuelContents.getOptionalItemStackHandler().invalidate();
		fuelOutContents.getOptionalItemStackHandler().invalidate();
		inputContents.getOptionalItemStackHandler().invalidate();
		outputContents.getOptionalItemStackHandler().invalidate();
		stateData.getOptionalInputFluidHandler().invalidate();
		stateData.getOptionalOutputFluidHandler().invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed)
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (side == Direction.UP) return inputContents.getOptionalItemStackHandler().cast();
				if (side == null || side == Direction.DOWN) return outputContents.getOptionalItemStackHandler().cast();
				return fuelContents.getOptionalItemStackHandler().cast();
			}
			else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				if (side == Direction.DOWN || side == Direction.UP) return stateData.getOptionalInputFluidHandler().cast();
				else return stateData.getOptionalOutputFluidHandler().cast();
			}
		return super.getCapability(cap, side);
	}
}
