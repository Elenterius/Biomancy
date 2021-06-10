package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.capabilities.InventoryProviders;
import com.github.elenterius.biomancy.capabilities.SpecialSingleItemStackHandler;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ItemStorageBagItem extends BagItem implements IKeyListener {

	public static final String NBT_KEY_INVENTORY = SpecialSingleItemStackHandler.NBT_KEY_INVENTORY;
	public static final String NBT_KEY_ITEM = SpecialSingleItemStackHandler.NBT_KEY_ITEM;
	public static final String NBT_KEY_AMOUNT = SpecialSingleItemStackHandler.NBT_KEY_AMOUNT;
	public static final String NBT_KEY_MAX_AMOUNT = SpecialSingleItemStackHandler.NBT_KEY_MAX_AMOUNT;
	public static final String NBT_KEY_IS_DIRTY = SpecialSingleItemStackHandler.NBT_KEY_IS_DIRTY;

	public ItemStorageBagItem(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return !stack.isEmpty() ? new InventoryProviders.ItemStackInvProvider(stack) : null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_INVENTORY)) {
			CompoundNBT invNbt = nbt.getCompound(NBT_KEY_INVENTORY);
			ItemStack storedStack = ItemStack.read(invNbt.getCompound(NBT_KEY_ITEM));
			int amount = invNbt.getShort(NBT_KEY_AMOUNT);
			if (!storedStack.isEmpty() && amount > 0) {
				int maxAmount = invNbt.getShort(NBT_KEY_MAX_AMOUNT);
				tooltip.add(TextUtil.getTooltipText("contains", storedStack.getDisplayName()).mergeStyle(TextFormatting.GRAY));
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
				tooltip.add(new StringTextComponent(df.format(amount) + "/" + df.format(maxAmount)).mergeStyle(TextFormatting.GRAY));
			}
			else tooltip.add(TextUtil.getTooltipText("contains_nothing").mergeStyle(TextFormatting.GRAY));
		}
		else tooltip.add(TextUtil.getTooltipText("contains_nothing").mergeStyle(TextFormatting.GRAY));

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		tooltip.add(new StringTextComponent("Mode: ").mergeStyle(TextFormatting.GRAY)
				.appendSibling(new TranslationTextComponent(getMode(stack).getTranslationKey()).mergeStyle(TextFormatting.AQUA)));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTooltipText("action_cycle")).mergeStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_INVENTORY)) {
			CompoundNBT invNbt = nbt.getCompound(NBT_KEY_INVENTORY);
			ItemStack storedStack = ItemStack.read(invNbt.getCompound(NBT_KEY_ITEM));
			if (!storedStack.isEmpty()) {
				int amount = invNbt.getShort(NBT_KEY_AMOUNT);
				return new StringTextComponent("").appendSibling(displayName).appendString(" (")
						.appendSibling(new TranslationTextComponent(getMode(stack).getTranslationKey()))
						.appendString(", " + amount + "x ").appendSibling(storedStack.getDisplayName()).appendString(")");
			}
		}

		return new StringTextComponent("").appendSibling(displayName).appendString(" (")
				.appendSibling(new TranslationTextComponent(getMode(stack).getTranslationKey())).appendString(", Empty)");
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, ClientWorld world, PlayerEntity player, byte flags) {
		Mode bagMode = getMode(stack).cycle();
		player.playSound(SoundEvents.ENTITY_GENERIC_HURT, 0.8F, 0.25f + world.rand.nextFloat() * 0.25f);
		return ActionResult.resultSuccess(bagMode.id);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		setMode(stack, Mode.fromId(flags));
	}

	@Override
	public float getFullness(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_INVENTORY)) {
			CompoundNBT invNBT = nbt.getCompound(NBT_KEY_INVENTORY);
			float amount = invNBT.getShort(NBT_KEY_AMOUNT);
			float maxAmount = invNBT.getShort(NBT_KEY_MAX_AMOUNT);
			return MathHelper.clamp(amount / maxAmount, 0f, 1f);
		}
		return 0f;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote() && handIn == Hand.MAIN_HAND && playerIn.isSneaking()) {
			ItemStack heldStack = playerIn.getHeldItem(handIn);
			onPlayerInteractWithItem(heldStack, playerIn);
			ItemStack offhandStack = playerIn.getHeldItemOffhand();

			if (!offhandStack.isEmpty()) { //extract stored item and put it in offhand
				if (offhandStack.getItem() != this) {
					LazyOptional<IItemHandler> capability = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
					capability.ifPresent(itemHandler -> {
						if (itemHandler instanceof SpecialSingleItemStackHandler) {
							int count = offhandStack.getCount();
							ItemStack remainder = itemHandler.insertItem(0, offhandStack.copy(), false);
							offhandStack.setCount(remainder.getCount());
							if (count != offhandStack.getCount()) {
								playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.8F, 0.25f + playerIn.world.rand.nextFloat() * 0.25f);
							}
						}
					});
					return ActionResult.resultFail(heldStack);
				}
			}
			else { //get item from offhand and store it
				LazyOptional<IItemHandler> capability = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				capability.ifPresent(itemHandler -> {
					if (itemHandler instanceof SpecialSingleItemStackHandler && !((SpecialSingleItemStackHandler) itemHandler).isEmpty()) {
						ItemStack result = itemHandler.extractItem(0, offhandStack.getMaxStackSize(), false);
						if (!result.isEmpty()) {
							playerIn.setHeldItem(Hand.OFF_HAND, result);
							playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.8F, 0.25f + playerIn.world.rand.nextFloat() * 0.25f);
						}
					}
				});
				return ActionResult.resultFail(heldStack);
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	public Mode getMode(ItemStack stack) {
		return Mode.deserialize(stack.getOrCreateTag());
	}

	public void setMode(ItemStack stack, Mode mode) {
		Mode.serialize(stack.getOrCreateTag(), mode);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (!context.getWorld().isRemote() && context.getPlayer() != null) {
			onPlayerInteractWithItem(stack, context.getPlayer());
		}

		return getMode(stack).onItemUseFirst(this, stack, context);
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote() && entityIn instanceof PlayerEntity && PlayerInventory.isHotbar(itemSlot) && worldIn.getGameTime() % 20L == 0L) {
//			onPlayerInteractWithItem(stack, context.getPlayer());
			getMode(stack).onInventoryTick(this, stack, (PlayerEntity) entityIn);
		}
	}

	public ActionResultType extractItemsFromTileEntity(ItemStack stack, ItemUseContext context) {
		final CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_INVENTORY)) {
			if (!context.getWorld().isRemote()) {
				final CompoundNBT invNbt = nbt.getCompound(NBT_KEY_INVENTORY);
				final int maxAmount = invNbt.getShort(NBT_KEY_MAX_AMOUNT);
				final int oldAmount = invNbt.getShort(NBT_KEY_AMOUNT);
				final ItemStack storedStack = ItemStack.read(invNbt.getCompound(NBT_KEY_ITEM));
				if (oldAmount < maxAmount && !storedStack.isEmpty()) {
					TileEntity tile = context.getWorld().getTileEntity(context.getPos());
					if (tile != null) {
						LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
						if (capability.isPresent()) {
							capability.ifPresent(itemHandler -> {
								int amount = oldAmount;
								int nSlots = itemHandler.getSlots();
								for (int i = 0; i < nSlots; i++) {
									if (!ItemHandlerHelper.canItemStacksStack(itemHandler.getStackInSlot(i), storedStack)) continue;
									int extractAmount = Math.min(itemHandler.getSlotLimit(i), maxAmount - amount);
									ItemStack result = itemHandler.extractItem(i, extractAmount, false);
									if (!result.isEmpty()) {
										amount += result.getCount();
									}
									if (amount >= maxAmount) break;
								}
								if (amount > oldAmount) {
									invNbt.putShort(NBT_KEY_AMOUNT, (short) MathHelper.clamp(amount, 0, maxAmount));
									invNbt.putBoolean(NBT_KEY_IS_DIRTY, true);
								}
							});
							context.getWorld().playSound(null, context.getPos(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.9F, 0.3f + context.getWorld().rand.nextFloat() * 0.25f);
							return ActionResultType.SUCCESS;
						}
						else return ActionResultType.FAIL;
					}
				}
			}
			return ActionResultType.func_233537_a_(context.getWorld().isRemote());
		}

		return ActionResultType.PASS;
	}

	public ActionResultType insertItemsIntoTileEntity(ItemStack stack, ItemUseContext context) {
		final CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_INVENTORY)) {
			if (!context.getWorld().isRemote()) {
				final CompoundNBT invNbt = nbt.getCompound(NBT_KEY_INVENTORY);
				if (invNbt.getShort(NBT_KEY_AMOUNT) > 0) {
					TileEntity tile = context.getWorld().getTileEntity(context.getPos());
					if (tile != null) {
						LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
						if (capability.isPresent()) {
							capability.ifPresent(itemHandler -> {
								ItemStack storedStack = ItemStack.read(invNbt.getCompound(NBT_KEY_ITEM));
								int amount = invNbt.getShort(NBT_KEY_AMOUNT);
								int nSlots = itemHandler.getSlots();
								for (int i = 0; i < nSlots; i++) {
									int insertAmount = Math.min(itemHandler.getSlotLimit(i), amount);
									ItemStack insertStack = ItemHandlerHelper.copyStackWithSize(storedStack, insertAmount);
									if (itemHandler.isItemValid(i, insertStack)) {
										ItemStack remainder = itemHandler.insertItem(i, insertStack, false);
										if (!remainder.isEmpty()) {
											insertAmount -= remainder.getCount();
										}
										amount -= insertAmount;
									}
									if (amount <= 0) break;
								}
								if (amount > 0) {
									invNbt.putShort(NBT_KEY_AMOUNT, (short) amount);
									invNbt.putBoolean(NBT_KEY_IS_DIRTY, true);
								}
								else nbt.remove(NBT_KEY_INVENTORY);
							});
							context.getWorld().playSound(null, context.getPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.8f, 0.25f + context.getWorld().rand.nextFloat() * 0.25f);
							return ActionResultType.SUCCESS;
						}
						else return ActionResultType.FAIL;
					}
				}
			}
			return ActionResultType.func_233537_a_(context.getWorld().isRemote());
		}

		return ActionResultType.PASS;
	}

	private void replenishItems(ItemStack stack, PlayerEntity player) {
		LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		capability.ifPresent(handler -> {
			if (handler instanceof SpecialSingleItemStackHandler) {
				SpecialSingleItemStackHandler itemHandler = (SpecialSingleItemStackHandler) handler;
				Iterable<ItemStack> heldEquipment = player.getHeldEquipment();
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
					NonNullList<ItemStack> inventory = player.inventory.mainInventory;
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
					player.inventory.markDirty();
				}
			}
		});
	}

	public enum Mode {
		NONE(0, (item, stack, player) -> {}, (item, stack, context) -> ActionResultType.PASS),
		DEVOUR(1, ItemStorageBagItem::storeItems, ItemStorageBagItem::extractItemsFromTileEntity),
		REPLENISH(2, ItemStorageBagItem::replenishItems, ItemStorageBagItem::insertItemsIntoTileEntity);

		final byte id;
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

}
