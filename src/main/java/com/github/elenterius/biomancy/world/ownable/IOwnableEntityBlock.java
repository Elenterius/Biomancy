package com.github.elenterius.biomancy.world.ownable;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TooltipHacks;
import com.github.elenterius.biomancy.world.block.entity.OwnableBlockEntity;
import com.github.elenterius.biomancy.world.permission.UserType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public interface IOwnableEntityBlock extends EntityBlock {

	String NBT_KEY_OWNER = "OwnerUUID";
	String NBT_KEY_USER_LIST = "UserList";
	String NBT_KEY_USER = "UserUUID";

	static void setupBlockEntityOwner(Level level, IOwnable ownable, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer == null) return;

		CompoundTag entityData = BlockItem.getBlockEntityData(stack);
		boolean containsOwner = entityData != null && entityData.hasUUID(IOwnableEntityBlock.NBT_KEY_OWNER);
		if (!containsOwner && !ownable.hasOwner()) { //make sure we don't overwrite the previous owner
			ownable.setOwner(placer.getUUID());
		}
	}

	static void dropForCreativePlayer(Level worldIn, Block block, BlockPos pos, Player player) {
		if (!worldIn.isClientSide() && player.isCreative()) { // drop item for creative player
			if (worldIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
				if (worldIn.getBlockEntity(pos) instanceof OwnableBlockEntity blockEntity) {
					ItemStack stack = new ItemStack(block);
					blockEntity.saveToItem(stack);
					if (stack.hasTag()) {
						ItemEntity itemEntity = new ItemEntity(worldIn, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack);
						itemEntity.setDefaultPickUpDelay();
						worldIn.addFreshEntity(itemEntity);
					}
				}
			}
		}
	}

	static void appendUserListToTooltip(ItemStack stack, List<Component> tooltip) {
		CompoundTag entityData = BlockItem.getBlockEntityData(stack);
		if (entityData == null) return;

		appendUserListToTooltip(entityData, tooltip);
	}

	static void appendUserListToTooltip(CompoundTag entityData, List<Component> tooltip) {

		String ownerName = "NULL";
		if (entityData.hasUUID(NBT_KEY_OWNER)) {
			ownerName = ClientTextUtil.tryToGetPlayerNameOnClientSide(entityData.getUUID(NBT_KEY_OWNER));
		}

		tooltip.add(TooltipHacks.EMPTY_LINE_COMPONENT);
		tooltip.add(TextComponentUtil.getTooltipText("owner", new TextComponent(ownerName).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));

		if (entityData.contains(NBT_KEY_USER_LIST)) {
			ListTag nbtList = entityData.getList(NBT_KEY_USER_LIST, Tag.TAG_COMPOUND);
			tooltip.add(new TextComponent("Users: ").withStyle(ChatFormatting.GRAY));
			int limit = Screen.hasControlDown() ? Math.min(5, nbtList.size()) : nbtList.size();
			for (int i = 0; i < limit; i++) {
				CompoundTag userNbt = nbtList.getCompound(i);
				String userName = ClientTextUtil.tryToGetPlayerNameOnClientSide(userNbt.getUUID(NBT_KEY_USER));
				UserType level = UserType.deserialize(userNbt);
				tooltip.add(new TextComponent(String.format(" - %s (%s)", userName, level.name().toLowerCase(Locale.ROOT))).withStyle(ChatFormatting.GRAY));
			}
			int remainder = nbtList.size() - limit;
			if (remainder > 0) {
				tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getCtrlKey(), "show " + remainder + " more users..."));
			}
		}
	}

}
