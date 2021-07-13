package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.block.DigesterBlock;
import com.github.elenterius.biomancy.fluid.simibubi.FluidIngredient;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.inventory.SolidifierContainer;
import com.github.elenterius.biomancy.recipe.RecipeType;
import com.github.elenterius.biomancy.recipe.SolidifierRecipe;
import com.github.elenterius.biomancy.tileentity.state.SolidifierStateData;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SolidifierTileEntity extends MachineTileEntity<SolidifierRecipe, SolidifierStateData> {

	public static final int FILLED_BUCKET_SLOTS = 1;
	public static final int EMPTY_BUCKET_SLOTS = 1;
	public static final int OUTPUT_SLOTS = 1;

	public static final int MAX_FLUID = 32_000;
	public static final RecipeType.FluidStackRecipeType<SolidifierRecipe> RECIPE_TYPE = ModRecipes.SOLIDIFIER_RECIPE_TYPE;
	public final LazyOptional<IItemHandler> combinedInventory;
	private final SolidifierStateData stateData = new SolidifierStateData();
	private final SimpleInventory filledBucketInventory;
	private final SimpleInventory emptyBucketInventory;
	private final SimpleInventory outputInventory;

	public SolidifierTileEntity() {
		super(ModTileEntityTypes.SOLIDIFIER.get());
		filledBucketInventory = SimpleInventory.createServerContents(FILLED_BUCKET_SLOTS, HandlerBehaviors::filterFilledFluidContainer, this::canPlayerOpenInv, this::markDirty);
		emptyBucketInventory = SimpleInventory.createServerContents(EMPTY_BUCKET_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::markDirty);
		outputInventory = SimpleInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::markDirty);
		combinedInventory = LazyOptional.of(() -> new CombinedInvWrapper(filledBucketInventory.getItemStackHandler(), emptyBucketInventory.getItemStackHandler(), outputInventory.getItemStackHandler()));
	}

	@Override
	protected SolidifierStateData getStateData() {
		return stateData;
	}

	@Override
	public int getFuelAmount() {
		return stateData.inputTank.getFluidAmount();
	}

	@Override
	public void setFuelAmount(int newAmount) {
		if (stateData.inputTank.isEmpty()) {
			stateData.inputTank.setFluid(new FluidStack(ModFluids.NUTRIENT_SLURRY.get(), newAmount));
		}
		else {
			stateData.inputTank.getFluid().setAmount(newAmount);
		}
	}

	@Override
	public void addFuelAmount(int addAmount) {
		if (stateData.inputTank.isEmpty()) {
			if (addAmount > 0) setFuelAmount(addAmount);
		}
		else {
			stateData.inputTank.getFluid().grow(addAmount);
		}
	}

	@Override
	public int getMaxFuelAmount() {
		return MAX_FLUID;
	}

	@Override
	public int getFuelCost() {
		return 0;
	}

	@Override
	public boolean isItemValidFuel(ItemStack stack) {
		return HandlerBehaviors.FILLED_FLUID_ITEM_PREDICATE.test(stack);
	}

	@Override
	public float getItemFuelValue(ItemStack stack) {
		return 1;
	}

	@Override
	public ItemStack getStackInFuelSlot() {
		return filledBucketInventory.getStackInSlot(0);
	}

	@Override
	public void setStackInFuelSlot(ItemStack stack) {
		filledBucketInventory.setInventorySlotContents(0, stack);
	}

	@Override
	protected boolean doesItemFitIntoOutputInventory(ItemStack stackToCraft) {
		return outputInventory.doesItemStackFit(0, stackToCraft);
	}

	@Override
	public void tick() {
		if (world != null && !world.isRemote) {
			if (ticks % 42 == 0) {
				tryToGetFluidFromAttachedBlock(world);
			}
		}

		super.tick();
	}

	@Override
	protected boolean craftRecipe(SolidifierRecipe recipeToCraft, World world) {
		ItemStack result = recipeToCraft.getFluidCraftingResult();
		if (!result.isEmpty() && outputInventory.doesItemStackFit(0, result)) {
			FluidIngredient ingredient = recipeToCraft.getFluidIngredient();
			int requiredAmount = ingredient.getRequiredAmount();
			FluidStack drained = stateData.inputTank.drain(requiredAmount, IFluidHandler.FluidAction.SIMULATE);
			if (ingredient.test(drained) && drained.getAmount() == requiredAmount) {
				stateData.inputTank.drain(requiredAmount, IFluidHandler.FluidAction.EXECUTE);
				outputInventory.insertItemStack(0, result);
				markDirty();
				return true;
			}
		}
		return false;
	}

	@Override
	public void refuel() {
		int fluidAmount = stateData.inputTank.getFluidAmount();
		int maxFluidAmount = stateData.inputTank.getCapacity();
		if (fluidAmount < maxFluidAmount) {
			ItemStack stack = filledBucketInventory.getStackInSlot(0);
			if (stack.isEmpty()) return;

			if (HandlerBehaviors.FILLED_FLUID_ITEM_PREDICATE.test(stack)) {
				FluidActionResult fluidAction = FluidUtil.tryEmptyContainerAndStow(stack, stateData.inputTank, emptyBucketInventory.getItemStackHandler(), maxFluidAmount - fluidAmount, null, true);
				if (fluidAction.isSuccess()) {
					filledBucketInventory.setInventorySlotContents(0, fluidAction.getResult());
					markDirty();
				}
			}
			else {
				ItemStack remainder = emptyBucketInventory.insertItemStack(0, stack);
				filledBucketInventory.setInventorySlotContents(0, remainder);
				markDirty();
			}
		}
	}

	@Nullable
	@Override
	protected SolidifierRecipe resolveRecipeFromInput(World world) {
		return RECIPE_TYPE.getRecipeFromFluidTank(world, stateData.inputTank).orElse(null);
	}

	protected void tryToGetFluidFromAttachedBlock(World world) {
		Direction direction = getBlockState().get(DigesterBlock.FACING).getOpposite();
		BlockPos blockPos = getPos().offset(direction);
		LazyOptional<IFluidHandler> capability = FluidUtil.getFluidHandler(world, blockPos, Direction.DOWN);
		capability.ifPresent(fluidHandler -> {
			FluidStack fluidStack = FluidUtil.tryFluidTransfer(stateData.inputTank, fluidHandler, 1000, true);
			if (!fluidStack.isEmpty()) {
				world.playSound(null, getPos(), SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK, SoundCategory.PLAYERS, 0.9F, 0.3f + world.rand.nextFloat() * 0.25f);
			}
		});
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropInventoryItems(world, pos, filledBucketInventory);
		InventoryHelper.dropInventoryItems(world, pos, emptyBucketInventory);
		InventoryHelper.dropInventoryItems(world, pos, outputInventory);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "solidifier");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return SolidifierContainer.createServerContainer(screenId, playerInv, filledBucketInventory, emptyBucketInventory, outputInventory, stateData);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		stateData.serializeNBT(nbt);
		nbt.put("FilledBucketSlots", filledBucketInventory.serializeNBT());
		nbt.put("EmptyBucketSlots", emptyBucketInventory.serializeNBT());
		nbt.put("OutputSlots", outputInventory.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		stateData.deserializeNBT(nbt);
		filledBucketInventory.deserializeNBT(nbt.getCompound("FilledBucketSlots"));
		emptyBucketInventory.deserializeNBT(nbt.getCompound("EmptyBucketSlots"));
		outputInventory.deserializeNBT(nbt.getCompound("OutputSlots"));

		if (filledBucketInventory.getSizeInventory() != FILLED_BUCKET_SLOTS || emptyBucketInventory.getSizeInventory() != EMPTY_BUCKET_SLOTS || outputInventory.getSizeInventory() != OUTPUT_SLOTS) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public void invalidateCaps() {
		filledBucketInventory.getOptionalItemStackHandler().invalidate();
		emptyBucketInventory.getOptionalItemStackHandler().invalidate();
		outputInventory.getOptionalItemStackHandler().invalidate();
		stateData.getOptionalInputFluidHandler().invalidate();
		combinedInventory.invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed)
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (side == Direction.DOWN) return outputInventory.getOptionalItemStackHandler().cast();
				return combinedInventory.cast();
			}
			else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return stateData.getOptionalInputFluidHandler().cast();
			}
		return super.getCapability(cap, side);
	}

}
