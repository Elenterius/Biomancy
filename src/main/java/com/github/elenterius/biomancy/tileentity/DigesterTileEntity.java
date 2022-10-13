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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

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
		if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) return true;
		return FluidUtil.getFluidContained(stack).map(DigesterTileEntity::isFluidValidFuel).orElse(false);
	};
	public static final RecipeType.ItemStackRecipeType<DigesterRecipe> RECIPE_TYPE = ModRecipes.DIGESTER_RECIPE_TYPE;

	private final DigesterStateData stateData = new DigesterStateData();
	private final SimpleInventory<?> fuelInventory;
	private final SimpleInventory<?> inputInventory;
	private final SimpleInventory<?> emptyBucketInInventory;

	private final SimpleInventory<?> emptyBucketOutInventory;
	private final SimpleInventory<?> filledBucketOutInventory;
	private final SimpleInventory<?> outputInventory;
	private final LazyOptional<CombinedInvWrapper> combinedOutputInventory;

	public DigesterTileEntity() {
		super(ModTileEntityTypes.DIGESTER.get());
		fuelInventory = SimpleInventory.createServerContents(FUEL_SLOTS, ish -> HandlerBehaviors.filterInput(ish, VALID_FUEL_ITEM), this::canPlayerOpenInv, this::setChanged);
		inputInventory = SimpleInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		emptyBucketInInventory = SimpleInventory.createServerContents(BUCKET_SLOTS, HandlerBehaviors::filterFluidContainer, this::canPlayerOpenInv, this::setChanged);

		filledBucketOutInventory = SimpleInventory.createServerContents(BUCKET_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
		emptyBucketOutInventory = SimpleInventory.createServerContents(EMPTY_BUCKET_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
		outputInventory = SimpleInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
		combinedOutputInventory = LazyOptional.of(() -> new CombinedInvWrapper(filledBucketOutInventory.getItemHandlerWithBehavior(), emptyBucketOutInventory.getItemHandlerWithBehavior(), outputInventory.getItemHandlerWithBehavior()));
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
	protected boolean doesFluidFitIntoOutputTank(FluidStack stackToCraft) {
		return stateData.outputTank.fill(stackToCraft, IFluidHandler.FluidAction.SIMULATE) == stackToCraft.getAmount();
	}

	@Override
	public void tick() {
		if (level != null && !level.isClientSide) {
			if (ticks % 8 == 0) {
				fillFluidContainerItem();
			}

			if (ticks % 42 == 0) {
				tryToGetItemsFromAttachedBlock(level);
			}
//			if (ticks % 60 == 0) {
//				tryToSuckWater(level);
//			}
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
			for (int idx = 0; idx < inputInventory.getContainerSize(); idx++) {
				inputInventory.removeItem(idx, 1);
			}
			stateData.outputTank.fill(result, IFluidHandler.FluidAction.EXECUTE);
			tryToInjectFluid(world);

			Byproduct byproduct = recipeToCraft.getByproduct();
			if (byproduct != null && world.random.nextFloat() <= byproduct.getChance()) {
				ItemStack stack = byproduct.getItemStack();
				for (int idx = 0; idx < outputInventory.getContainerSize(); idx++) { //index 0 is reserved for the main crafting output
					stack = outputInventory.insertItemStack(idx, stack); //update stack with remainder
					if (stack.isEmpty()) break;
				}
			}

			setChanged();
			return true;
		}
		return false;
	}

	@Override
	public void refuel() {
		int fluidAmount = stateData.fuelTank.getFluidAmount();
		int maxFluidAmount = stateData.fuelTank.getCapacity();
		if (fluidAmount < maxFluidAmount) {
			ItemStack stack = fuelInventory.getItem(0);
			if (stack.isEmpty()) return;

			if (isItemValidFuel(stack)) {
				if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER) {
					ItemStack outStack = emptyBucketOutInventory.getItem(0);
					if (fluidAmount < maxFluidAmount - 333 && (outStack.isEmpty() || outStack.getItem() == Items.GLASS_BOTTLE)) {
						stateData.fuelTank.fill(new FluidStack(Fluids.WATER, 334), IFluidHandler.FluidAction.EXECUTE);
						stack.shrink(1);
						if (outStack.isEmpty()) emptyBucketOutInventory.setItem(0, new ItemStack(Items.GLASS_BOTTLE));
						else outStack.grow(1);
						setChanged();
					}
				}
				else {
					FluidActionResult fluidAction = FluidUtil.tryEmptyContainerAndStow(stack, stateData.fuelTank, emptyBucketOutInventory.getItemHandler(), maxFluidAmount - fluidAmount, null, true);
					if (fluidAction.isSuccess()) {
						fuelInventory.setItem(0, fluidAction.getResult());
						setChanged();
					}
				}
			}
			else {
				ItemStack remainder = emptyBucketOutInventory.insertItemStack(0, stack);
				fuelInventory.setItem(0, remainder);
				setChanged();
			}
		}
	}

	public void fillFluidContainerItem() {
		if (stateData.outputTank.getFluidAmount() > 0) {
			ItemStack stack = emptyBucketInInventory.getItem(0);
			if (stack.isEmpty()) return;

			if (HandlerBehaviors.FLUID_CONTAINER_ITEM_PREDICATE.test(stack)) {
				FluidActionResult fluidAction = FluidUtil.tryFillContainerAndStow(stack, stateData.outputTank, filledBucketOutInventory.getItemHandler(), MAX_FLUID, null, true);
				if (fluidAction.isSuccess()) {
					emptyBucketInInventory.setItem(0, fluidAction.getResult());
				}
				else if (FluidUtil.getFluidContained(stack).isPresent()) {
					ItemStack remainder = filledBucketOutInventory.insertItemStack(0, stack);
					emptyBucketInInventory.setItem(0, remainder);
				}
				setChanged();
			}
		}
	}

	protected void tryToSuckWater(World world) {
		int fluidAmount = stateData.fuelTank.getFluidAmount();
		int maxFluidAmount = stateData.fuelTank.getCapacity();
		if (fluidAmount <= maxFluidAmount - 1000) {
			Direction direction = getBlockState().getValue(DigesterBlock.FACING);
			if (world.getBlockState(getBlockPos().relative(direction.getOpposite())).getBlock() == Blocks.WATER) {
				addFuelAmount(1000);
			}
			else if (world.getBlockState(getBlockPos().relative(direction)).getBlock() == Blocks.WATER) {
				addFuelAmount(1000);
			}
		}
	}

	protected void tryToInjectFluid(World world) {
		Direction direction = getFacingDirection().getOpposite();
		BlockPos blockPos = getBlockPos().relative(direction);
		LazyOptional<IFluidHandler> capability = FluidUtil.getFluidHandler(world, blockPos, Direction.UP);
		capability.ifPresent(fluidHandler -> {
			FluidStack fluidStack = FluidUtil.tryFluidTransfer(fluidHandler, stateData.outputTank, 1000, true);
		});
	}

	protected void tryToGetItemsFromAttachedBlock(World world) {
		Direction direction = getFacingDirection().getOpposite();
		BlockPos blockPos = getBlockPos().relative(direction);
		int maxAmount = inputInventory.getItemHandler().getSlotLimit(0);
		ItemStack storedStack = inputInventory.getItem(0);
		int oldAmount = storedStack.getCount();
		if (oldAmount < maxAmount / 2) {
			TileEntity tile = world.getBlockEntity(blockPos);
			if (tile != null) {
				LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN);
				if (capability.isPresent()) {
					capability.ifPresent(itemHandler -> {
						int amount = oldAmount;
						int nSlots = itemHandler.getSlots();
						for (int i = 0; i < nSlots; i++) {
							ItemStack stackInSlot = itemHandler.getStackInSlot(i);
							if (!stackInSlot.isEmpty()) {
								if (!storedStack.isEmpty()) {
									if (!ItemHandlerHelper.canItemStacksStack(stackInSlot, storedStack)) continue;
								}
								else {
									if (!isValidInputItem(stackInSlot.getItem())) continue;
								}

								int extractAmount = Math.min(itemHandler.getSlotLimit(i), maxAmount - amount);
								ItemStack result = itemHandler.extractItem(i, extractAmount, false);
								if (!result.isEmpty()) {
									amount += result.getCount();
									inputInventory.insertItemStack(0, result.copy());
								}
								if (amount >= maxAmount) break;
							}
						}

						if (amount != oldAmount) {
							setChanged();
							world.playSound(null, getBlockPos(), SoundEvents.GENERIC_EAT, SoundCategory.PLAYERS, 0.9F, 0.3f + world.random.nextFloat() * 0.25f);
						}
					});
				}
			}
		}
	}

	private Direction getFacingDirection() {
		return getBlockState().getValue(DigesterBlock.FACING);
	}

	private boolean isValidInputItem(Item item) {
		if (item == ModItems.BOLUS.get()) return true;
		if (ModTags.Items.isRawMeat(item)) return true;
		if (item.is(ModTags.Items.COOKED_MEATS)) return true;
		return item.is(ModTags.Items.BIOMASS);
	}

	@Override
	public ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "digester");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DigesterContainer.createServerContainer(screenId, playerInv, fuelInventory, emptyBucketOutInventory, inputInventory, outputInventory, emptyBucketInInventory, filledBucketOutInventory, stateData);
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropContents(world, pos, fuelInventory);
		InventoryHelper.dropContents(world, pos, emptyBucketOutInventory);
		InventoryHelper.dropContents(world, pos, inputInventory);
		InventoryHelper.dropContents(world, pos, outputInventory);
		InventoryHelper.dropContents(world, pos, emptyBucketInInventory);
		InventoryHelper.dropContents(world, pos, filledBucketOutInventory);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
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
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		stateData.deserializeNBT(nbt);
		fuelInventory.deserializeNBT(nbt.getCompound("FuelSlots"));
		emptyBucketOutInventory.deserializeNBT(nbt.getCompound("EmptyBucketOutSlots"));
		inputInventory.deserializeNBT(nbt.getCompound("InputSlots"));
		outputInventory.deserializeNBT(nbt.getCompound("OutputSlots"));
		emptyBucketInInventory.deserializeNBT(nbt.getCompound("EmptyBucketInSlots"));
		filledBucketOutInventory.deserializeNBT(nbt.getCompound("FilledBucketOutSlots"));

		if (fuelInventory.getContainerSize() != FUEL_SLOTS || fuelInventory.getContainerSize() != EMPTY_BUCKET_SLOTS
				|| inputInventory.getContainerSize() != INPUT_SLOTS || outputInventory.getContainerSize() != OUTPUT_SLOTS
				|| emptyBucketInInventory.getContainerSize() != BUCKET_SLOTS || filledBucketOutInventory.getContainerSize() != BUCKET_SLOTS) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public void invalidateCaps() {
		fuelInventory.getOptionalItemHandlerWithBehavior().invalidate();
		emptyBucketOutInventory.getOptionalItemHandlerWithBehavior().invalidate();
		inputInventory.getOptionalItemHandlerWithBehavior().invalidate();
		outputInventory.getOptionalItemHandlerWithBehavior().invalidate();
		emptyBucketInInventory.getOptionalItemHandlerWithBehavior().invalidate();
		filledBucketOutInventory.getOptionalItemHandlerWithBehavior().invalidate();
		stateData.getCombinedFluidHandlers().invalidate();
		combinedOutputInventory.invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove) {
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (side == null || side == Direction.DOWN) return combinedOutputInventory.cast();

				Direction.Axis axis = getFacingDirection().getAxis();
				if (axis.isHorizontal()) {
					if (side.getAxis() == axis) return inputInventory.getOptionalItemHandlerWithBehavior().cast();
				}
				else if (side == Direction.UP) return inputInventory.getOptionalItemHandlerWithBehavior().cast();
				return fuelInventory.getOptionalItemHandlerWithBehavior().cast();
			}
			else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return stateData.getCombinedFluidHandlers().cast();
			}
		}
		return super.getCapability(cap, side);
	}

}
