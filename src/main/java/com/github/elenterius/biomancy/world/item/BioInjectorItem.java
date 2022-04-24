package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.inventory.ItemInventory;
import com.github.elenterius.biomancy.world.inventory.menu.BioInjectorMenu;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BioInjectorItem extends Item implements IKeyListener, IBiomancyItem {

	public static final int SLOTS = 5;
	public static final int MAX_SLOT_SIZE = 1;
	public static final String NBT_KEY_SERUM_AMOUNT = "SerumAmount";
	public static final String INVENTORY_TAG = "inventory";

	public BioInjectorItem(Properties properties) {
		super(properties);
	}

	public static boolean tryInjectLivingEntity(ServerLevel level, BlockPos pos, ItemStack stack) {
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos), EntitySelector.NO_SPECTATORS);
		if (!entities.isEmpty() && dispenserAffectEntity(level, stack, entities.get(0))) {
			level.playSound(null, pos, ModSoundEvents.INJECT.get(), SoundSource.BLOCKS, 0.8f, 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f);
			return true;
		}
		return false;
	}

	private static boolean dispenserAffectEntity(ServerLevel level, ItemStack stack, LivingEntity target) {
		BioInjectorItem item = (BioInjectorItem) stack.getItem();
		Serum serum = item.getSerum(stack);
		if (serum != null && serum.affectEntity(Serum.getDataTag(stack), null, target)) {
			if (serum.isAttributeModifier()) serum.applyAttributesModifiersToEntity(target);
			item.consumeSerum(stack, serum, null);
			level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, target.blockPosition(), 0);
			return true;
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, ClientLevel level, Player player, byte flags) {
		if (!interactWithPlayerSelf(stack, player)) {
			ModSoundEvents.playItemSFX(level, player, ModSoundEvents.FAIL);
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}
		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (interactWithPlayerSelf(stack, player)) {
			ModSoundEvents.playItemSFX(level, player, ModSoundEvents.INJECT.get());
		}
		else {
			ModSoundEvents.playItemSFX(level, player, ModSoundEvents.FAIL);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		Player player = context.getPlayer();
		if (player != null && !player.mayUseItemAt(context.getClickedPos().relative(context.getClickedFace()), context.getClickedFace(), stack))
			return InteractionResult.FAIL;

		Serum serum = getSerum(stack);
		if (serum != null) {
			Level level = context.getLevel();
			boolean success = serum.affectBlock(Serum.getDataTag(stack), player, level, context.getClickedPos(), context.getClickedFace());
			if (success) {
				if (!level.isClientSide) {
					if (player == null || !player.isCreative()) consumeSerum(stack, serum, player);
					level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, context.getClickedPos().above(), 0);
					if (player != null) {
						ModSoundEvents.playItemSFX(level, player, ModSoundEvents.INJECT.get());
						stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			if (level.isClientSide && player != null) ModSoundEvents.playItemSFX(level, player, ModSoundEvents.FAIL);
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		Serum serum = getSerum(stack);
		if (serum != null) {
			if (MobUtil.canPierceThroughArmor(stack, interactionTarget)) {
				if (serum.affectEntity(Serum.getDataTag(stack), player, interactionTarget)) {
					if (interactionTarget.level.isClientSide) return InteractionResult.SUCCESS;

					if (serum.isAttributeModifier()) serum.applyAttributesModifiersToEntity(interactionTarget);
					if (!player.isCreative()) consumeSerum(stack, serum, player);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

					interactionTarget.level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, interactionTarget.blockPosition(), 0);
					ModSoundEvents.playItemSFX(interactionTarget.level, player, ModSoundEvents.INJECT.get());
					return InteractionResult.CONSUME;
				}
			}
			else if (!player.level.isClientSide) {
				stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}

			if (player.level.isClientSide) ModSoundEvents.playItemSFX(player.level, player, ModSoundEvents.FAIL);
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	public boolean interactWithPlayerSelf(ItemStack stack, Player player) {
		Serum serum = getSerum(stack);
		if (serum != null) {
			if (MobUtil.canPierceThroughArmor(stack, player)) {
				boolean success = serum.affectPlayerSelf(Serum.getDataTag(stack), player);
				if (success && !player.level.isClientSide) {
					if (serum.isAttributeModifier()) serum.applyAttributesModifiersToEntity(player);
					if (!player.isCreative()) consumeSerum(stack, serum, player);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
				}
				return success;
			}

			if (!player.level.isClientSide) {
				stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
		}
		return false;
	}

	@Nullable
	public Serum getSerum(ItemStack stack) {
		Optional<IItemHandler> optional = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (optional.isPresent()) {
			IItemHandler itemHandler = optional.get();
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stackInSlot = itemHandler.getStackInSlot(i);
				if (stackInSlot.getItem() instanceof SerumItem) {
					return Serum.deserialize(stackInSlot.getOrCreateTag());
				}
			}
		}
		return null;
	}

	public int getSerumColor(ItemStack stack) {
		Serum serum = getSerum(stack);
		return serum != null ? serum.getColor() : -1;
	}

	public void consumeSerum(ItemStack stack, Serum serum, @Nullable Player player) {
		Optional<IItemHandler> optional = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (optional.isPresent()) {
			IItemHandler itemHandler = optional.get();
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stackInSlot = itemHandler.getStackInSlot(i);
				if (stackInSlot.getItem() instanceof SerumItem) {
					Serum foundSerum = Serum.deserialize(stackInSlot.getOrCreateTag());
					if (foundSerum == serum) {
						ItemStack serumStack = itemHandler.extractItem(i, 1, false);
						if (serumStack.hasContainerItem()) {
							ItemStack containerItem = serumStack.getContainerItem();
							ItemStack remainder = itemHandler.insertItem(i, containerItem, false);
							if (!remainder.isEmpty() && player != null && !player.addItem(remainder)) {
								player.drop(remainder, false);
							}
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.PIERCING || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));

		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(INVENTORY_TAG)) {
			CompoundTag inventory = tag.getCompound(INVENTORY_TAG);
			ListTag tagList = inventory.getList("Items", Tag.TAG_COMPOUND);
			tooltip.add(new TextComponent(String.format("Amount: %d/" + SLOTS, tagList.size())).withStyle(ChatFormatting.GRAY));

			Serum serum = getSerum(stack);
			if (serum != null) {
				serum.addInfoToTooltip(stack, level, tooltip, isAdvanced);
			}
		}
		else tooltip.add(TextComponentUtil.getTooltipText("contains_nothing").withStyle(ChatFormatting.GRAY));

		tooltip.add(TextComponent.EMPTY);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action_self_inject")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getShiftKey().append(" + ").append(ClientTextUtil.getRightMouseKey()), TextComponentUtil.getTooltipText("action_open_inventory")).withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		if (displayName instanceof MutableComponent mutableComponent) {
			Serum serum = getSerum(stack);
			if (serum != null) {
				return mutableComponent.append(" (").append(new TranslatableComponent(serum.getTranslationKey()).withStyle(ChatFormatting.AQUA)).append(")");
			}
		}
		return displayName;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if (player.isShiftKeyDown()) {
			if (!level.isClientSide) {
				ItemStack stack = player.getItemInHand(usedHand);
				ItemInventory inventory = ItemInventory.createServerContents(SLOTS, MAX_SLOT_SIZE, stack);
				MenuProvider container = new SimpleMenuProvider((id, playerInv, p) -> BioInjectorMenu.createServerMenu(id, playerInv, inventory), stack.getHoverName());
				NetworkHooks.openGui((ServerPlayer) player, container, byteBuf -> {
					byteBuf.writeByte(SLOTS);
					byteBuf.writeByte(MAX_SLOT_SIZE);
					byteBuf.writeItem(stack);
				});
			}
			else {
				player.playSound(SoundEvents.ARMOR_EQUIP_IRON, 1f, 1f);
			}
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
		}
		return InteractionResultHolder.pass(player.getItemInHand(usedHand));
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new InventoryCapability(stack);
	}

	private static class InventoryCapability implements ICapabilityProvider {

		private final ItemInventory itemHandler;

		public InventoryCapability(ItemStack stack) {
			itemHandler = ItemInventory.createServerContents(SLOTS, MAX_SLOT_SIZE, stack);
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, itemHandler.getOptionalItemHandler());
		}

	}

}
