package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.capabilities.InventoryProviders;
import com.github.elenterius.blightlings.capabilities.SingleItemStackHandler;
import com.github.elenterius.blightlings.init.ClientSetupHandler;
import com.github.elenterius.blightlings.util.TooltipUtil;
import net.minecraft.client.util.ITooltipFlag;
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
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class ItemStorageItem extends BagItem implements IKeyListener {

	public ItemStorageItem(Properties properties) {
		super(properties);
	}

	private static void replenishItems(PlayerEntity player, SingleItemStackHandler itemHandler) {
		Iterable<ItemStack> heldEquipment = player.getHeldEquipment();
		ItemStack unsafeStack = itemHandler.getStackInSlot(0);
		for (ItemStack activeItemStack : heldEquipment) {
			if (!activeItemStack.isEmpty() && itemHandler.getCount() > 0 && activeItemStack.getCount() < activeItemStack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(unsafeStack, activeItemStack)) {
				int replenishAmount = Math.min(8, activeItemStack.getMaxStackSize() - activeItemStack.getCount());
				ItemStack stack = itemHandler.extractItem(0, replenishAmount, false);
				activeItemStack.grow(stack.getCount());
			}
		}
	}

	private static void storeItems(PlayerEntity player, SingleItemStackHandler itemHandler) {
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

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return !stack.isEmpty() ? new InventoryProviders.ItemStackInvProvider(stack) : null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		CompoundNBT parentNBT = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		if (parentNBT.contains("Inventory")) {
			CompoundNBT wrapper = parentNBT.getCompound("Inventory");
			ItemStack storedStack = ItemStack.read(wrapper.getCompound("Item"));
			int amount = wrapper.getShort("Amount");
			if (!storedStack.isEmpty() && amount > 0) {
				int maxAmount = wrapper.getShort("MaxAmount");
				tooltip.add(new StringTextComponent("Contains: ").append(storedStack.getDisplayName()).mergeStyle(TextFormatting.GRAY));
				tooltip.add(new StringTextComponent(String.format("%d/%d", amount, maxAmount)).mergeStyle(TextFormatting.GRAY));
			}
			else tooltip.add(new StringTextComponent("Contains: Nothing"));
		}
		else tooltip.add(new StringTextComponent("Empty"));

		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		tooltip.add(new StringTextComponent("Mode: ").mergeStyle(TextFormatting.GRAY)
				.append(new TranslationTextComponent("enum.blightlings.mode." + getBagMode(stack).toString().toLowerCase(Locale.ROOT)).mergeStyle(TextFormatting.AQUA)));
		tooltip.add(new TranslationTextComponent("tooltip.blightlings.press_button_to", ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.func_238171_j_().copyRaw().mergeStyle(TextFormatting.AQUA), new TranslationTextComponent("tooltip.blightlings.action_toggle")).mergeStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		CompoundNBT parentNBT = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		if (parentNBT.contains("Inventory")) {
			CompoundNBT wrapper = parentNBT.getCompound("Inventory");
			ItemStack storedStack = ItemStack.read(wrapper.getCompound("Item"));
			if (!storedStack.isEmpty()) {
				int amount = wrapper.getShort("Amount");
				return new StringTextComponent("").append(displayName).appendString(" (")
						.append(new TranslationTextComponent("enum.blightlings.mode." + getBagMode(stack).toString().toLowerCase(Locale.ROOT))).appendString(", " + amount + "x ")
						.append(storedStack.getDisplayName()).appendString(")");
			}
		}

		return new StringTextComponent("").append(displayName).appendString(" (")
				.append(new TranslationTextComponent("enum.blightlings.mode." + getBagMode(stack).toString().toLowerCase(Locale.ROOT))).appendString(", Empty)");
	}

	@Override
	public float getFullness(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		if (nbt.contains("Inventory")) {
			CompoundNBT invNBT = nbt.getCompound("Inventory");
			float amount = invNBT.getShort("Amount");
			float maxAmount = invNBT.getShort("MaxAmount");
			return MathHelper.clamp(amount / maxAmount, 0f, 1f);
		}
		return 0f;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote() && handIn == Hand.MAIN_HAND) {
			ItemStack heldStack = playerIn.getHeldItem(handIn);
			onPlayerInteractWithItem(heldStack, playerIn);
			ItemStack offhandStack = playerIn.getHeldItemOffhand();
			if (!offhandStack.isEmpty() && offhandStack.getItem() != this) {
				LazyOptional<IItemHandler> capability = heldStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				capability.ifPresent(itemHandler -> {
					if (itemHandler instanceof SingleItemStackHandler && ((SingleItemStackHandler) itemHandler).isEmpty()) {
						ItemStack remainder = itemHandler.insertItem(0, offhandStack.copy(), false);
						offhandStack.setCount(remainder.getCount());
						playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.8F, 0.25f + playerIn.world.rand.nextFloat() * 0.25f);
					}
				});
				return ActionResult.resultFail(heldStack);
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (!context.getWorld().isRemote() && context.getPlayer() != null) {
			onPlayerInteractWithItem(stack, context.getPlayer());
		}

		final CompoundNBT nbt = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		Mode mode = Mode.deserialize(nbt);
		if (mode == Mode.REPLENISH) {
			if (nbt.contains("Inventory")) {
				if (!context.getWorld().isRemote()) {
					final CompoundNBT wrapper = nbt.getCompound("Inventory");
					if (wrapper.getShort("Amount") > 0) {
						TileEntity tile = context.getWorld().getTileEntity(context.getPos());
						if (tile != null) {
							LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
							if (capability.isPresent()) {
								capability.ifPresent(itemHandler -> {
									ItemStack storedStack = ItemStack.read(wrapper.getCompound("Item"));
									int amount = wrapper.getShort("Amount");
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
										wrapper.putShort("Amount", (short) amount);
										wrapper.putBoolean("IsDirty", true);
									}
									else nbt.remove("Inventory");
								});
								context.getWorld().playSound(null, context.getPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.8F, 0.25f + context.getWorld().rand.nextFloat() * 0.25f);
								return ActionResultType.SUCCESS;
							}
							else return ActionResultType.FAIL;
						}
					}
				}
				return ActionResultType.func_233537_a_(context.getWorld().isRemote());
			}
		}
		else if (mode == Mode.DEVOUR) {
			if (nbt.contains("Inventory")) {
				if (!context.getWorld().isRemote()) {
					final CompoundNBT wrapper = nbt.getCompound("Inventory");
					final int maxAmount = wrapper.getShort("MaxAmount");
					final int oldAmount = wrapper.getShort("Amount");
					final ItemStack storedStack = ItemStack.read(wrapper.getCompound("Item"));
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
										wrapper.putShort("Amount", (short) MathHelper.clamp(amount, 0, maxAmount));
										wrapper.putBoolean("IsDirty", true);
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
		}

		return ActionResultType.PASS;
	}

	public Mode getBagMode(ItemStack stack) {
		return Mode.deserialize(stack.getOrCreateChildTag(BlightlingsMod.MOD_ID));
	}

	public void setBagMode(ItemStack stack, Mode mode) {
		Mode.serialize(stack.getOrCreateChildTag(BlightlingsMod.MOD_ID), mode);
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote() && entityIn instanceof PlayerEntity && PlayerInventory.isHotbar(itemSlot) && worldIn.getGameTime() % 20L == 0L) {
//			onPlayerInteractWithItem(stack, context.getPlayer());
			Mode bagMode = getBagMode(stack);
			if (bagMode != Mode.NONE) {
				LazyOptional<IItemHandler> capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				capability.ifPresent(itemHandler -> {
					if (itemHandler instanceof SingleItemStackHandler) {
						bagMode.performAbility((PlayerEntity) entityIn, (SingleItemStackHandler) itemHandler);
					}
				});
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, World world, PlayerEntity player, byte flags) {
		Mode bagMode = getBagMode(stack).cycle();
		player.playSound(SoundEvents.ENTITY_GENERIC_HURT, 0.8F, 0.25f + world.rand.nextFloat() * 0.25f);
		return ActionResult.resultSuccess(bagMode.id);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		setBagMode(stack, Mode.fromId(flags));
	}

	public enum Mode {
		NONE(0, (player, itemHandler) -> {}), DEVOUR(1, ItemStorageItem::storeItems), REPLENISH(2, ItemStorageItem::replenishItems);

		final byte id;
		BiConsumer<PlayerEntity, SingleItemStackHandler> consumer;

		Mode(int id, BiConsumer<PlayerEntity, SingleItemStackHandler> consumer) {
			this.id = (byte) id;
			this.consumer = consumer;
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

		public void performAbility(PlayerEntity player, SingleItemStackHandler itemHandler) {
			consumer.accept(player, itemHandler);
		}

		public Mode cycle() {
			return fromId((byte) (id + 1));
		}
	}
}
