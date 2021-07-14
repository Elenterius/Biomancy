package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.DecomposerContainer;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.RecipeType;
import com.github.elenterius.biomancy.tileentity.state.DecomposerStateData;
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

public class DecomposerTileEntity extends BFMachineTileEntity<DecomposerRecipe, DecomposerStateData> {

	public static final int FUEL_SLOTS = 1;
	public static final int EMPTY_BUCKET_SLOTS = 1;
	public static final int INPUT_SLOTS = DecomposerRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS = 1 + DecomposerRecipe.MAX_BYPRODUCTS;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 5;
	public static final RecipeType.ItemStackRecipeType<DecomposerRecipe> RECIPE_TYPE = ModRecipes.DECOMPOSING_RECIPE_TYPE;

	private final SimpleInventory fuelInventory;
	private final SimpleInventory emptyBucketInventory;
	private final SimpleInventory inputInventory;
	private final SimpleInventory outputInventory;
	private final DecomposerStateData stateData = new DecomposerStateData();

	public DecomposerTileEntity() {
		super(ModTileEntityTypes.DECOMPOSER.get());
		fuelInventory = SimpleInventory.createServerContents(FUEL_SLOTS, HandlerBehaviors::filterBiofuel, this::canPlayerOpenInv, this::markDirty);
		emptyBucketInventory = SimpleInventory.createServerContents(EMPTY_BUCKET_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::markDirty);
		inputInventory = SimpleInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::markDirty);
		outputInventory = SimpleInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::markDirty);
	}

	@Override
	protected DecomposerStateData getStateData() {
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
	protected boolean craftRecipe(DecomposerRecipe recipeToCraft, World world) {
		ItemStack result = recipeToCraft.getCraftingResult(inputInventory);
		if (!result.isEmpty() && outputInventory.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputInventory.getSizeInventory(); idx++) {
				inputInventory.decrStackSize(idx, recipeToCraft.getIngredientCount()); //consume input
			}

			outputInventory.insertItemStack(0, result); //output result

			//output optional byproducts
			for (Byproduct byproduct : recipeToCraft.getByproducts()) {
				if (world.rand.nextFloat() <= byproduct.getChance()) {
					ItemStack stack = byproduct.getItemStack();
					for (int idx = 1; idx < outputInventory.getSizeInventory(); idx++) { //index 0 is reserved for the main crafting output
						stack = outputInventory.insertItemStack(idx, stack); //update stack with remainder
						if (stack.isEmpty()) break;
					}
				}
			}

			markDirty();
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	protected DecomposerRecipe resolveRecipeFromInput(World world) {
		return RECIPE_TYPE.getRecipeFromInventory(world, inputInventory).orElse(null);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "decomposer");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DecomposerContainer.createServerContainer(screenId, playerInv, fuelInventory, emptyBucketInventory, inputInventory, outputInventory, stateData);
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropInventoryItems(world, pos, fuelInventory);
		InventoryHelper.dropInventoryItems(world, pos, emptyBucketInventory);
		InventoryHelper.dropInventoryItems(world, pos, inputInventory);
		InventoryHelper.dropInventoryItems(world, pos, outputInventory);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		stateData.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelInventory.serializeNBT());
		nbt.put("EmptyBucketSlots", emptyBucketInventory.serializeNBT());
		nbt.put("InputSlots", inputInventory.serializeNBT());
		nbt.put("OutputSlots", outputInventory.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		stateData.deserializeNBT(nbt);
		fuelInventory.deserializeNBT(nbt.getCompound("FuelSlots"));
		emptyBucketInventory.deserializeNBT(nbt.getCompound("EmptyBucketSlots"));
		inputInventory.deserializeNBT(nbt.getCompound("InputSlots"));
		outputInventory.deserializeNBT(nbt.getCompound("OutputSlots"));

		if (fuelInventory.getSizeInventory() != FUEL_SLOTS || inputInventory.getSizeInventory() != INPUT_SLOTS
				|| outputInventory.getSizeInventory() != OUTPUT_SLOTS || emptyBucketInventory.getSizeInventory() != EMPTY_BUCKET_SLOTS) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public CompoundNBT writeToItemBlockEntityTag(CompoundNBT nbt) {
		super.writeToItemBlockEntityTag(nbt);
		//TODO: decide if we want this for creative players, would lead to inventory clutter
//		stateData.serializeNBT(nbt);
//		if (!fuelContents.isEmpty()) nbt.put("FuelSlots", fuelContents.serializeNBT());
//		if (!inputContents.isEmpty()) nbt.put("InputSlots", inputContents.serializeNBT());
//		if (!outputContents.isEmpty()) nbt.put("OutputSlots", outputContents.serializeNBT());
		return nbt;
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
		if (!removed)
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (side == Direction.UP) return inputInventory.getOptionalItemStackHandler().cast();
				if (side == null || side == Direction.DOWN) return outputInventory.getOptionalItemStackHandler().cast();
				return fuelInventory.getOptionalItemStackHandler().cast();
			}
			else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return stateData.getOptionalFuelHandler().cast();
			}
		return super.getCapability(cap, side);
	}

}
