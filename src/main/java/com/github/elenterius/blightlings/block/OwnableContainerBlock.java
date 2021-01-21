package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.tileentity.OwnableTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class OwnableContainerBlock extends ContainerBlock implements IOwnableBlock {

	protected OwnableContainerBlock(Properties builder) {
		super(builder);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		OwnableBlock.addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof OwnableTileEntity) {
			OwnableBlock.attachDataToOwnableTile(worldIn, (OwnableTileEntity) tileEntity, placer, stack);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		OwnableBlock.dropForCreativePlayer(worldIn, this, pos, player);
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

	@Nullable
	@Override
	public abstract TileEntity createNewTileEntity(IBlockReader worldIn);

}
