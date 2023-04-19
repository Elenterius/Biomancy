package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.IOwnableTile;
import com.github.elenterius.biomancy.tileentity.OwnableTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
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
		CompoundNBT nbt = stack.getTagElement("BlockEntityTag");
		if (nbt != null) {
			if (nbt.hasUUID(IOwnableBlock.NBT_KEY_OWNER)) {
				tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
				StringTextComponent ownerName = new StringTextComponent(ClientTextUtil.tryToGetPlayerNameOnClientSide(nbt.getUUID(IOwnableBlock.NBT_KEY_OWNER)));
				ownerName.withStyle(TextFormatting.WHITE);
				tooltip.add(ClientTextUtil.getTooltipText("owner", ownerName).withStyle(TextFormatting.GRAY));

				if (nbt.contains(IOwnableBlock.NBT_KEY_USER_LIST)) {
					ListNBT nbtList = nbt.getList(IOwnableBlock.NBT_KEY_USER_LIST, Constants.NBT.TAG_COMPOUND);
					tooltip.add(new StringTextComponent("Users: ").withStyle(TextFormatting.GRAY));
					int limit = Screen.hasControlDown() ? Math.min(5, nbtList.size()) : nbtList.size();
					for (int i = 0; i < limit; i++) {
						CompoundNBT userNbt = nbtList.getCompound(i);
						String userName = ClientTextUtil.tryToGetPlayerNameOnClientSide(userNbt.getUUID(IOwnableBlock.NBT_KEY_USER));
						UserAuthorization.AuthorityLevel level = UserAuthorization.AuthorityLevel.deserialize(userNbt);
						tooltip.add(new StringTextComponent(String.format(" - %s (%s)", userName, level.name().toLowerCase(Locale.ROOT))).withStyle(TextFormatting.GRAY));
					}
					int remainder = nbtList.size() - limit;
					if (remainder > 0) {
						tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getCtrlKey(), "show " + remainder + " more users..."));
					}
				}
			}
		}
	}

	public static void attachDataToOwnableTile(World worldIn, OwnableTileEntity ownableTile, @Nullable LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			ownableTile.setCustomName(stack.getHoverName());
		}

		CompoundNBT nbt = stack.getTagElement("BlockEntityTag"); //make sure we don't overwrite the previous owner
		if (nbt == null && !ownableTile.hasOwner() && placer instanceof PlayerEntity) {
			ownableTile.setOwner(placer.getUUID());
		}
	}

	public static void dropForCreativePlayer(World worldIn, Block block, BlockPos pos, PlayerEntity player) {
		if (!worldIn.isClientSide() && player.isCreative()) { // drop item for creative player
			if (worldIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
				TileEntity tileEntity = worldIn.getBlockEntity(pos);
				if (tileEntity instanceof OwnableTileEntity) {
					OwnableTileEntity tile = (OwnableTileEntity) tileEntity;

					ItemStack stack = new ItemStack(block);
					CompoundNBT nbt = tile.writeToItemBlockEntityTag(new CompoundNBT());
					if (!nbt.isEmpty()) {
						stack.addTagElement("BlockEntityTag", nbt);

						if (tile.hasCustomName()) {
							stack.setHoverName(tile.getCustomName());
						}

						ItemEntity itemEntity = new ItemEntity(worldIn, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack);
						itemEntity.setDefaultPickUpDelay();
						worldIn.addFreshEntity(itemEntity);
					}
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof OwnableTileEntity) {
			attachDataToOwnableTile(worldIn, (OwnableTileEntity) tileEntity, placer, stack);
		}
	}

	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		dropForCreativePlayer(worldIn, this, pos, player);
		super.playerWillDestroy(worldIn, pos, state, player);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof IOwnableTile) {
			if (((IOwnableTile) tileEntity).isUserAuthorized(player)) { //only allow authorized players to mine the block
				return super.getDestroyProgress(state, player, worldIn, pos);
			}
		}
		return 0f;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
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