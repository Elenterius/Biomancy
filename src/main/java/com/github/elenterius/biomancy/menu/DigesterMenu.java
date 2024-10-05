package com.github.elenterius.biomancy.menu;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

public class DigesterMenu extends PlayerContainerMenu {

	protected final Level level;
	private final DigesterBlockEntity digester;

	protected DigesterMenu(int id, Inventory playerInventory, @Nullable DigesterBlockEntity digester) {
		super(ModMenuTypes.DIGESTER.get(), id, playerInventory, 111, 169);
		level = playerInventory.player.level();

		this.digester = digester;

		if (digester != null) {
			addSlot(new SlotItemHandler(digester.getFuelInventory(), 0, 39, 68));

			addSlot(new SlotItemHandler(digester.getInputInventory(), 0, 80, 24));

			IItemHandler itemHandler = digester.getOutputInventory();
			addSlot(new SlotItemHandler(itemHandler, 0, 69, 68));
			addSlot(new SlotItemHandler(itemHandler, 1, 91, 68));

			addDataSlots(digester.getStateData());
		}
	}

	public static DigesterMenu createServerMenu(int screenId, Inventory playerInventory, DigesterBlockEntity digester) {
		return new DigesterMenu(screenId, playerInventory, digester);
	}

	public static DigesterMenu createClientMenu(int screenId, Inventory playerInventory, FriendlyByteBuf extraData) {
		DigesterBlockEntity digester = playerInventory.player.level().getBlockEntity(extraData.readBlockPos()) instanceof DigesterBlockEntity be ? be : null;
		return new DigesterMenu(screenId, playerInventory, digester);
	}

	@Override
	public boolean stillValid(Player player) {
		return digester != null && digester.canPlayerInteract(player);
	}

	public float getCraftingProgressNormalized() {
		if (digester.getStateData().timeForCompletion == 0) return 0f;
		return Mth.clamp(digester.getStateData().timeElapsed / (float) digester.getStateData().timeForCompletion, 0f, 1f);
	}

	public float getFuelAmountNormalized() {
		return Mth.clamp((float) digester.getStateData().fuelHandler.getFuelAmount() / digester.getStateData().fuelHandler.getMaxFuelAmount(), 0f, 1f);
	}

	public int getFuelAmount() {
		return digester.getStateData().fuelHandler.getFuelAmount();
	}

	public int getMaxFuelAmount() {
		return digester.getStateData().fuelHandler.getMaxFuelAmount();
	}

	public int getFuelCost() {
		return digester.getStateData().getFuelCost();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stackInSlot = slot.getItem();
		ItemStack copyOfStack = stackInSlot.copy();

		SlotZone slotZone = SlotZone.getZoneFromIndex(index);
		boolean successfulTransfer = switch (slotZone) {
			case OUTPUT_ZONE -> mergeIntoEither(SlotZone.PLAYER_HOTBAR, SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, true);
			case INPUT_ZONE, FUEL_ZONE -> mergeIntoEither(SlotZone.PLAYER_MAIN_INVENTORY, SlotZone.PLAYER_HOTBAR, stackInSlot, false);
			case PLAYER_HOTBAR, PLAYER_MAIN_INVENTORY -> mergeIntoInputZone(stackInSlot) || mergeIntoFuelZone(stackInSlot) || mergeIntoPlayerZone(slotZone, stackInSlot);
		};

		if (!successfulTransfer) return ItemStack.EMPTY;
		if (slotZone == SlotZone.OUTPUT_ZONE) slot.onQuickCraft(stackInSlot, copyOfStack);

		if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();

		if (stackInSlot.getCount() == copyOfStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker("DecomposerMenu"), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		slot.onTake(player, stackInSlot);
		return copyOfStack;
	}

	private boolean mergeIntoInputZone(ItemStack stackInSlot) {
		if (DigesterBlockEntity.RECIPE_TYPE.get().getRecipeForIngredient(level, stackInSlot).isPresent()) {
			return mergeInto(SlotZone.INPUT_ZONE, stackInSlot, false);
		}
		return false;
	}

	private boolean mergeIntoFuelZone(ItemStack stackInSlot) {
		if (Nutrients.isValidFuel(stackInSlot)) {
			return mergeInto(SlotZone.FUEL_ZONE, stackInSlot, true);
		}
		return false;
	}

	private boolean mergeIntoPlayerZone(SlotZone slotZone, ItemStack stackInSlot) {
		if (slotZone == SlotZone.PLAYER_HOTBAR) {
			return mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, stackInSlot, false);
		}
		return mergeInto(SlotZone.PLAYER_HOTBAR, stackInSlot, false);
	}

	public enum SlotZone implements ISlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(PLAYER_HOTBAR, 3 * 9),
		FUEL_ZONE(PLAYER_MAIN_INVENTORY, DigesterBlockEntity.FUEL_SLOTS),
		INPUT_ZONE(FUEL_ZONE, DigesterBlockEntity.INPUT_SLOTS),
		OUTPUT_ZONE(INPUT_ZONE, DigesterBlockEntity.OUTPUT_SLOTS);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(SlotZone slotZone, int numberOfSlots) {
			this(slotZone.lastIndexPlus1, numberOfSlots);
		}

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			slotCount = numberOfSlots;
			lastIndexPlus1 = firstIndex + numberOfSlots;
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
		public int getLastIndexPlusOne() {
			return lastIndexPlus1;
		}

		@Override
		public int getSlotCount() {
			return slotCount;
		}

	}

}
