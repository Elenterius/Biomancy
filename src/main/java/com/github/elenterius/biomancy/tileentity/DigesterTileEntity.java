package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.DigesterBlock;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.DigesterContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import com.github.elenterius.biomancy.mixin.RecipeManagerMixinAccessor;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DigesterRecipe;
import com.github.elenterius.biomancy.tileentity.state.CraftingState;
import com.github.elenterius.biomancy.tileentity.state.DigesterStateData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
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
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class DigesterTileEntity extends OwnableTileEntity implements INamedContainerProvider, ITickableTileEntity {

	public static final int FUEL_SLOTS_COUNT = 1;
	public static final int FUEL_OUT_SLOTS_COUNT = 1;
	public static final int INPUT_SLOTS_COUNT = 1;
	public static final int OUTPUT_SLOTS_COUNT = 2;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 2;

	private final DigesterStateData stateData = new DigesterStateData();
	private final SimpleInvContents fuelContents;
	private final SimpleInvContents fuelOutContents;
	private final SimpleInvContents inputContents;
	private final SimpleInvContents outputContents;

	public DigesterTileEntity() {
		super(ModTileEntityTypes.DIGESTER.get());
		fuelContents = SimpleInvContents.createServerContents(FUEL_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		fuelOutContents = SimpleInvContents.createServerContents(FUEL_OUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		inputContents = SimpleInvContents.createServerContents(INPUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		outputContents = SimpleInvContents.createServerContents(OUTPUT_SLOTS_COUNT, SimpleInvContents.ISHandlerType.NO_INSERT, this::canPlayerOpenInv, this::markDirty);
	}

	public static Optional<DigesterRecipe> getRecipeForItem(World world, ItemStack stackIn) {
		RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();

		return recipeManager.callGetRecipes(ModRecipes.DIGESTER_RECIPE_TYPE).values().stream().map((recipe) -> (DigesterRecipe) recipe)
				.filter(recipe -> {
					for (Ingredient ingredient : recipe.getIngredients()) {
						if (ingredient.test(stackIn)) return true;
					}
					return false;
				}).findFirst();
	}

	public static boolean areRecipesEqual(DigesterRecipe recipeA, DigesterRecipe recipeB, boolean relaxed) {
		boolean flag = recipeA.getId().equals(recipeB.getId());
		if (!relaxed && !ItemHandlerHelper.canItemStacksStack(recipeA.getRecipeOutput(), recipeB.getRecipeOutput())) {
			return false;
		}
		return flag;
	}

	public static Optional<DigesterRecipe> getRecipeForInput(World world, IInventory inputInv) {
		RecipeManager recipeManager = world.getRecipeManager();
		return recipeManager.getRecipe(ModRecipes.DIGESTER_RECIPE_TYPE, inputInv, world);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return BiomancyMod.getTranslationText("container", "digester");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DigesterContainer.createServerContainer(screenId, playerInv, fuelContents, fuelOutContents, inputContents, outputContents, stateData);
	}

	@Override
	public void tick() {
		if (world == null || world.isRemote) return;

		if (world.getGameTime() % 10L == 0L) {
			refuel();
		}

		DigesterRecipe recipeToCraft = getRecipeForInput(world, inputContents).orElse(null);
		if (recipeToCraft == null) {
			stateData.cancelCrafting();
		}
		else {
			ItemStack itemToCraft = recipeToCraft.getRecipeOutput(); // .copy()
			if (itemToCraft.isEmpty()) {
				stateData.cancelCrafting();
			}
			else {
				if (outputContents.doesItemStackFit(0, itemToCraft)) {
					if (stateData.getCraftingState() == CraftingState.NONE) {
						stateData.setCraftingState(CraftingState.IN_PROGRESS);
						stateData.clear(); //safe guard, shouldn't be needed
						stateData.setCraftingGoalRecipe(recipeToCraft); // this also sets the time required for crafting
					}
					else if (!stateData.isCraftingCanceled()) {
						DigesterRecipe recipeCraftingGoal = stateData.getCraftingGoalRecipe(world).orElse(null);
						if (recipeCraftingGoal == null || !areRecipesEqual(recipeToCraft, recipeCraftingGoal, true)) {
							stateData.cancelCrafting();
						}
					}
				}
				else {
					if (stateData.getCraftingState() != CraftingState.COMPLETED) stateData.cancelCrafting();
				}
			}

			//change crafting progress
			if (stateData.getCraftingState() == CraftingState.IN_PROGRESS) {
				if (consumeFuel()) stateData.timeElapsed += 1;
				else stateData.timeElapsed -= 2;

				if (stateData.timeElapsed < 0) stateData.timeElapsed = 0;
			}

			//craft items
			if (stateData.getCraftingState() == CraftingState.IN_PROGRESS || stateData.getCraftingState() == CraftingState.COMPLETED) {
				if (stateData.timeElapsed >= stateData.timeForCompletion) {
					stateData.setCraftingState(CraftingState.COMPLETED);
					if (craftItems(recipeToCraft, world.rand)) {
						stateData.setCraftingState(CraftingState.NONE);
					}
				}
			}
		}

		//clean-up states
		if (stateData.isCraftingCanceled()) {
			stateData.setCraftingState(CraftingState.NONE);
			stateData.clear();
		}
		else if (stateData.getCraftingState() == CraftingState.NONE) {
			stateData.clear();
		}

		BlockState oldBlockState = world.getBlockState(pos);
		BlockState newBlockState = oldBlockState.with(DigesterBlock.CRAFTING, stateData.getCraftingState() == CraftingState.IN_PROGRESS);
		if (!newBlockState.equals(oldBlockState)) {
			world.setBlockState(pos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
			markDirty();
		}
	}

	private boolean craftItems(DigesterRecipe recipeToCraft, Random rand) {
		ItemStack result = recipeToCraft.getCraftingResult(inputContents);
		if (!result.isEmpty() && outputContents.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputContents.getSizeInventory(); idx++) {
				inputContents.decrStackSize(idx, 1);
			}
			outputContents.insertItemStack(0, result);

			Byproduct byproduct = recipeToCraft.getByproduct();
			if (byproduct != null && rand.nextFloat() <= byproduct.getChance()) {
				ItemStack stack = byproduct.getItemStack();
				for (int idx = 1; idx < outputContents.getSizeInventory(); idx++) { //index 0 is reserved for the main crafting output
					stack = outputContents.insertItemStack(idx, stack); //update stack with remainder
					if (stack.isEmpty()) break;
				}
			}

			markDirty();
			return true;
		}
		return false;
	}

	private boolean consumeFuel() {
		if (stateData.fuel.getFluidAmount() >= FUEL_COST) {
			stateData.fuel.getFluid().shrink(FUEL_COST);
			return true;
		}
		return false;
	}

	public static boolean isFluidValidFuel(FluidStack fluidStack) {
		return fluidStack.getFluid() == Fluids.WATER;
	}

	public static boolean isItemValidFuel(ItemStack stack) {
		if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER) return true;
		return FluidUtil.getFluidContained(stack).map(DigesterTileEntity::isFluidValidFuel).orElse(false);
	}

	public void refuel() {
		int fluidAmount = stateData.fuel.getFluidAmount();
		int maxFluidAmount = stateData.fuel.getCapacity();
		if (fluidAmount < maxFluidAmount) {
			ItemStack stack = fuelContents.getStackInSlot(0);
			if (stack.isEmpty()) return;

			if (isItemValidFuel(stack)) {
				if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER) {
					ItemStack outStack = fuelOutContents.getStackInSlot(0);
					if (fluidAmount < maxFluidAmount - 333 && (outStack.isEmpty() || outStack.getItem() == Items.GLASS_BOTTLE)) {
						stateData.fuel.fill(new FluidStack(Fluids.WATER, 334), IFluidHandler.FluidAction.EXECUTE);
						stack.shrink(1);
						if (outStack.isEmpty()) fuelOutContents.setInventorySlotContents(0, new ItemStack(Items.GLASS_BOTTLE));
						else outStack.grow(1);
						markDirty();
					}
				}
				else {
					FluidActionResult fluidAction = FluidUtil.tryEmptyContainerAndStow(stack, stateData.fuel, fuelOutContents.getItemStackHandler(), maxFluidAmount - fluidAmount, null, true);
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
				return stateData.getOptionalFluidHandler().cast();
			}
		return super.getCapability(cap, side);
	}
}
