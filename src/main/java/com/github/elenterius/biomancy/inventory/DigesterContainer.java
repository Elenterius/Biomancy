package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModContainerTypes;
import com.github.elenterius.biomancy.inventory.slot.OutputSlot;
import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import com.github.elenterius.biomancy.tileentity.state.DigesterStateData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.logging.log4j.MarkerManager;

public class DigesterContainer extends ContainerWithPlayerInv {

	protected final SimpleInventory<?> fuelInventory;
	protected final SimpleInventory<?> emptyBucketOutInventory;
	protected final SimpleInventory<?> inputInventory;
	protected final SimpleInventory<?> outputInventory;
	protected final SimpleInventory<?> emptyBucketInInventory;
	protected final SimpleInventory<?> filledBucketOutInventory;
	private final DigesterStateData stateData;
	private final World world;

	private DigesterContainer(int screenId, PlayerInventory playerInventory, SimpleInventory<?> fuelInventory, SimpleInventory<?> emptyBucketOutInventory, SimpleInventory<?> inputInventory, SimpleInventory<?> outputInventory, SimpleInventory<?> emptyBucketInInventory, SimpleInventory<?> filledBucketOutInventory, DigesterStateData stateData) {
		super(ModContainerTypes.DIGESTER.get(), screenId, playerInventory);
		world = playerInventory.player.level;

		this.fuelInventory = fuelInventory;
		this.emptyBucketOutInventory = emptyBucketOutInventory;
		this.inputInventory = inputInventory;
		this.outputInventory = outputInventory;
		this.emptyBucketInInventory = emptyBucketInInventory;
		this.filledBucketOutInventory = filledBucketOutInventory;
		this.stateData = stateData;

		addDataSlots(stateData);

		addSlot(new Slot(fuelInventory, 0, 17, 17) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return DigesterTileEntity.VALID_FUEL_ITEM.test(stack);
			}
		});

		addSlot(new Slot(inputInventory, 0, 62, 26));

		addSlot(new OutputSlot(outputInventory, 0, 98, 26));

		addSlot(new OutputSlot(emptyBucketOutInventory, 0, 17, 44));

		addSlot(new Slot(emptyBucketInInventory, 0, 131, 17) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return HandlerBehaviors.FLUID_CONTAINER_ITEM_PREDICATE.test(stack);
			}
		});
		addSlot(new OutputSlot(filledBucketOutInventory, 0, 131, 44));
	}

	public static DigesterContainer createServerContainer(int screenId, PlayerInventory playerInventory, SimpleInventory<?> fuelInventory, SimpleInventory<?> emptyBucketOutInventory,
														  SimpleInventory<?> inputInventory, SimpleInventory<?> outputInventory, SimpleInventory<?> emptyBucketInInventory,
														  SimpleInventory<?> filledBucketOutInventory, DigesterStateData stateData) {
		return new DigesterContainer(screenId, playerInventory, fuelInventory, emptyBucketOutInventory, inputInventory, outputInventory, emptyBucketInInventory, filledBucketOutInventory, stateData);
	}

	public static DigesterContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		SimpleInventory<?> fuelInventory = SimpleInventory.createClientContents(DigesterTileEntity.FUEL_SLOTS);
		SimpleInventory<?> emptyBucketOutInventory = SimpleInventory.createClientContents(DigesterTileEntity.EMPTY_BUCKET_SLOTS);
		SimpleInventory<?> inputInventory = SimpleInventory.createClientContents(DigesterTileEntity.INPUT_SLOTS);
		SimpleInventory<?> outputInventory = SimpleInventory.createClientContents(DigesterTileEntity.OUTPUT_SLOTS);
		SimpleInventory<?> emptyBucketInInventory = SimpleInventory.createClientContents(DigesterTileEntity.BUCKET_SLOTS);
		SimpleInventory<?> filledBucketOutInventory = SimpleInventory.createClientContents(DigesterTileEntity.BUCKET_SLOTS);
		DigesterStateData stateData = new DigesterStateData();
		return new DigesterContainer(screenId, playerInventory, fuelInventory, emptyBucketOutInventory, inputInventory, outputInventory, emptyBucketInInventory, filledBucketOutInventory, stateData);
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		//we don't check all three inventories because they all call the same method in the decomposer tile entity
		return inputInventory.stillValid(playerIn);
	}

	public float getCraftingProgressNormalized() {
		if (stateData.timeForCompletion == 0) return 0f;
		return MathHelper.clamp(stateData.timeElapsed / (float) stateData.timeForCompletion, 0f, 1f);
	}

	public FluidTank getFuelTank() {
		return stateData.fuelTank;
	}

	public FluidTank getOutputTank() {
		return stateData.outputTank;
	}

	/**
	 * copied from: https://github.com/TheGreyGhost/MinecraftByExample/blob/1-16-3-final/src/main/java/minecraftbyexample/mbe31_inventory_furnace/ContainerFurnace.java
	 */
	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = slots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		boolean successfulTransfer = false;
		SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);

		switch (sourceZone) {
			case OUTPUT_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, true) || mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, true);
				if (successfulTransfer) sourceSlot.onQuickCraft(sourceStack, copyOfSourceStack);
				break;

			case INPUT_ZONE:
			case FUEL_ZONE:
			case EMPTY_BUCKET_OUT_ZONE:
			case EMPTY_BUCKET_IN_ZONE:
			case FILLED_BUCKET_OUT_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false) || mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				break;

			case PLAYER_HOTBAR:
			case PLAYER_MAIN_INVENTORY:
				if (DigesterTileEntity.RECIPE_TYPE.getRecipeForItem(world, sourceStack).isPresent()) {
					successfulTransfer = mergeInto(SlotZone.INPUT_ZONE, sourceStack, false);
				}
				if (!successfulTransfer && DigesterTileEntity.VALID_FUEL_ITEM.test(sourceStack)) {
					successfulTransfer = mergeInto(SlotZone.FUEL_ZONE, sourceStack, true);
				}
				if (!successfulTransfer && HandlerBehaviors.FLUID_CONTAINER_ITEM_PREDICATE.test(sourceStack)) {
					successfulTransfer = mergeInto(SlotZone.EMPTY_BUCKET_IN_ZONE, sourceStack, true);
				}
				if (!successfulTransfer) {
					if (sourceZone == SlotZone.PLAYER_HOTBAR) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
					else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				}
				break;

			default:
				throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
		}

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
		else sourceSlot.setChanged();

		if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker(getClass().getSimpleName()), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR.lastIndexPlus1, 3 * 9),
		FUEL_ZONE(PLAYER_MAIN_INVENTORY.lastIndexPlus1, DigesterTileEntity.FUEL_SLOTS),
		INPUT_ZONE(FUEL_ZONE.lastIndexPlus1, DigesterTileEntity.INPUT_SLOTS),
		OUTPUT_ZONE(INPUT_ZONE.lastIndexPlus1, DigesterTileEntity.OUTPUT_SLOTS),
		EMPTY_BUCKET_OUT_ZONE(OUTPUT_ZONE.lastIndexPlus1, DigesterTileEntity.EMPTY_BUCKET_SLOTS),
		EMPTY_BUCKET_IN_ZONE(EMPTY_BUCKET_OUT_ZONE.lastIndexPlus1, DigesterTileEntity.BUCKET_SLOTS),
		FILLED_BUCKET_OUT_ZONE(EMPTY_BUCKET_IN_ZONE.lastIndexPlus1, DigesterTileEntity.BUCKET_SLOTS);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			this.slotCount = numberOfSlots;
			this.lastIndexPlus1 = firstIndex + numberOfSlots;
		}

		public static SlotZone getZoneFromIndex(int slotIndex) {
			for (SlotZone slotZone : SlotZone.values()) {
				if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
			}
			throw new IndexOutOfBoundsException("Unexpected slotIndex");
		}

		@Override
		public int getFirstIndex() {
			return firstIndex;
		}

		@Override
		public int getLastIndexPlus1() {
			return lastIndexPlus1;
		}

		@Override
		public int getSlotCount() {
			return slotCount;
		}
	}
}
