package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.EvolutionPoolContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import com.github.elenterius.biomancy.inventory.itemhandler.behavior.ItemHandlerBehavior;
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

	public static final int FUEL_SLOTS_COUNT = 1;
	public static final int INPUT_SLOTS_COUNT = 6;
	public static final int OUTPUT_SLOTS_COUNT = 1;

	public static final int DEFAULT_TIME = 400;
	public static final int MAX_FUEL = 32_000;
	public static final short FUEL_COST = 2;
	public static final float ITEM_FUEL_VALUE = 200; // FUEL_COST * DEFAULT_TIME / 4f
	public static final Predicate<ItemStack> VALID_FUEL_ITEM = stack -> stack.getItem() == ModItems.MUTAGENIC_BILE.get();
	public static final RecipeType.ItemStackRecipeType<EvolutionPoolRecipe> RECIPE_TYPE = ModRecipes.EVOLUTION_POOL_RECIPE_TYPE;

	private final EvolutionPoolStateData stateData = new EvolutionPoolStateData();
	private final SimpleInvContents fuelContents;
	private final SimpleInvContents inputContents;
	private final SimpleInvContents outputContents;
	private final Set<BlockPos> subTiles = new HashSet<>();
	private boolean isValidMultiBlock = false;

	public EvolutionPoolTileEntity() {
		super(ModTileEntityTypes.EVOLUTION_POOL.get());
		fuelContents = SimpleInvContents.createServerContents(FUEL_SLOTS_COUNT, itemStackHandler -> ItemHandlerBehavior.filterInput(itemStackHandler, this::isItemValidFuel), this::canPlayerOpenInv, this::markDirty);
		inputContents = SimpleInvContents.createServerContents(INPUT_SLOTS_COUNT, this::canPlayerOpenInv, this::markDirty);
		outputContents = SimpleInvContents.createServerContents(OUTPUT_SLOTS_COUNT, ItemHandlerBehavior::denyInput, this::canPlayerOpenInv, this::markDirty);
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
	protected boolean craftRecipe(EvolutionPoolRecipe recipeToCraft, World world) {
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

	@Nullable
	@Override
	protected EvolutionPoolRecipe resolveRecipeFromInput(World world) {
		return RECIPE_TYPE.getRecipeFromInventory(world, inputContents).orElse(null);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return TextUtil.getTranslationText("container", "evolution_pool");
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity player) {
		return EvolutionPoolContainer.createServerContainer(screenId, playerInv, fuelContents, inputContents, outputContents, stateData);
	}

	public void addSubTile(BlockPos pos) {
		subTiles.add(pos);
	}

	public void addSubTile(OwnableTileEntityDelegator delegator) {
		addSubTile(delegator.getPos());
	}

	public void removeSubTile(OwnableTileEntityDelegator delegator) {
		removeSubTile(delegator.getPos());
	}

	public void removeSubTile(BlockPos posIn) {
		if (subTiles.contains(posIn)) {
			subTiles.remove(posIn);
			isValidMultiBlock = false;
			scheduleMultiBlockDeconstruction(false);
		}
	}

	public void scheduleMultiBlockDeconstruction(boolean isController) {
		if (world == null) return;

		for (BlockPos subPos : subTiles) {
			TileEntity tile = world.getTileEntity(subPos);
			if (tile instanceof OwnableTileEntityDelegator) {
				((OwnableTileEntityDelegator) tile).setDelegate(null);
				world.getPendingBlockTicks().scheduleTick(subPos, ModBlocks.EVOLUTION_POOL.get(), 1, TickPriority.EXTREMELY_HIGH); //schedule tick asap in order to trigger self-destruct of block
			}
		}
		subTiles.clear();
		isValidMultiBlock = false;
		if (!isController)
			world.getPendingBlockTicks().scheduleTick(this.pos, ModBlocks.EVOLUTION_POOL.get(), 1, TickPriority.EXTREMELY_HIGH); //schedule tick asap in order to trigger self-destruct of block
	}

	public boolean isValidMultiBlock() {
		return isValidMultiBlock;
	}

	public boolean validateMultiBlock() {
		isValidMultiBlock = false;
		int size = subTiles.size();

		if (world == null || world.isRemote) return false;

		if (size == 4 - 1) {
			if (validate2x2Pattern(world)) {
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
		BlockPos posOffset = pos.offset(direction);
		if (subTiles.contains(posOffset) && validateSubTile(worldIn, posOffset)) {
			BlockPos rightPos = pos.offset(direction.rotateY());
			BlockPos leftPos = pos.offset(direction.rotateYCCW());
			if (subTiles.contains(rightPos) && subTiles.contains(rightPos.offset(direction))) {
				return validateSubTile(worldIn, rightPos) && validateSubTile(worldIn, rightPos.offset(direction));
			}
			else if (subTiles.contains(leftPos) && subTiles.contains(leftPos.offset(direction))) {
				return validateSubTile(worldIn, leftPos) && validateSubTile(worldIn, leftPos.offset(direction));
			}
		}

		direction = direction.getOpposite(); //south
		posOffset = pos.offset(direction);
		if (subTiles.contains(posOffset) && validateSubTile(worldIn, posOffset)) {
			BlockPos rightPos = pos.offset(direction.rotateY());
			BlockPos leftPos = pos.offset(direction.rotateYCCW());
			if (subTiles.contains(rightPos) && subTiles.contains(rightPos.offset(direction))) {
				return validateSubTile(worldIn, rightPos) && validateSubTile(worldIn, rightPos.offset(direction));
			}
			else if (subTiles.contains(leftPos) && subTiles.contains(leftPos.offset(direction))) {
				return validateSubTile(worldIn, leftPos) && validateSubTile(worldIn, leftPos.offset(direction));
			}
		}

		return false;
	}

	public boolean validateSubTile(World worldIn, BlockPos posIn) {
		BlockState state = worldIn.getBlockState(posIn);
		TileEntity tileEntity = worldIn.getTileEntity(posIn);
		if (tileEntity instanceof OwnableTileEntityDelegator) {
			return state.matchesBlock(ModBlocks.EVOLUTION_POOL.get()) && ((OwnableTileEntityDelegator) tileEntity).getDelegatePos().equals(this.pos);
		}
		return false;
	}

	@Override
	public void tick() {
		if (world == null || world.isRemote || !isValidMultiBlock) return;
		super.tick();
	}

	@Override
	protected void updateBlockState(World world, EvolutionPoolStateData tileState, boolean powerBlock) {
		updateMultiBlockStates(world, tileState.getCraftingState() == CraftingState.IN_PROGRESS);
	}

	private void updateMultiBlockStates(World worldIn, boolean isCraftingInProgress) {
		boolean isDirty = false;

		BlockState oldBlockState = worldIn.getBlockState(pos);
		BlockState newBlockState = oldBlockState.with(getIsCraftingBlockStateProperty(), isCraftingInProgress);
		if (!newBlockState.equals(oldBlockState)) {
			worldIn.setBlockState(pos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
			isDirty = true;
		}

		for (BlockPos subPos : subTiles) {
			oldBlockState = worldIn.getBlockState(subPos);
			newBlockState = oldBlockState.with(getIsCraftingBlockStateProperty(), isCraftingInProgress);
			if (!newBlockState.equals(oldBlockState)) {
				worldIn.setBlockState(subPos, newBlockState, Constants.BlockFlags.BLOCK_UPDATE);
				isDirty = true;
			}
		}

		if (isDirty) markDirty();
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

		nbt.putBoolean("ValidMultiBlock", isValidMultiBlock);
		if (subTiles.size() > 0) {
			ListNBT listNBT = new ListNBT();
			for (BlockPos pos : subTiles) {
				listNBT.add(LongNBT.valueOf(pos.toLong()));
			}
			nbt.put("SubTilesPos", listNBT);
		}

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

		isValidMultiBlock = nbt.getBoolean("ValidMultiBlock");
		subTiles.clear();
		if (nbt.contains("SubTilesPos")) {
			ListNBT nbtList = nbt.getList("SubTilesPos", Constants.NBT.TAG_LONG);
			for (INBT nbtEntry : nbtList) {
				if (nbtEntry instanceof LongNBT) {
					subTiles.add(BlockPos.fromLong(((LongNBT) nbtEntry).getLong()));
				}
			}
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
