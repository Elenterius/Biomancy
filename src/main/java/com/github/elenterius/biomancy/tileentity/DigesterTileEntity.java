package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.block.DigesterBlock;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.DigesterContainer;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.recipe.RecipeType;
import com.github.elenterius.biomancy.tileentity.state.DigesterStateData;
import com.github.elenterius.biomancy.util.TextUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class DigesterTileEntity extends MachineTileEntity<DigesterRecipe, DigesterStateData> {

	public static final int FUEL_SLOTS = 1;
	public static final int EMPTY_BUCKET_SLOTS = 1;
	public static final int BUCKET_SLOTS = 1;
	public static final int INPUT_SLOTS = 1;
	public static final int OUTPUT_SLOTS = 1;

	public static final int MAX_FLUID = 32_000;
	public static final short FUEL_COST = 1;
	public static final Predicate<ItemStack> VALID_FUEL_ITEM = stack -> {
		if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER) return true;
		return FluidUtil.getFluidContained(stack).map(DigesterTileEntity::isFluidValidFuel).orElse(false);
	};
	public static final RecipeType.ItemStackRecipeType<DigesterRecipe> RECIPE_TYPE = ModRecipes.DIGESTER_RECIPE_TYPE;

	private final DigesterStateData stateData = new DigesterStateData();
	private final SimpleInventory fuelInventory;
	private final SimpleInventory emptyBucketOutInventory;
	private final SimpleInventory inputInventory;
	private final SimpleInventory outputInventory;
	private final SimpleInventory emptyBucketInInventory;
	private final SimpleInventory filledBucketOutInventory;

	public DigesterTileEntity() {
		super(ModTileEntityTypes.DIGESTER.get());
		fuelInventory = SimpleInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterBiofuel, this::canPlayerOpenInv, this::markDirty);
		emptyBucketOutInventory = SimpleInventory.createServerContents(EMPTY_BUCKET_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::markDirty);
		inputInventory = SimpleInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::markDirty);
		outputInventory = SimpleInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::markDirty);
		emptyBucketInInventory = SimpleInventory.createServerContents(BUCKET_SLOTS, HandlerBehaviors::filterFluidContainer, this::canPlayerOpenInv, this::markDirty);
		filledBucketOutInventory = SimpleInventory.createServerContents(BUCKET_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::markDirty);
	}

	@Override
	protected DigesterStateData getStateData() {
		return stateData;
	}

	@Override
	public int getFuelAmount() {
		return stateData.fuelTank.getFluidAmount();
	}

	@Override
	public void setFuelAmount(int newAmount) {
		if (stateData.fuelTank.isEmpty()) {
			stateData.fuelTank.setFluid(new FluidStack(Fluids.WATER, newAmount));
		}
		else {
			stateData.fuelTank.getFluid().setAmount(newAmount);
		}
	}

	@Override
	public void addFuelAmount(int addAmount) {
		if (stateData.fuelTank.isEmpty()) {
			if (addAmount > 0) stateData.fuelTank.setFluid(new FluidStack(Fluids.WATER, addAmount));
		}
		else {
			stateData.fuelTank.getFluid().grow(addAmount);
		}
	}

	@Override
	public int getMaxFuelAmount() {
		return MAX_FLUID;
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
		return fuelInventory.getStackInSlot(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		fuelInventory.setInventorySlotContents(0, stack);
	}

	@Override
	protected boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft) {
		return outputInventory.doesItemStackFit(0, stackToCraft);
	}

	@Override
	protected boolean doesFluidFitIntoOutputTank(FluidStack stackToCraft) {
		return stateData.outputTank.fill(stackToCraft, IFluidHandler.FluidAction.SIMULATE) == stackToCraft.getAmount();
	}

	@Override
	public void tick() {
		if (world != null && !world.isRemote) {
			if (ticks % 8 == 0) {
				fillFluidContainerItem();
			}

			if (ticks % 42 == 0) {
				tryToGetItemsFromAttachedBlock(world);
			}
			if (ticks % 60 == 0) {
				tryToSuckWater(world);
			}
		}

		super.tick();
	}

	@Nullable
	@Override
	protected DigesterRecipe resolveRecipeFromInput(World world) {
		return RECIPE_TYPE.getRecipeFromInventory(world, inputInventory).orElse(null);
	}

	@Override
	protected boolean craftRecipe(DigesterRecipe recipeToCraft, World world) {
		FluidStack result = recipeToCraft.getFluidResult();
		if (!result.isEmpty() && stateData.outputTank.fill(result, IFluidHandler.FluidAction.SIMULATE) == result.getAmount()) {
			for (int idx = 0; idx < inputInventory.getSizeInventory(); idx++) {
				inputInventory.decrStackSize(idx, 1);
			}
			stateData.outputTank.fill(result, IFluidHandler.FluidAction.EXECUTE);
			tryToInjectFluid(world);

			Byproduct byproduct = recipeToCraft.getByproduct();
			if (byproduct != null && world.rand.nextFloat() <= byproduct.getChance()) {
				ItemStack stack = byproduct.getItemStack();
				for (int idx = 0; idx < outputInventory.getSizeInventory(); idx++) { //index 0 is reserved for the main crafting output
					stack = outputInventory.insertItemStack(idx, stack); //update stack with remainder
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
		int fluidAmount = stateData.fuelTank.getFluidAmount();
		int maxFluidAmount = stateData.fuelTank.getCapacity();
		if (fluidAmount < maxFluidAmount) {
			ItemStack stack = fuelInventory.getStackInSlot(0);
			if (stack.isEmpty()) return;

			if (isItemValidFuel(stack)) {
				if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER) {
					ItemStack outStack = emptyBucketOutInventory.getStackInSlot(0);
					if (fluidAmount < maxFluidAmount - 333 && (outStack.isEmpty() || outStack.getItem() == Items.GLASS_BOTTLE)) {
						stateData.fuelTank.fill(new FluidStack(Fluids.WATER, 334), IFluidHandler.FluidAction.EXECUTE);
						stack.shrink(1);
						if (outStack.isEmpty()) emptyBucketOutInventory.setInventorySlotContents(0, new ItemStack(Items.GLASS_BOTTLE));
						else outStack.grow(1);
						markDirty();
					}
				}
				else {
					FluidActionResult fluidAction = FluidUtil.tryEmptyContainerAndStow(stack, stateData.fuelTank, emptyBucketOutInventory.getItemStackHandler(), maxFluidAmount - fluidAmount, null, true);
					if (fluidAction.isSuccess()) {
						fuelInventory.setInventorySlotContents(0, fluidAction.getResult());
						markDirty();
					}
				}
			}
			else {
				ItemStack remainder = emptyBucketOutInventory.insertItemStack(0, stack);
				fuelInventory.setInventorySlotContents(0, remainder);
				markDirty();
			}
		}
	}

	public void fillFluidContainerItem() {
		if (stateData.outputTank.getFluidAmount() > 0) {
			ItemStack stack = emptyBucketInInventory.getStackInSlot(0);
			if (stack.isEmpty()) return;

			if (HandlerBehaviors.FLUID_CONTAINER_ITEM_PREDICATE.test(stack)) {
				FluidActionResult fluidAction = FluidUtil.tryFillContainerAndStow(stack, stateData.outputTank, filledBucketOutInventory.getItemStackHandler(), MAX_FLUID, null, true);
				if (fluidAction.isSuccess()) {
					emptyBucketInInventory.setInventorySlotContents(0, fluidAction.getResult());
				}
				else if (FluidUtil.getFluidContained(stack).isPresent()) {
					ItemStack remainder = filledBucketOutInventory.insertItemStack(0, stack);
					emptyBucketInInventory.setInventorySlotContents(0, remainder);
				}
				markDirty();
			}
		}
	}
	
	protected void tryToSuckWater(World world) {
		int fluidAmount = stateData.fuelTank.getFluidAmount();
		int maxFluidAmount = stateData.fuelTank.getCapacity();
		if (fluidAmount <= maxFluidAmount-1000) {
			Direction direction = getBlockState().get(DigesterBlock.FACING);
			BlockPos blockPos = getPos().offset(direction);
			Block neighbourBlock = world.getBlockState(blockPos).getBlock();
			if (neighbourBlock == Blocks.WET_SPONGE) {
				world.setBlockState(blockPos, Blocks.SPONGE.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
				addFuelAmount(1000);
			}
		}
	}
	
	protected void tryToInjectFluid(World world) {
		Direction direction = getBlockState().get(DigesterBlock.FACING).getOpposite();
		BlockPos blockPos = getPos().offset(direction);
		LazyOptional<IFluidHandler> capability = FluidUtil.getFluidHandler(world, blockPos, Direction.UP);
		capability.ifPresent(fluidHandler -> {
			FluidStack fluidStack = FluidUtil.tryFluidTransfer(fluidHandler, stateData.outputTank, 1000, true);
		});
	}
	
	protected void tryToGetItemsFromAttachedBlock(World world) {
		Direction direction = getBlockState().get(DigesterBlock.FACING).getOpposite();
		BlockPos blockPos = getPos().offset(direction);
		int maxAmount = inputInventory.getItemStackHandler().getSlotLimit(0);
		ItemStack storedStack = inputInventory.getStackInSlot(0);
		int oldAmount = storedStack.getCount();
		if (oldAmount < maxAmount / 2) {
			TileEntity tile = world.getTileEntity(blockPos);
			if (tile != null) {
				LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN);
				if (capability.isPresent()) {
					capability.ifPresent(itemHandler -> {
						int amount = oldAmount;
						int nSlots = itemHandler.getSlots();
						for (int i = 0; i < nSlots; i++) {
							if (!storedStack.isEmpty()) {
								if (!ItemHandlerHelper.canItemStacksStack(itemHandler.getStackInSlot(i), storedStack)) continue;
							}
							else {
								ItemStack foundStack = itemHandler.getStackInSlot(i);
								if (foundStack.getItem() != ModItems.BOLUS.get() && !ModTags.Items.BIOMASS.contains(foundStack.getItem())) continue;
							}

							int extractAmount = Math.min(itemHandler.getSlotLimit(i), maxAmount - amount);
							ItemStack result = itemHandler.extractItem(i, extractAmount, false);
							if (!result.isEmpty()) {
								amount += result.getCount();
								inputInventory.insertItemStack(0, result.copy());
							}
							if (amount >= maxAmount) break;
						}
						if (amount != oldAmount) {
							markDirty();
							world.playSound(null, getPos(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.9F, 0.3f + world.rand.nextFloat() * 0.25f);
						}
					});
				}
			}
		}
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "digester");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DigesterContainer.createServerContainer(screenId, playerInv, fuelInventory, emptyBucketOutInventory, inputInventory, outputInventory, emptyBucketInInventory, filledBucketOutInventory, stateData);
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropInventoryItems(world, pos, fuelInventory);
		InventoryHelper.dropInventoryItems(world, pos, emptyBucketOutInventory);
		InventoryHelper.dropInventoryItems(world, pos, inputInventory);
		InventoryHelper.dropInventoryItems(world, pos, outputInventory);
		InventoryHelper.dropInventoryItems(world, pos, emptyBucketInInventory);
		InventoryHelper.dropInventoryItems(world, pos, filledBucketOutInventory);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		stateData.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelInventory.serializeNBT());
		nbt.put("EmptyBucketOutSlots", emptyBucketOutInventory.serializeNBT());
		nbt.put("InputSlots", inputInventory.serializeNBT());
		nbt.put("OutputSlots", outputInventory.serializeNBT());
		nbt.put("EmptyBucketInSlots", emptyBucketInInventory.serializeNBT());
		nbt.put("FilledBucketOutSlots", filledBucketOutInventory.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		stateData.deserializeNBT(nbt);
		fuelInventory.deserializeNBT(nbt.getCompound("FuelSlots"));
		emptyBucketOutInventory.deserializeNBT(nbt.getCompound("EmptyBucketOutSlots"));
		inputInventory.deserializeNBT(nbt.getCompound("InputSlots"));
		outputInventory.deserializeNBT(nbt.getCompound("OutputSlots"));
		emptyBucketInInventory.deserializeNBT(nbt.getCompound("EmptyBucketInSlots"));
		filledBucketOutInventory.deserializeNBT(nbt.getCompound("FilledBucketOutSlots"));

		if (fuelInventory.getSizeInventory() != FUEL_SLOTS || fuelInventory.getSizeInventory() != EMPTY_BUCKET_SLOTS
				|| inputInventory.getSizeInventory() != INPUT_SLOTS || outputInventory.getSizeInventory() != OUTPUT_SLOTS
				|| emptyBucketInInventory.getSizeInventory() != BUCKET_SLOTS || filledBucketOutInventory.getSizeInventory() != BUCKET_SLOTS) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public void invalidateCaps() {
		fuelInventory.getOptionalItemStackHandler().invalidate();
		emptyBucketOutInventory.getOptionalItemStackHandler().invalidate();
		inputInventory.getOptionalItemStackHandler().invalidate();
		outputInventory.getOptionalItemStackHandler().invalidate();
		emptyBucketInInventory.getOptionalItemStackHandler().invalidate();
		filledBucketOutInventory.getOptionalItemStackHandler().invalidate();
		stateData.getCombinedFluidHandlers().invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed) {
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (side == Direction.UP) return inputInventory.getOptionalItemStackHandler().cast();
				if (side == null || side == Direction.DOWN) return outputInventory.getOptionalItemStackHandler().cast();
				return fuelInventory.getOptionalItemStackHandler().cast();
			}
			else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return stateData.getCombinedFluidHandlers().cast();
			}
		}
		return super.getCapability(cap, side);
	}

}
