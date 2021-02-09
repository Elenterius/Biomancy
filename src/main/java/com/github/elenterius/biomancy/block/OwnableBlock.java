package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.OwnableTileEntity;
import com.github.elenterius.biomancy.util.TooltipUtil;
import com.github.elenterius.biomancy.util.UserAuthorization;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public abstract class OwnableBlock extends Block implements IOwnableBlock {

	public OwnableBlock(Properties properties) {
		super(properties);
	}

	@OnlyIn(Dist.CLIENT)
	public static void addOwnableTooltip(ItemStack stack, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null) {
			if (nbt.hasUniqueId("OwnerUUID")) {
				tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
				StringTextComponent ownerName = new StringTextComponent(TooltipUtil.tryToGetPlayerNameOnClientSide(nbt.getUniqueId("OwnerUUID")));
				ownerName.mergeStyle(TextFormatting.WHITE);
				tooltip.add(new TranslationTextComponent("tooltip.biomancy.owner", ownerName).mergeStyle(TextFormatting.GRAY));

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
						tooltip.add(new TranslationTextComponent("tooltip.biomancy.press_button_to", TooltipUtil.CTRL_KEY_TEXT.mergeStyle(TextFormatting.AQUA), "show " + remainder + " more users..."));
					}
				}
			}
		}
	}

	public static void attachDataToOwnableTile(World worldIn, OwnableTileEntity ownableTile, @Nullable LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			ownableTile.setCustomName(stack.getDisplayName());
		}

		CompoundNBT nbt = stack.getChildTag("BlockEntityTag"); //make sure we don't overwrite the previous owner
		if (nbt == null && !ownableTile.hasOwner() && placer instanceof PlayerEntity) {
			ownableTile.setOwner(placer.getUniqueID());
		}
	}

	public static void dropForCreativePlayer(World worldIn, Block block, BlockPos pos, PlayerEntity player) {
		if (!worldIn.isRemote() && player.isCreative()) { // drop item for creative player
			if (worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);
				if (tileEntity instanceof OwnableTileEntity) {
					OwnableTileEntity tile = (OwnableTileEntity) tileEntity;

					ItemStack stack = new ItemStack(block);
					CompoundNBT nbt = tile.writeToItemBlockEntityTag(new CompoundNBT());
					if (!nbt.isEmpty()) {
						stack.setTagInfo("BlockEntityTag", nbt);

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

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof OwnableTileEntity) {
			attachDataToOwnableTile(worldIn, (OwnableTileEntity) tileEntity, placer, stack);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		dropForCreativePlayer(worldIn, this, pos, player);
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof OwnableTileEntity) {
			if (((OwnableTileEntity) tileEntity).isPlayerAuthorized(player)) { //only allow authorized players to mine the block
				return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
			}
		}
		return 0f;
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

}
