package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.tileentity.OwnableTileEntity;
import com.github.elenterius.blightlings.util.TooltipUtil;
import com.github.elenterius.blightlings.util.UserAuthorization;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public interface IOwnableBlock {

	@OnlyIn(Dist.CLIENT)
	static void addOwnableTooltip(ItemStack stack, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null) {
			if (nbt.hasUniqueId("OwnerUUID")) {
				StringTextComponent ownerName = new StringTextComponent(TooltipUtil.tryToGetPlayerNameOnClientSide(nbt.getUniqueId("OwnerUUID")));
				ownerName.mergeStyle(TextFormatting.WHITE);
				tooltip.add(new TranslationTextComponent("tooltip.blightlings.owner", ownerName).mergeStyle(TextFormatting.GRAY));

				if (nbt.contains("UserList")) {
					ListNBT nbtList = nbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
					tooltip.add(new StringTextComponent("Users: ").mergeStyle(TextFormatting.GRAY));
					int limit = Screen.hasControlDown() ? Math.min(5, nbtList.size()) : nbtList.size();
					for (int i = 0; i < limit; i++) {
						CompoundNBT userNbt = nbtList.getCompound(i);
						StringTextComponent userName = new StringTextComponent(TooltipUtil.tryToGetPlayerNameOnClientSide(userNbt.getUniqueId("UserUUID")));
						UserAuthorization.AuthorityType level = UserAuthorization.AuthorityType.deserialize(userNbt);
						tooltip.add(new StringTextComponent(String.format(" - %s (%s)", userName, level.name().toLowerCase(Locale.ROOT))).mergeStyle(TextFormatting.GRAY));
					}
					int remainder = nbtList.size() - limit;
					if (remainder > 0) {
						tooltip.add(new TranslationTextComponent("tooltip.blightlings.press_button_to", TooltipUtil.CTRL_KEY_TEXT.mergeStyle(TextFormatting.AQUA), "show " + remainder + " more users..."));
					}
				}
			}
		}
	}

	static void attachDataToOwnableTile(World worldIn, OwnableTileEntity ownableTile, @Nullable LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			ownableTile.setCustomName(stack.getDisplayName());
		}

		CompoundNBT nbt = stack.getChildTag("BlockEntityTag"); //make sure we don't overwrite the previous owner
		if (nbt == null && !ownableTile.hasOwner() && placer instanceof PlayerEntity) {
			ownableTile.setOwner(placer.getUniqueID());
		}
	}

	static void dropForCreativePlayer(World worldIn, Block block, BlockPos pos, PlayerEntity player) {
		if (!worldIn.isRemote() && player.isCreative()) { // drop item for creative player
			if (worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);
				if (tileEntity instanceof OwnableTileEntity) {
					OwnableTileEntity tile = (OwnableTileEntity) tileEntity;

					ItemStack stack = new ItemStack(block);
					CompoundNBT nbt = tile.writeToItemBlockEntityTag(new CompoundNBT());
					if (!nbt.isEmpty()) {
						stack.setTagInfo("BlockEntityTag", nbt);
					}

					if (tile.hasCustomName()) {
						stack.setDisplayName(tile.getCustomName());
					}

					ItemEntity itemEntity = new ItemEntity(worldIn, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack);
					itemEntity.setDefaultPickupDelay();
					worldIn.addEntity(itemEntity);
				}
			}
		}
	}
}
