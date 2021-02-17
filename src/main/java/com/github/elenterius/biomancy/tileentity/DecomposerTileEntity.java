package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.DecomposerBlock;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.DecomposerContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import com.github.elenterius.biomancy.mixin.RecipeManagerMixinAccessor;
import com.github.elenterius.biomancy.recipe.DecomposingRecipe;
import com.github.elenterius.biomancy.tileentity.state.CraftingState;
import com.github.elenterius.biomancy.tileentity.state.DecomposerStateData;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class DecomposerTileEntity extends OwnableTileEntity implements INamedContainerProvider, ITickableTileEntity {

	public static final int FUEL_SLOTS_COUNT = 1;
	public static final int INPUT_SLOTS_COUNT = DecomposingRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS_COUNT = 1 + DecomposingRecipe.MAX_BYPRODUCTS;
	public static final int FUEL_COST = 10;
	public static final int MAX_FUEL = 32_000;

	private final DecomposerStateData decomposerState = new DecomposerStateData();
	private final SimpleInvContents fuelContents;
	private final SimpleInvContents inputContents;
	private final SimpleInvContents outputContents;

	public DecomposerTileEntity() {
		super(ModTileEntityTypes.DECOMPOSER.get());
		fuelContents = SimpleInvContents.createServerContents(FUEL_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		inputContents = SimpleInvContents.createServerContents(INPUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		outputContents = SimpleInvContents.createServerContents(OUTPUT_SLOTS_COUNT, true, this::canPlayerOpenInv, this::markDirty);
	}

	public static boolean areRecipesEqual(DecomposingRecipe recipeA, DecomposingRecipe recipeB, boolean relaxed) {
		boolean flag = recipeA.getId().equals(recipeB.getId());
		if (!relaxed && !ItemHandlerHelper.canItemStacksStack(recipeA.getRecipeOutput(), recipeB.getRecipeOutput())) {
			return false;
		}
		return flag;
	}

	public static ItemStack getRecipeResult(World world, IInventory inputInv) {
		Optional<DecomposingRecipe> recipe = getRecipeForInput(world, inputInv);
		return recipe.map(decomposingRecipe -> decomposingRecipe.getRecipeOutput().copy()).orElse(ItemStack.EMPTY);
	}

	public static int getCraftingTime(World world, IInventory inputInv) {
		Optional<DecomposingRecipe> recipe = getRecipeForInput(world, inputInv);
		return recipe.map(DecomposingRecipe::getDecomposingTime).orElse(0);
	}

	public static Optional<DecomposingRecipe> getRecipeForInput(World world, IInventory inputInv) {
		RecipeManager recipeManager = world.getRecipeManager();
		return recipeManager.getRecipe(ModRecipes.DECOMPOSING_RECIPE_TYPE, inputInv, world);
	}

	public static Optional<DecomposingRecipe> getRecipeForItem(World world, ItemStack stack) {
		RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();

		return recipeManager.callGetRecipes(ModRecipes.DECOMPOSING_RECIPE_TYPE).values().stream().map((recipe) -> (DecomposingRecipe) recipe)
				.filter(recipe -> {
					for (Ingredient ingredient : recipe.getIngredients()) {
						if (ingredient.test(stack)) return true;
					}
					return false;
				}).findFirst();
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DecomposerContainer.createServerContainer(screenId, playerInv, fuelContents, inputContents, outputContents, decomposerState);
	}

	@Override
	public void tick() {
		if (world == null || world.isRemote) return;

		if (world.getGameTime() % 10L == 0L) {
			refuel();
		}

		DecomposingRecipe recipeToCraft = getRecipeForInput(world, inputContents).orElse(null);
		if (recipeToCraft == null) {
			decomposerState.cancelCrafting();
		}
		else {
			ItemStack itemToCraft = recipeToCraft.getRecipeOutput().copy();
			if (itemToCraft.isEmpty()) {
				decomposerState.cancelCrafting();
			}
			else {
				if (outputContents.doesItemStackFit(0, itemToCraft)) {
					if (decomposerState.getCraftingState() == CraftingState.NONE) {
						decomposerState.setCraftingState(CraftingState.IN_PROGRESS);
						decomposerState.clear(); //safe guard, shouldn't be needed
						decomposerState.setCraftingGoalRecipe(recipeToCraft); // this also sets the time required for crafting
					}
					else if (!decomposerState.isCraftingCanceled()) {
						DecomposingRecipe recipeCraftingGoal = decomposerState.getCraftingGoalRecipe(world).orElse(null);
						if (recipeCraftingGoal == null || !areRecipesEqual(recipeToCraft, recipeCraftingGoal, true)) {
							decomposerState.cancelCrafting();
						}
					}
				}
				else {
					if (decomposerState.getCraftingState() != CraftingState.COMPLETED) decomposerState.cancelCrafting();
				}
			}

			//change crafting progress
			if (decomposerState.getCraftingState() == CraftingState.IN_PROGRESS) {
				if (consumeFuel()) decomposerState.timeElapsed += consumeSpeedFuel() ? 2 : 1;
				else decomposerState.timeElapsed -= 2;

				if (decomposerState.timeElapsed < 0) decomposerState.timeElapsed = 0;
			}

			//craft items
			if (decomposerState.getCraftingState() == CraftingState.IN_PROGRESS || decomposerState.getCraftingState() == CraftingState.COMPLETED) {
				if (decomposerState.timeElapsed >= decomposerState.timeForCompletion) {
					decomposerState.setCraftingState(CraftingState.COMPLETED);
					if (craftItems(recipeToCraft, world.rand)) {
						decomposerState.setCraftingState(CraftingState.NONE);
					}
				}
			}
		}

		//clean-up states
		if (decomposerState.isCraftingCanceled()) {
			decomposerState.setCraftingState(CraftingState.NONE);
			decomposerState.clear();
		}
		else if (decomposerState.getCraftingState() == CraftingState.NONE) {
			decomposerState.clear();
		}

		BlockState oldBlockState = world.getBlockState(pos);
		BlockState newBlockState = oldBlockState.with(DecomposerBlock.DECOMPOSING, decomposerState.getCraftingState() == CraftingState.IN_PROGRESS);
		if (!newBlockState.equals(oldBlockState)) {
			world.setBlockState(pos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
			markDirty();
		}
	}

	private boolean craftItems(DecomposingRecipe recipeToCraft, Random rand) {
		ItemStack result = recipeToCraft.getCraftingResult(inputContents);
		if (!result.isEmpty() && outputContents.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputContents.getSizeInventory(); idx++) {
				inputContents.decrStackSize(idx, 1); //consume input
			}

			outputContents.insertItemStack(0, result); //output result

			//output optional byproducts
			for (DecomposingRecipe.OptionalByproduct byproduct : recipeToCraft.getOptionalByproducts()) {
				if (rand.nextFloat() <= byproduct.getChance()) {
					ItemStack stack = byproduct.getItemStack().copy();
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

	private boolean consumeSpeedFuel() {
		if (decomposerState.speedFuel >= FUEL_COST + 5) {
			decomposerState.speedFuel -= FUEL_COST + 5;
			return true;
		}
		return false;
	}

	private boolean consumeFuel() {
		if (decomposerState.mainFuel >= FUEL_COST) {
			decomposerState.mainFuel -= FUEL_COST;
			return true;
		}
		return false;
	}

	public static boolean isItemValidFuel(ItemStack stack) {
		if (stack.isEmpty()) return false;
		Item item = stack.getItem();
		if (item.isFood() && item.getFood() != null && item.getFood().isMeat()) return true;
		return item.isIn(ModTags.Items.RAW_MEATS) || item.isIn(ModTags.Items.SUGARS);
	}

	public void refuel() {
		ItemStack stack = fuelContents.getStackInSlot(0);
		if (!stack.isEmpty()) {
			ItemStack remainder = addFuel(stack);
			if (remainder.getCount() != stack.getCount()) {
				fuelContents.setInventorySlotContents(0, remainder);
				markDirty();
			}
		}
	}

	public ItemStack addFuel(ItemStack stackIn) {
		if (world == null || world.isRemote()) return stackIn;

		if (!stackIn.isEmpty()) {
			Item item = stackIn.getItem();
			if (decomposerState.mainFuel < MAX_FUEL && stackIn.isFood()) {
				Food food = item.getFood();
				if (food != null && food.isMeat()) {
					int fuelValue = Math.max(1, food.getHealing());
					if (item.isIn(ModTags.Items.RAW_MEATS)) {
						fuelValue = fuelValue * 2 * 300;
					}
					else {
						fuelValue = Math.max(1, fuelValue / 3) * 100;
					}

					int itemsNeeded = Math.round(Math.max(0, MAX_FUEL - decomposerState.mainFuel) / (float) fuelValue);
					int consumeAmount = Math.min(stackIn.getCount(), itemsNeeded);
					if (consumeAmount > 0) {
						decomposerState.mainFuel = (short) MathHelper.clamp(decomposerState.mainFuel + fuelValue * consumeAmount, 0, MAX_FUEL + fuelValue);

						if (item.isIn(ModTags.Items.SUGARS) && decomposerState.speedFuel < MAX_FUEL) {
							decomposerState.speedFuel += MathHelper.clamp(consumeAmount, 0, MAX_FUEL);
						}

						return ItemHandlerHelper.copyStackWithSize(stackIn, stackIn.getCount() - consumeAmount);
					}
				}
			}

			if (decomposerState.speedFuel < MAX_FUEL && item.isIn(ModTags.Items.SUGARS)) {
				int speedFuelValue = 200;
				if (item instanceof BlockItem) speedFuelValue = ((BlockItem) item).getBlock() instanceof SlabBlock ? 800 : 1600;
				if (item.getRegistryName() != null && item.getRegistryName().getPath().contains("honey")) speedFuelValue *= 2;
				if (item == Items.SWEET_BERRIES) speedFuelValue *= 4;

				int itemsNeeded = Math.round(Math.max(0, MAX_FUEL - decomposerState.speedFuel) / (float) speedFuelValue);
				int consumeAmount = Math.min(stackIn.getCount(), itemsNeeded);
				if (consumeAmount > 0) {
					decomposerState.speedFuel = (short) MathHelper.clamp(decomposerState.speedFuel + speedFuelValue * consumeAmount, 0, MAX_FUEL + speedFuelValue);
					return ItemHandlerHelper.copyStackWithSize(stackIn, stackIn.getCount() - consumeAmount);
				}
			}
		}
		return stackIn;
	}

	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropInventoryItems(world, pos, fuelContents);
		InventoryHelper.dropInventoryItems(world, pos, inputContents);
		InventoryHelper.dropInventoryItems(world, pos, outputContents);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		decomposerState.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelContents.serializeNBT());
		nbt.put("InputSlots", inputContents.serializeNBT());
		nbt.put("OutputSlots", outputContents.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		decomposerState.deserializeNBT(nbt);
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
		decomposerState.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelContents.serializeNBT());
		nbt.put("InputSlots", inputContents.serializeNBT());
		nbt.put("OutputSlots", outputContents.serializeNBT());
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

	@Override
	protected ITextComponent getDefaultName() {
		return BiomancyMod.getTranslationText("container", "decomposer");
	}

}
