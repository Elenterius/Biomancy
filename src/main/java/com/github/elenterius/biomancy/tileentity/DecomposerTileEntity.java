package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.block.DecomposerBlock;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.DecomposerContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import com.github.elenterius.biomancy.mixin.RecipeManagerMixinAccessor;
import com.github.elenterius.biomancy.recipe.Byproduct;
import com.github.elenterius.biomancy.recipe.DecomposerRecipe;
import com.github.elenterius.biomancy.tileentity.state.CraftingState;
import com.github.elenterius.biomancy.tileentity.state.DecomposerStateData;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
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
	public static final int INPUT_SLOTS_COUNT = DecomposerRecipe.MAX_INGREDIENTS;
	public static final int OUTPUT_SLOTS_COUNT = 1 + DecomposerRecipe.MAX_BYPRODUCTS;

	public static final int DEFAULT_TIME = 200;
	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 5;
	public static final float FUEL_CONVERSION = FUEL_COST * DEFAULT_TIME / 4f;

	private final DecomposerStateData stateData = new DecomposerStateData();
	private final SimpleInvContents fuelContents;
	private final SimpleInvContents inputContents;
	private final SimpleInvContents outputContents;

	public DecomposerTileEntity() {
		super(ModTileEntityTypes.DECOMPOSER.get());
		fuelContents = SimpleInvContents.createServerContents(FUEL_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		inputContents = SimpleInvContents.createServerContents(INPUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		outputContents = SimpleInvContents.createServerContents(OUTPUT_SLOTS_COUNT, SimpleInvContents.ISHandlerType.NO_INSERT, this::canPlayerOpenInv, this::markDirty);
	}

	public static boolean areRecipesEqual(DecomposerRecipe recipeA, DecomposerRecipe recipeB, boolean relaxed) {
		boolean flag = recipeA.getId().equals(recipeB.getId());
		if (!relaxed && !ItemHandlerHelper.canItemStacksStack(recipeA.getRecipeOutput(), recipeB.getRecipeOutput())) {
			return false;
		}
		return flag;
	}

	public static ItemStack getRecipeResult(World world, IInventory inputInv) {
		Optional<DecomposerRecipe> recipe = getRecipeForInput(world, inputInv);
		return recipe.map(decomposingRecipe -> decomposingRecipe.getRecipeOutput().copy()).orElse(ItemStack.EMPTY);
	}

	public static int getCraftingTime(World world, IInventory inputInv) {
		Optional<DecomposerRecipe> recipe = getRecipeForInput(world, inputInv);
		return recipe.map(DecomposerRecipe::getDecomposingTime).orElse(0);
	}

	public static Optional<DecomposerRecipe> getRecipeForInput(World world, IInventory inputInv) {
		RecipeManager recipeManager = world.getRecipeManager();
		return recipeManager.getRecipe(ModRecipes.DECOMPOSING_RECIPE_TYPE, inputInv, world);
	}

	public static Optional<DecomposerRecipe> getRecipeForItem(World world, ItemStack stack) {
		RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();

		return recipeManager.callGetRecipes(ModRecipes.DECOMPOSING_RECIPE_TYPE).values().stream().map((recipe) -> (DecomposerRecipe) recipe)
				.filter(recipe -> {
					for (Ingredient ingredient : recipe.getIngredients()) {
						if (ingredient.test(stack)) return true;
					}
					return false;
				}).findFirst();
	}

	public static boolean isItemValidFuel(ItemStack stack) {
		return stack.getItem() == ModItems.NUTRIENT_PASTE.get() || stack.getItem() == ModItems.NUTRIENT_BAR.get() || stack.getItem().isIn(ModTags.Items.COOKED_MEATS);
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return DecomposerContainer.createServerContainer(screenId, playerInv, fuelContents, inputContents, outputContents, stateData);
	}

	@Override
	public void tick() {
		if (world == null || world.isRemote) return;

		if (world.getGameTime() % 10L == 0L) {
			refuel();
		}

		DecomposerRecipe recipeToCraft = getRecipeForInput(world, inputContents).orElse(null);
		if (recipeToCraft == null) {
			stateData.cancelCrafting();
		}
		else {
			ItemStack itemToCraft = recipeToCraft.getRecipeOutput().copy();
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
						DecomposerRecipe recipeCraftingGoal = stateData.getCraftingGoalRecipe(world).orElse(null);
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
				if (consumeFuel()) stateData.timeElapsed += consumeSpeedFuel() ? 2 : 1;
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
		BlockState newBlockState = oldBlockState.with(DecomposerBlock.DECOMPOSING, stateData.getCraftingState() == CraftingState.IN_PROGRESS);
		if (!newBlockState.equals(oldBlockState)) {
			world.setBlockState(pos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
			markDirty();
		}
	}

	private boolean craftItems(DecomposerRecipe recipeToCraft, Random rand) {
		ItemStack result = recipeToCraft.getCraftingResult(inputContents);
		if (!result.isEmpty() && outputContents.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputContents.getSizeInventory(); idx++) {
				inputContents.decrStackSize(idx, 1); //consume input
			}

			outputContents.insertItemStack(0, result); //output result

			//output optional byproducts
			for (Byproduct byproduct : recipeToCraft.getByproducts()) {
				if (rand.nextFloat() <= byproduct.getChance()) {
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

	private boolean consumeSpeedFuel() {
		if (stateData.speedFuel >= FUEL_COST + 5) {
			stateData.speedFuel -= FUEL_COST + 5;
			return true;
		}
		return false;
	}

	private boolean consumeFuel() {
		if (stateData.mainFuel >= FUEL_COST) {
			stateData.mainFuel -= FUEL_COST;
			return true;
		}
		return false;
	}

	public void refuel() {
		if (stateData.mainFuel < MAX_FUEL) {
			ItemStack stack = fuelContents.getStackInSlot(0);
			if (isItemValidFuel(stack)) {
				ItemStack remainder = addFuel(stack);
				if (remainder.getCount() != stack.getCount()) {
					fuelContents.setInventorySlotContents(0, remainder);
					markDirty();
				}
			}
		}
	}

	public ItemStack addFuel(ItemStack stackIn) {
		if (world == null || world.isRemote()) return stackIn;

		if (!stackIn.isEmpty() && stateData.mainFuel < MAX_FUEL) {
			float fuelConversion = FUEL_CONVERSION * (stackIn.getItem() == ModItems.NUTRIENT_BAR.get() ? 5 : 1);
			int itemsNeeded = Math.round(Math.max(0, MAX_FUEL - stateData.mainFuel) / fuelConversion);
			int consumeAmount = Math.min(stackIn.getCount(), itemsNeeded);
			if (consumeAmount > 0) {
				stateData.mainFuel = (short) MathHelper.clamp(stateData.mainFuel + fuelConversion * consumeAmount, 0, MAX_FUEL + fuelConversion);
				return ItemHandlerHelper.copyStackWithSize(stackIn, stackIn.getCount() - consumeAmount);
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
		stateData.serializeNBT(nbt);
		if (!fuelContents.isEmpty()) nbt.put("FuelSlots", fuelContents.serializeNBT());
		if (!inputContents.isEmpty()) nbt.put("InputSlots", inputContents.serializeNBT());
		if (!outputContents.isEmpty()) nbt.put("OutputSlots", outputContents.serializeNBT());
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
		return TextUtil.getTranslationText("container", "decomposer");
	}

}
