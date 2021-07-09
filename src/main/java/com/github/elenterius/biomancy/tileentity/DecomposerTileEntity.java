package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.DecomposerContainer;
import com.github.elenterius.biomancy.inventory.FuelInvContents;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.recipe.RecipeType;
import com.github.elenterius.biomancy.tileentity.state.DecomposerStateData;
import com.github.elenterius.biomancy.util.BiofuelUtil;
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
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DecomposerTileEntity extends MachineTileEntity<DecomposerRecipe, DecomposerStateData> {

	public static final int FUEL_SLOTS_COUNT = 1;
	public static final int INPUT_SLOTS_COUNT = DecomposerRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS_COUNT = 1 + DecomposerRecipe.MAX_BYPRODUCTS;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 5;
	public static final RecipeType.ItemStackRecipeType<DecomposerRecipe> RECIPE_TYPE = ModRecipes.DECOMPOSING_RECIPE_TYPE;

	private final DecomposerStateData stateData = new DecomposerStateData();
	private final FuelInvContents fuelContents;
	private final SimpleInvContents inputContents;
	private final SimpleInvContents outputContents;

	public DecomposerTileEntity() {
		super(ModTileEntityTypes.DECOMPOSER.get());
		fuelContents = FuelInvContents.createServerContents(FUEL_SLOTS_COUNT, this::isItemValidFuel, this::canPlayerOpenInv, this::markDirty);
		inputContents = SimpleInvContents.createServerContents(INPUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		outputContents = SimpleInvContents.createServerContents(OUTPUT_SLOTS_COUNT, SimpleInvContents.ISHandlerType.NO_INSERT, this::canPlayerOpenInv, this::markDirty);
	}

	@Override
	protected DecomposerStateData getStateData() {
		return stateData;
	}

	@Override
	public int getFuelAmount() {
		return stateData.fuel;
	}

	@Override
	public void setFuelAmount(int newAmount) {
		stateData.fuel = (short) newAmount;
	}

	@Override
	public void addFuelAmount(int addAmount) {
		stateData.fuel += addAmount;
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
	public boolean isItemValidFuel(ItemStack stack) {
		return BiofuelUtil.isItemValidFuel(stack);
	}

	@Override
	public float getItemFuelValue(ItemStack stackIn) {
		return BiofuelUtil.getItemFuelValue(stackIn);
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
	protected boolean craftRecipe(DecomposerRecipe recipeToCraft, World world) {
		ItemStack result = recipeToCraft.getCraftingResult(inputContents);
		if (!result.isEmpty() && outputContents.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputContents.getSizeInventory(); idx++) {
				inputContents.decrStackSize(idx, recipeToCraft.getIngredientCount()); //consume input
			}

			outputContents.insertItemStack(0, result); //output result

			//output optional byproducts
			for (Byproduct byproduct : recipeToCraft.getByproducts()) {
				if (world.rand.nextFloat() <= byproduct.getChance()) {
					ItemStack stack = byproduct.getItemStack();
					for (int idx = 1; idx < outputContents.getSizeInventory(); idx++) { //index 0 is reserved for the main crafting output
						stack = outputContents.insertItemStack(idx, stack); //update stack with remainder
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
		return RECIPE_TYPE.getRecipeFromInventory(world, inputContents).orElse(null);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "decomposer");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DecomposerContainer.createServerContainer(screenId, playerInv, fuelContents, inputContents, outputContents, stateData);
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropInventoryItems(world, pos, fuelContents);
		InventoryHelper.dropInventoryItems(world, pos, inputContents);
		InventoryHelper.dropInventoryItems(world, pos, outputContents);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		stateData.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelContents.serializeNBT());
		nbt.put("InputSlots", inputContents.serializeNBT());
		nbt.put("OutputSlots", outputContents.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		stateData.deserializeNBT(nbt);
		fuelContents.deserializeNBT(nbt.getCompound("FuelSlots"));
		inputContents.deserializeNBT(nbt.getCompound("InputSlots"));
		outputContents.deserializeNBT(nbt.getCompound("OutputSlots"));

		if (fuelContents.getSizeInventory() != FUEL_SLOTS_COUNT || inputContents.getSizeInventory() != INPUT_SLOTS_COUNT || outputContents.getSizeInventory() != OUTPUT_SLOTS_COUNT) {
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
		fuelContents.getOptionalItemStackHandler().invalidate();
		inputContents.getOptionalItemStackHandler().invalidate();
		outputContents.getOptionalItemStackHandler().invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.UP) return inputContents.getOptionalItemStackHandler().cast();
			if (side == null || side == Direction.DOWN) return outputContents.getOptionalItemStackHandler().cast();
			return fuelContents.getOptionalItemStackHandler().cast();
		}
		return super.getCapability(cap, side);
	}

}
