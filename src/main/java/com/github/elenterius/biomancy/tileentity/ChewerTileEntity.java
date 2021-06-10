package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.block.ChewerBlock;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.ChewerContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import com.github.elenterius.biomancy.mixin.RecipeManagerMixinAccessor;
import com.github.elenterius.biomancy.recipe.ChewerRecipe;
import com.github.elenterius.biomancy.tileentity.state.ChewerStateData;
import com.github.elenterius.biomancy.tileentity.state.CraftingState;
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

public class ChewerTileEntity extends OwnableTileEntity implements INamedContainerProvider, ITickableTileEntity {

	public static final int FUEL_SLOTS_COUNT = 1;
	public static final int INPUT_SLOTS_COUNT = 1;
	public static final int OUTPUT_SLOTS_COUNT = 1;

	public static final int DEFAULT_TIME = 200;
	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 2;
	public static final float FUEL_CONVERSION = FUEL_COST * DEFAULT_TIME / 4f;

	private final ChewerStateData stateData = new ChewerStateData();
	private final SimpleInvContents fuelContents;
	private final SimpleInvContents inputContents;
	private final SimpleInvContents outputContents;

	public ChewerTileEntity() {
		super(ModTileEntityTypes.CHEWER.get());
		fuelContents = SimpleInvContents.createServerContents(FUEL_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		inputContents = SimpleInvContents.createServerContents(INPUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		outputContents = SimpleInvContents.createServerContents(OUTPUT_SLOTS_COUNT, SimpleInvContents.ISHandlerType.NO_INSERT, this::canPlayerOpenInv, this::markDirty);
	}

	public static Optional<ChewerRecipe> getRecipeForItem(World world, ItemStack stackIn) {
		RecipeManagerMixinAccessor recipeManager = (RecipeManagerMixinAccessor) world.getRecipeManager();

		return recipeManager.callGetRecipes(ModRecipes.CHEWER_RECIPE_TYPE).values().stream().map((recipe) -> (ChewerRecipe) recipe)
				.filter(recipe -> {
					for (Ingredient ingredient : recipe.getIngredients()) {
						if (ingredient.test(stackIn)) return true;
					}
					return false;
				}).findFirst();
	}

	public static boolean areRecipesEqual(ChewerRecipe recipeA, ChewerRecipe recipeB, boolean relaxed) {
		boolean flag = recipeA.getId().equals(recipeB.getId());
		if (!relaxed && !ItemHandlerHelper.canItemStacksStack(recipeA.getRecipeOutput(), recipeB.getRecipeOutput())) {
			return false;
		}
		return flag;
	}

	public static Optional<ChewerRecipe> getRecipeForInput(World world, IInventory inputInv) {
		RecipeManager recipeManager = world.getRecipeManager();
		return recipeManager.getRecipe(ModRecipes.CHEWER_RECIPE_TYPE, inputInv, world);
	}

	public static boolean isItemValidFuel(ItemStack stack) {
		return stack.getItem() == ModItems.NUTRIENT_PASTE.get() || stack.getItem() == ModItems.NUTRIENT_BAR.get() || stack.getItem().isIn(ModTags.Items.COOKED_MEATS);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "chewer");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return ChewerContainer.createServerContainer(screenId, playerInv, fuelContents, inputContents, outputContents, stateData);
	}

	@Override
	public void tick() {
		if (world == null || world.isRemote) return;

		if (world.getGameTime() % 10L == 0L) {
			refuel();
		}

		ChewerRecipe recipeToCraft = getRecipeForInput(world, inputContents).orElse(null);
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
						ChewerRecipe recipeCraftingGoal = stateData.getCraftingGoalRecipe(world).orElse(null);
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
					if (craftItem(recipeToCraft)) {
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
		BlockState newBlockState = oldBlockState.with(ChewerBlock.CRAFTING, stateData.getCraftingState() == CraftingState.IN_PROGRESS);
		if (!newBlockState.equals(oldBlockState)) {
			world.setBlockState(pos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
			markDirty();
		}
	}

	private boolean craftItem(ChewerRecipe recipeToCraft) {
		ItemStack result = recipeToCraft.getCraftingResult(inputContents);
		if (!result.isEmpty() && outputContents.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputContents.getSizeInventory(); idx++) {
				inputContents.decrStackSize(idx, 1);
			}
			outputContents.insertItemStack(0, result);
			markDirty();
			return true;
		}
		return false;
	}

	private boolean consumeFuel() {
		if (stateData.fuel >= FUEL_COST) {
			stateData.fuel -= FUEL_COST;
			return true;
		}
		return false;
	}

	public void refuel() {
		if (stateData.fuel < MAX_FUEL) {
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

		if (!stackIn.isEmpty() && stateData.fuel < MAX_FUEL) {
			float fuelConversion = FUEL_CONVERSION * (stackIn.getItem() == ModItems.NUTRIENT_BAR.get() ? 5 : 1);
			int itemsNeeded = Math.round(Math.max(0, MAX_FUEL - stateData.fuel) / fuelConversion);
			int consumeAmount = Math.min(stackIn.getCount(), itemsNeeded);
			if (consumeAmount > 0) {
				stateData.fuel = (short) MathHelper.clamp(stateData.fuel + fuelConversion * consumeAmount, 0, MAX_FUEL + fuelConversion);
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
