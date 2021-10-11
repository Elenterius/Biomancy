package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.capabilities.InventoryProviders;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.ItemBagContainer;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.inventory.itemhandler.SpecialSingleItemStackHandler;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ItemStorageBagItem extends BagItem implements IKeyListener {

	public static final short SLOT_SIZE = 64 * 64; //4096
	public static final String STACK_NBT_KEY = "StackNbt";
	public static final String CAPABILITY_NBT_KEY = "CapNbt";
	public static final String UUID_NBT_KEY = "BagId";

	public ItemStorageBagItem(Properties properties) {
		super(properties);
	}

	@Nullable
	public static LargeSingleItemStackHandler getItemHandler(ItemStack stack) {
		LazyOptional<IItemHandler> lazyOptional = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if (lazyOptional.isPresent()) {
			IItemHandler itemHandler = lazyOptional.orElse(null);
			if (itemHandler instanceof LargeSingleItemStackHandler) return (LargeSingleItemStackHandler) itemHandler;
		}

		BiomancyMod.LOGGER.error(MarkerManager.getMarker("ItemStorageBagItem"), "Item is missing expected ITEM_HANDLER_CAPABILITY");
		return null;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return !stack.isEmpty() ? new InventoryProviders.LargeSingleItemHandlerProvider(SLOT_SIZE) : null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			ItemStack storedStack = itemHandler.getStack();
			if (!storedStack.isEmpty()) {
				int amount = itemHandler.getAmount();
				int maxAmount = itemHandler.getMaxAmount();
				tooltip.add(TextUtil.getTooltipText("contains", storedStack.getHoverName()).withStyle(TextFormatting.GRAY));
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				tooltip.add(new StringTextComponent(df.format(amount) + "/" + df.format(maxAmount)).withStyle(TextFormatting.GRAY));
			}
			else tooltip.add(TextUtil.getTooltipText("contains_nothing").withStyle(TextFormatting.GRAY));
		}
		else tooltip.add(TextUtil.getTooltipText("contains_nothing").withStyle(TextFormatting.GRAY));

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		tooltip.add(new StringTextComponent("Mode: ").withStyle(TextFormatting.GRAY)
				.append(new TranslationTextComponent(getMode(stack).getTranslationKey()).withStyle(TextFormatting.AQUA)));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTooltipText("action_cycle")).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			ItemStack storedStack = itemHandler.getStack();
			if (!storedStack.isEmpty()) {
				int amount = itemHandler.getAmount();
				return new StringTextComponent("").append(displayName).append(" (")
						.append(new TranslationTextComponent(getMode(stack).getTranslationKey()))
						.append(", " + amount + "x ").append(storedStack.getHoverName()).append(")");
			}
		}

		return new StringTextComponent("").append(displayName).append(" (")
				.append(new TranslationTextComponent(getMode(stack).getTranslationKey())).append(", Empty)");
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, ClientWorld world, PlayerEntity player, byte flags) {
		Mode bagMode = getMode(stack).cycle();
		player.playSound(SoundEvents.GENERIC_HURT, 0.8F, 0.25f + world.random.nextFloat() * 0.25f);
		return ActionResult.success(bagMode.id);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		setMode(stack, Mode.fromId(flags));
	}

	@Override
	public float getFullness(ItemStack stack) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			float amount = itemHandler.getAmount();
			float maxAmount = itemHandler.getMaxAmount();
			return MathHelper.clamp(amount / maxAmount, 0f, 1f);
		}
		return 0f;
	}

	public short getStoredItemAmount(ItemStack stack) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			return (short) itemHandler.getAmount();
		}
		return 0;
	}

	public short getStoredItemMaxAmount(ItemStack stack) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			return (short) itemHandler.getMaxAmount();
		}
		return 0;
	}

	public ItemStack getStoredItem(ItemStack stack) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			return itemHandler.getStack();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isClientSide()) {
			if (playerIn.isShiftKeyDown() && handIn == Hand.MAIN_HAND) {
				ItemStack heldStack = playerIn.getItemInHand(handIn);
				onPlayerInteractWithItem(heldStack, playerIn);
				ItemStack offhandStack = playerIn.getOffhandItem();

				if (!offhandStack.isEmpty()) {
					//get item from offhand and store it
					if (HandlerBehaviors.EMPTY_ITEM_INVENTORY_PREDICATE.test(offhandStack)) { //prevent nesting of items with non-empty inventories
						LargeSingleItemStackHandler itemHandler = getItemHandler(heldStack);
						if (itemHandler != null) {
							int count = offhandStack.getCount();
							ItemStack remainder = itemHandler.insertItem(0, offhandStack.copy(), false);
							offhandStack.setCount(remainder.getCount());
							if (count != offhandStack.getCount()) {
								playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.GENERIC_EAT, SoundCategory.PLAYERS, 0.8F, 0.25f + playerIn.level.random.nextFloat() * 0.25f);
							}
						}
						return ActionResult.fail(heldStack);
					}
				}
				else {
					//extract stored item and put it in offhand
					LargeSingleItemStackHandler itemHandler = getItemHandler(heldStack);
					if (itemHandler != null && !itemHandler.isEmpty()) {
						ItemStack result = itemHandler.extractItem(0, offhandStack.getMaxStackSize(), false);
						if (!result.isEmpty()) {
							playerIn.setItemInHand(Hand.OFF_HAND, result);
							playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.8F, 0.25f + playerIn.level.random.nextFloat() * 0.25f);
						}
					}
					return ActionResult.fail(heldStack);
				}
			}
			else {
				ItemStack heldStack = playerIn.getItemInHand(handIn);
				//TODO: rework?
//				CompoundNBT nbt = heldStack.getOrCreateTag();
//				UUID bagId;
//				if (nbt.hasUUID(UUID_NBT_KEY)) {
//					bagId = nbt.getUUID(UUID_NBT_KEY);
//				}
//				else {
//					bagId = UUID.randomUUID();
//					nbt.putUUID(UUID_NBT_KEY, bagId);
//				}
//				INamedContainerProvider containerProvider = new ItemBagContainerProvider<>(heldStack);
//				NetworkHooks.openGui((ServerPlayerEntity) playerIn, containerProvider, packetBuffer -> packetBuffer.writeUUID(bagId));
				return ActionResult.success(heldStack);
			}
		}
		return super.use(worldIn, playerIn, handIn);
	}

	public Mode getMode(ItemStack stack) {
		return Mode.deserialize(stack.getOrCreateTag());
	}

	public void setMode(ItemStack stack, Mode mode) {
		Mode.serialize(stack.getOrCreateTag(), mode);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null) {
			onPlayerInteractWithItem(stack, context.getPlayer());
		}

		return getMode(stack).onItemUseFirst(this, stack, context);
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isClientSide() && entityIn instanceof PlayerEntity && PlayerInventory.isHotbarSlot(itemSlot) && worldIn.getGameTime() % 20L == 0L) {
//			onPlayerInteractWithItem(stack, context.getPlayer());
			getMode(stack).onInventoryTick(this, stack, (PlayerEntity) entityIn);
		}
	}

	public ActionResultType extractItemsFromTileEntity(ItemStack stack, ItemUseContext context) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			if (!context.getLevel().isClientSide()) {
				final int maxAmount = itemHandler.getMaxAmount();
				final int oldAmount = itemHandler.getAmount();
				final ItemStack storedStack = itemHandler.getStack();
				if (oldAmount < maxAmount && !storedStack.isEmpty()) {
					TileEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
					if (tile != null) {
						LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
						if (capability.isPresent()) {
							capability.ifPresent(otherItemHandler -> {
								int amount = oldAmount;
								int nSlots = otherItemHandler.getSlots();
								for (int i = 0; i < nSlots; i++) {
									if (!ItemHandlerHelper.canItemStacksStack(otherItemHandler.getStackInSlot(i), storedStack)) continue;
									int extractAmount = Math.min(otherItemHandler.getSlotLimit(i), maxAmount - amount);
									ItemStack result = otherItemHandler.extractItem(i, extractAmount, false);
									if (!result.isEmpty()) {
										amount += result.getCount();
									}
									if (amount >= maxAmount) break;
								}
								if (amount > oldAmount) {
									itemHandler.setAmount((short) MathHelper.clamp(amount, 0, maxAmount));
								}
								tile.setChanged();
							});
							context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.GENERIC_EAT, SoundCategory.PLAYERS, 0.9F, 0.3f + context.getLevel().random.nextFloat() * 0.25f);
							return ActionResultType.SUCCESS;
						}
						else return ActionResultType.FAIL;
					}
				}
			}
			return ActionResultType.sidedSuccess(context.getLevel().isClientSide());
		}

		return ActionResultType.PASS;
	}

	public ActionResultType insertItemsIntoTileEntity(ItemStack stack, ItemUseContext context) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			if (!context.getLevel().isClientSide()) {
				ItemStack storedStack = itemHandler.getStack();
				if (!storedStack.isEmpty()) {
					TileEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
					if (tile != null) {
						LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
						if (capability.isPresent()) {
							capability.ifPresent(otherItemHandler -> {
								int amount = itemHandler.getAmount();
								int nSlots = otherItemHandler.getSlots();
								for (int i = 0; i < nSlots; i++) {
									int insertAmount = Math.min(otherItemHandler.getSlotLimit(i), amount);
									ItemStack insertStack = ItemHandlerHelper.copyStackWithSize(storedStack, insertAmount);
									if (otherItemHandler.isItemValid(i, insertStack)) {
										ItemStack remainder = otherItemHandler.insertItem(i, insertStack, false);
										if (!remainder.isEmpty()) {
											insertAmount -= remainder.getCount();
										}
										amount -= insertAmount;
									}
									if (amount <= 0) break;
								}
								if (amount > 0) {
									itemHandler.setAmount((short) amount);
								}
								else itemHandler.setStack(ItemStack.EMPTY);
								tile.setChanged();
							});
							context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.8f, 0.25f + context.getLevel().random.nextFloat() * 0.25f);
							return ActionResultType.SUCCESS;
						}
						else return ActionResultType.FAIL;
					}
				}
			}
			return ActionResultType.sidedSuccess(context.getLevel().isClientSide());
		}

		return ActionResultType.PASS;
	}

	private void replenishItems(ItemStack stack, PlayerEntity player) {
		LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		capability.ifPresent(handler -> {
			if (handler instanceof SpecialSingleItemStackHandler) {
				SpecialSingleItemStackHandler itemHandler = (SpecialSingleItemStackHandler) handler;
				Iterable<ItemStack> heldEquipment = player.getHandSlots();
				ItemStack unsafeStack = itemHandler.getStackInSlot(0);
				for (ItemStack activeItemStack : heldEquipment) {
					if (!activeItemStack.isEmpty() && itemHandler.getCount() > 0 && activeItemStack.getCount() < activeItemStack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(unsafeStack, activeItemStack)) {
						int replenishAmount = Math.min(8, activeItemStack.getMaxStackSize() - activeItemStack.getCount());
						ItemStack extracted = itemHandler.extractItem(0, replenishAmount, false);
						activeItemStack.grow(extracted.getCount());
					}
				}
			}
		});
	}

	private void storeItems(ItemStack stack, PlayerEntity player) {
		LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		capability.ifPresent(handler -> {
			if (handler instanceof SpecialSingleItemStackHandler) {
				SpecialSingleItemStackHandler itemHandler = (SpecialSingleItemStackHandler) handler;
				ItemStack unsafeStack = itemHandler.getStackInSlot(0);
				int maxStackSize = itemHandler.getSlotLimit(0);
				if (!unsafeStack.isEmpty() && itemHandler.getCount() < maxStackSize) {
					NonNullList<ItemStack> inventory = player.inventory.items;
					int counter = 0;
					for (int i = 0; i < inventory.size(); i++) {
						ItemStack currStack = inventory.get(i);
						if (ItemHandlerHelper.canItemStacksStack(currStack, unsafeStack)) {
							boolean overflow = currStack.getCount() > 8;
							if (overflow) {
								ItemStack insertStack = ItemHandlerHelper.copyStackWithSize(currStack, 8);
								counter += 8;
								ItemStack remainder = itemHandler.insertItem(0, insertStack, false);
								counter -= remainder.getCount();
								currStack.grow(-8 + remainder.getCount());
							}
							else {
								counter += currStack.getCount();
								ItemStack remainder = itemHandler.insertItem(0, currStack, false);
								counter -= remainder.getCount();
								inventory.set(i, remainder);
							}
						}
						if (counter >= 8 || itemHandler.getCount() >= maxStackSize) break;
					}
					player.inventory.setChanged();
				}
			}
		});
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
		if (nbt == null) {
			stack.setTag(null);
			return;
		}
		CompoundNBT stackNbt = nbt.getCompound(STACK_NBT_KEY);
		CompoundNBT cpaNbt = nbt.getCompound(CAPABILITY_NBT_KEY);
		stack.setTag(stackNbt);
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			itemHandler.deserializeNBT(cpaNbt);
		}
	}

	@Nullable
	@Override
	public CompoundNBT getShareTag(ItemStack stack) {
		LargeSingleItemStackHandler itemHandler = getItemHandler(stack);
		if (itemHandler != null) {
			CompoundNBT nbt = new CompoundNBT();
			CompoundNBT stackNbt = stack.getTag();
			CompoundNBT capNbt = itemHandler.serializeNBT();
			if (stackNbt != null) nbt.put(STACK_NBT_KEY, stackNbt);
			nbt.put(CAPABILITY_NBT_KEY, capNbt);
			return nbt;
		}

		return super.getShareTag(stack);
	}

	public enum Mode {
		NONE(0, (item, stack, player) -> {}, (item, stack, context) -> ActionResultType.PASS),
		DEVOUR(1, ItemStorageBagItem::storeItems, ItemStorageBagItem::extractItemsFromTileEntity),
		REPLENISH(2, ItemStorageBagItem::replenishItems, ItemStorageBagItem::insertItemsIntoTileEntity);

		public final byte id;
		final InventoryTickConsumer tickConsumer;
		final ItemUseFirstFunction useFirstFunction;

		Mode(int id, InventoryTickConsumer tickConsumer, ItemUseFirstFunction useFirstFunction) {
			this.id = (byte) id;
			this.tickConsumer = tickConsumer;
			this.useFirstFunction = useFirstFunction;
		}

		public static Mode fromId(byte id) {
			if (id < 0 || id >= values().length) return NONE;
			switch (id) {
				case 1:
					return DEVOUR;
				case 2:
					return REPLENISH;
				case 0:
				default:
					return NONE;
			}
		}

		public static void serialize(CompoundNBT nbt, Mode mode) {
			nbt.putByte("Mode", mode.id);
		}

		public static Mode deserialize(CompoundNBT nbt) {
			return Mode.fromId(nbt.getByte("Mode"));
		}

		public void onInventoryTick(ItemStorageBagItem item, ItemStack stack, PlayerEntity player) {
			tickConsumer.accept(item, stack, player);
		}

		public ActionResultType onItemUseFirst(ItemStorageBagItem item, ItemStack stack, ItemUseContext context) {
			return useFirstFunction.apply(item, stack, context);
		}

		public Mode cycle() {
			return fromId((byte) (id + 1));
		}

		public String getTranslationKey() {
			return "enum.biomancy.mode." + name().toLowerCase(Locale.ROOT);
		}

		@FunctionalInterface
		public interface InventoryTickConsumer {
			void accept(ItemStorageBagItem item, ItemStack stack, PlayerEntity player);
		}

		@FunctionalInterface
		public interface ItemUseFirstFunction {
			ActionResultType apply(ItemStorageBagItem item, ItemStack stack, ItemUseContext context);
		}
	}

	static class ItemBagContainerProvider<ISH extends IItemHandler & IItemHandlerModifiable & INBTSerializable<CompoundNBT>> implements INamedContainerProvider {

		private final ItemStack cachedStack;

		public ItemBagContainerProvider(ItemStack bagStackIn) {
			cachedStack = bagStackIn;
		}

		@Override
		public ITextComponent getDisplayName() {
			return cachedStack.getHoverName();
		}

		@Nullable
		@Override
		public Container createMenu(int screenId, PlayerInventory playerInv, PlayerEntity playerIn) {
			IItemHandler itemHandler = cachedStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (itemHandler instanceof IItemHandlerModifiable && itemHandler instanceof INBTSerializable) {
				SimpleInventory<ISH> inv = SimpleInventory.createServerContents((ISH) itemHandler, player -> true, () -> {});
				return ItemBagContainer.createServerContainer(screenId, playerInv, inv, cachedStack);
			}
			return null;
		}
	}

}
