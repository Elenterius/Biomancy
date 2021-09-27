package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.EvolutionPoolContainer;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.recipe.EvolutionPoolRecipe;
import com.github.elenterius.biomancy.recipe.RecipeType;
import com.github.elenterius.biomancy.tileentity.state.CraftingState;
import com.github.elenterius.biomancy.tileentity.state.EvolutionPoolStateData;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class EvolutionPoolTileEntity extends MachineTileEntity<EvolutionPoolRecipe, EvolutionPoolStateData> {

	public static final int FUEL_SLOTS = 1;
	public static final int INPUT_SLOTS = 6;
	public static final int OUTPUT_SLOTS = 1;

	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 2;
	public static final float ITEM_FUEL_VALUE = 200; // FUEL_COST * 400 / 4f
	public static final Predicate<ItemStack> VALID_FUEL_ITEM = stack -> stack.getItem() == ModItems.MUTAGENIC_BILE.get();
	public static final RecipeType.ItemStackRecipeType<EvolutionPoolRecipe> RECIPE_TYPE = ModRecipes.EVOLUTION_POOL_RECIPE_TYPE;

	private final EvolutionPoolStateData stateData = new EvolutionPoolStateData();
	private final SimpleInventory<?> fuelInventory;
	private final SimpleInventory<?> inputInventory;
	private final SimpleInventory<?> outputInventory;
	private final Set<BlockPos> subTiles = new HashSet<>();
	private boolean isValidMultiBlock = false;

	public EvolutionPoolTileEntity() {
		super(ModTileEntityTypes.EVOLUTION_POOL.get());
		fuelInventory = SimpleInventory.createServerContents(FUEL_SLOTS, itemStackHandler -> HandlerBehaviors.filterInput(itemStackHandler, this::isItemValidFuel), this::canPlayerOpenInv, this::setChanged);
		inputInventory = SimpleInventory.createServerContents(INPUT_SLOTS, this::canPlayerOpenInv, this::setChanged);
		outputInventory = SimpleInventory.createServerContents(OUTPUT_SLOTS, HandlerBehaviors::denyInput, this::canPlayerOpenInv, this::setChanged);
	}

	@Override
	protected EvolutionPoolStateData getStateData() {
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
		return VALID_FUEL_ITEM.test(stack);
	}

	@Override
	public float getItemFuelValue(ItemStack stackIn) {
		return ITEM_FUEL_VALUE;
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
	protected boolean craftRecipe(EvolutionPoolRecipe recipeToCraft, World world) {
		ItemStack result = recipeToCraft.assemble(inputInventory);
		if (!result.isEmpty() && outputInventory.doesItemStackFit(0, result)) {
			for (int idx = 0; idx < inputInventory.getContainerSize(); idx++) {
				inputInventory.removeItem(idx, 1);
			}
			outputInventory.insertItemStack(0, result);
			setChanged();
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	protected EvolutionPoolRecipe resolveRecipeFromInput(World world) {
		return RECIPE_TYPE.getRecipeFromInventory(world, inputInventory).orElse(null);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "evolution_pool");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return EvolutionPoolContainer.createServerContainer(screenId, playerInv, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public void addSubTile(BlockPos pos) {
		subTiles.add(pos);
	}

	public void addSubTile(OwnableTileEntityDelegator delegator) {
		addSubTile(delegator.getBlockPos());
	}

	public void removeSubTile(OwnableTileEntityDelegator delegator) {
		removeSubTile(delegator.getBlockPos());
	}

	public void removeSubTile(BlockPos posIn) {
		if (subTiles.contains(posIn)) {
			subTiles.remove(posIn);
			isValidMultiBlock = false;
			scheduleMultiBlockDeconstruction(false);
		}
	}

	public void scheduleMultiBlockDeconstruction(boolean isController) {
		if (level == null) return;

		for (BlockPos subPos : subTiles) {
			TileEntity tile = level.getBlockEntity(subPos);
			if (tile instanceof OwnableTileEntityDelegator) {
				((OwnableTileEntityDelegator) tile).setDelegate(null);
				level.getBlockTicks().scheduleTick(subPos, ModBlocks.EVOLUTION_POOL.get(), 1, TickPriority.EXTREMELY_HIGH); //schedule tick asap in order to trigger self-destruct of block
			}
		}
		subTiles.clear();
		isValidMultiBlock = false;
		if (!isController)
			level.getBlockTicks().scheduleTick(this.worldPosition, ModBlocks.EVOLUTION_POOL.get(), 1, TickPriority.EXTREMELY_HIGH); //schedule tick asap in order to trigger self-destruct of block
	}

	public boolean isValidMultiBlock() {
		return isValidMultiBlock;
	}

	public boolean validateMultiBlock() {
		isValidMultiBlock = false;
		int size = subTiles.size();

		if (level == null || level.isClientSide) return false;

		if (size == 4 - 1) {
			if (validate2x2Pattern(level)) {
				isValidMultiBlock = true;
				return true;
			}
		}
		else if (size == 9 - 1) {
			isValidMultiBlock = true;
			//TODO: implement 3x3 pool size
		}

		return false;
	}

	public boolean validate2x2Pattern(World worldIn) {
		Direction direction = Direction.NORTH;
		BlockPos posOffset = worldPosition.relative(direction);
		if (subTiles.contains(posOffset) && validateSubTile(worldIn, posOffset)) {
			BlockPos rightPos = worldPosition.relative(direction.getClockWise());
			BlockPos leftPos = worldPosition.relative(direction.getCounterClockWise());
			if (subTiles.contains(rightPos) && subTiles.contains(rightPos.relative(direction))) {
				return validateSubTile(worldIn, rightPos) && validateSubTile(worldIn, rightPos.relative(direction));
			}
			else if (subTiles.contains(leftPos) && subTiles.contains(leftPos.relative(direction))) {
				return validateSubTile(worldIn, leftPos) && validateSubTile(worldIn, leftPos.relative(direction));
			}
		}

		direction = direction.getOpposite(); //south
		posOffset = worldPosition.relative(direction);
		if (subTiles.contains(posOffset) && validateSubTile(worldIn, posOffset)) {
			BlockPos rightPos = worldPosition.relative(direction.getClockWise());
			BlockPos leftPos = worldPosition.relative(direction.getCounterClockWise());
			if (subTiles.contains(rightPos) && subTiles.contains(rightPos.relative(direction))) {
				return validateSubTile(worldIn, rightPos) && validateSubTile(worldIn, rightPos.relative(direction));
			}
			else if (subTiles.contains(leftPos) && subTiles.contains(leftPos.relative(direction))) {
				return validateSubTile(worldIn, leftPos) && validateSubTile(worldIn, leftPos.relative(direction));
			}
		}

		return false;
	}

	public boolean validateSubTile(World worldIn, BlockPos posIn) {
		BlockState state = worldIn.getBlockState(posIn);
		TileEntity tileEntity = worldIn.getBlockEntity(posIn);
		if (tileEntity instanceof OwnableTileEntityDelegator) {
			return state.is(ModBlocks.EVOLUTION_POOL.get()) && ((OwnableTileEntityDelegator) tileEntity).getDelegatePos().equals(this.worldPosition);
		}
		return false;
	}

	@Override
	public void tick() {
		if (level == null || level.isClientSide || !isValidMultiBlock) return;
		super.tick();
	}

	@Override
	protected void updateBlockState(World world, EvolutionPoolStateData tileState, boolean powerBlock) {
		updateMultiBlockStates(world, tileState.getCraftingState() == CraftingState.IN_PROGRESS);
	}

	private void updateMultiBlockStates(World worldIn, boolean isCraftingInProgress) {
		boolean isDirty = false;

		BlockState oldBlockState = worldIn.getBlockState(worldPosition);
		BlockState newBlockState = oldBlockState.setValue(getIsCraftingBlockStateProperty(), isCraftingInProgress);
		if (!newBlockState.equals(oldBlockState)) {
			worldIn.setBlock(worldPosition, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
			isDirty = true;
		}

		for (BlockPos subPos : subTiles) {
			oldBlockState = worldIn.getBlockState(subPos);
			newBlockState = oldBlockState.setValue(getIsCraftingBlockStateProperty(), isCraftingInProgress);
			if (!newBlockState.equals(oldBlockState)) {
				worldIn.setBlock(subPos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
				isDirty = true;
			}
		}

		if (isDirty) setChanged();
	}

	@Override
	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropContents(world, pos, fuelInventory);
		InventoryHelper.dropContents(world, pos, inputInventory);
		InventoryHelper.dropContents(world, pos, outputInventory);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		stateData.serializeNBT(nbt);
		nbt.put("FuelSlots", fuelInventory.serializeNBT());
		nbt.put("InputSlots", inputInventory.serializeNBT());
		nbt.put("OutputSlots", outputInventory.serializeNBT());

		nbt.putBoolean("ValidMultiBlock", isValidMultiBlock);
		if (subTiles.size() > 0) {
			ListNBT listNBT = new ListNBT();
			for (BlockPos pos : subTiles) {
				listNBT.add(LongNBT.valueOf(pos.asLong()));
			}
			nbt.put("SubTilesPos", listNBT);
		}

		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		stateData.deserializeNBT(nbt);
		fuelInventory.deserializeNBT(nbt.getCompound("FuelSlots"));
		inputInventory.deserializeNBT(nbt.getCompound("InputSlots"));
		outputInventory.deserializeNBT(nbt.getCompound("OutputSlots"));

		if (fuelInventory.getContainerSize() != FUEL_SLOTS || inputInventory.getContainerSize() != INPUT_SLOTS || outputInventory.getContainerSize() != OUTPUT_SLOTS) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}

		isValidMultiBlock = nbt.getBoolean("ValidMultiBlock");
		subTiles.clear();
		if (nbt.contains("SubTilesPos")) {
			ListNBT nbtList = nbt.getList("SubTilesPos", Constants.NBT.TAG_LONG);
			for (INBT nbtEntry : nbtList) {
				if (nbtEntry instanceof LongNBT) {
					subTiles.add(BlockPos.of(((LongNBT) nbtEntry).getAsLong()));
				}
			}
		}
	}

	@Override
	public void invalidateCaps() {
		fuelInventory.getOptionalItemStackHandler().invalidate();
		inputInventory.getOptionalItemStackHandler().invalidate();
		outputInventory.getOptionalItemStackHandler().invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.UP) return inputInventory.getOptionalItemStackHandler().cast();
			if (side == null || side == Direction.DOWN) return outputInventory.getOptionalItemStackHandler().cast();
			return fuelInventory.getOptionalItemStackHandler().cast();
		}
		return super.getCapability(cap, side);
	}
}
