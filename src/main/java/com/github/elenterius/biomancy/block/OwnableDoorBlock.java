package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.IOwnableTile;
import com.github.elenterius.biomancy.tileentity.OwnableTileEntityDelegator;
import com.github.elenterius.biomancy.tileentity.SimpleOwnableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OwnableDoorBlock extends DoorBlock implements IOwnableBlock {

	public OwnableDoorBlock(Properties builder) {
		super(builder);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		OwnableBlock.addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		//we assume the pos of this method is always the origin position of the door (lower half)
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			OwnableBlock.attachDataToOwnableTile(worldIn, (SimpleOwnableTileEntity) tileEntity, placer, stack);
		}
		if (tileEntity instanceof OwnableTileEntityDelegator) {
			TileEntity main = worldIn.getTileEntity(pos.down());
			if (main instanceof SimpleOwnableTileEntity) {
				((OwnableTileEntityDelegator) tileEntity).setDelegate(main);
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		BlockPos tilePos = pos;
		if (state.get(HALF) == DoubleBlockHalf.UPPER) {
			tilePos = pos.down();
		}

		TileEntity tileEntity = worldIn.getTileEntity(tilePos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) { //only open/close door for authorized player
				state = state.cycleValue(OPEN);
				worldIn.setBlockState(pos, state, 10);
				worldIn.playEvent(player, state.get(OPEN) ? getOpenSound() : getCloseSound(), pos, 0);

				//handle connected door (open/close double door feature)
				boolean isRightHingeSide = state.get(HINGE) == DoorHingeSide.RIGHT;
				Direction direction = state.get(FACING);
				BlockPos connectedPos;
				switch (direction) {
					case EAST:
					default:
						connectedPos = pos.offset(isRightHingeSide ? Direction.NORTH : Direction.SOUTH);
						break;
					case SOUTH:
						connectedPos = pos.offset(isRightHingeSide ? Direction.EAST : Direction.WEST);
						break;
					case WEST:
						connectedPos = pos.offset(isRightHingeSide ? Direction.SOUTH : Direction.NORTH);
						break;
					case NORTH:
						connectedPos = pos.offset(isRightHingeSide ? Direction.WEST : Direction.EAST);
						break;
				}
				BlockState connectedState = worldIn.getBlockState(connectedPos);
				if (connectedState.matchesBlock(this) && connectedState.get(FACING) == direction && connectedState.get(HINGE) != state.get(HINGE)) { //check if it is a door with an opposite hinge
					tilePos = connectedPos;
					if (state.get(HALF) == DoubleBlockHalf.UPPER) {
						tilePos = connectedPos.down();
					}
					tileEntity = worldIn.getTileEntity(tilePos);
					if (tileEntity instanceof SimpleOwnableTileEntity) {
						if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) {
							boolean isOpen = state.get(OPEN);
							if (connectedState.get(OPEN) != isOpen) { //only updated connected door if its open state mismatches the targetState
								connectedState = connectedState.with(OPEN, isOpen);

								worldIn.setBlockState(connectedPos, connectedState, 10);
								worldIn.playEvent(player, isOpen ? getOpenSound() : getCloseSound(), connectedPos, 0);
							}
						}
					}
				}

				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockPos tilePos = pos;
		if (state.get(HALF) == DoubleBlockHalf.UPPER) {
			tilePos = pos.down();
		}
		OwnableBlock.dropForCreativePlayer(worldIn, this, tilePos, player);

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		BlockPos tilePos = pos;
		if (state.get(HALF) == DoubleBlockHalf.UPPER) {
			tilePos = pos.down();
		}

		TileEntity tileEntity = worldIn.getTileEntity(tilePos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(player)) { //only allow authorized players to mine the block
				return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
			}
		}
		return 0f;
	}

	@Override
	public void openDoor(World worldIn, BlockState state, BlockPos pos, boolean open) {
		if (state.matchesBlock(this) && state.get(OPEN) != open) {
			BlockPos tilePos = pos;
			DoubleBlockHalf half = state.get(HALF);
			if (half == DoubleBlockHalf.UPPER) {
				tilePos = pos.down();
			}

			TileEntity tileEntity = worldIn.getTileEntity(tilePos);
			if (tileEntity instanceof SimpleOwnableTileEntity) {
				if (!((SimpleOwnableTileEntity) tileEntity).isLocked()) {
					worldIn.setBlockState(pos, state.with(OPEN, open), 10);
					playOpenCloseSound(worldIn, pos, open);
				}
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
		if (worldIn.isRemote()) return;

		BlockPos tilePos = pos;
		DoubleBlockHalf half = state.get(HALF);
		if (half == DoubleBlockHalf.UPPER) {
			tilePos = pos.down();
		}

		TileEntity tileEntity = worldIn.getTileEntity(tilePos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			boolean isLocked = ((SimpleOwnableTileEntity) tileEntity).isLocked();

			//when the block is locked, check if th owner of the neighbor is authorized to interact with this block
			if (isLocked && neighborBlock instanceof IOwnableBlock) {
				if (worldIn.getBlockState(neighborPos).matchesBlock(neighborBlock)) { //only allow "direct" neighbors
					TileEntity neighborTile = worldIn.getTileEntity(neighborPos);
					if (neighborTile instanceof IOwnableTile) {
						Optional<UUID> neighborOwner = ((IOwnableTile) neighborTile).getOwner();
						if (neighborOwner.isPresent()) {
							isLocked = !((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(neighborOwner.get());
						}
					}
				}
			}

			boolean isPowered = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.offset(half == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));

			if (!isLocked) {
				if (neighborBlock != this && isPowered != state.get(OPEN)) { //force open the door
					if (isPowered != state.get(OPEN)) {
						playOpenCloseSound(worldIn, pos, isPowered);
					}
					worldIn.setBlockState(pos, state.with(POWERED, isPowered).with(OPEN, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
				}
			}
			else {
				if (neighborBlock != this && isPowered != state.get(POWERED)) {
					if (state.get(OPEN)) { //force close the door if open
						playOpenCloseSound(worldIn, pos, false);
						state = state.with(OPEN, false);
					}
					else if (isPowered && !state.get(OPEN)) {
						worldIn.playSound(null, pos, SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					worldIn.setBlockState(pos, state.with(POWERED, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
				}
			}
		}
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf half = stateIn.get(HALF);
		if ((facing.getAxis() == Direction.Axis.Y) && (half == DoubleBlockHalf.LOWER == (facing == Direction.UP))) { //check if current state is the lower half and facing state is the upper half
			if (facingState.matchesBlock(this) && facingState.get(HALF) != half) { //check if upper half is a different door half
				return stateIn.with(FACING, facingState.get(FACING)).with(OPEN, facingState.get(OPEN)).with(HINGE, facingState.get(HINGE)).with(POWERED, facingState.get(POWERED));
			}
			return Blocks.AIR.getDefaultState(); //top door half is missing, set the lower half to air as well
		}
		else {
			//don't check if door is standing on a block, let it float
			return stateIn;
		}
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	public void playOpenCloseSound(World worldIn, BlockPos pos, boolean isOpening) {
		worldIn.playEvent(null, isOpening ? getOpenSound() : getCloseSound(), pos, 0);
	}

	public int getCloseSound() {
		return Constants.WorldEvents.WOODEN_DOOR_CLOSE_SOUND;
	}

	public int getOpenSound() {
		return Constants.WorldEvents.WOODEN_DOOR_OPEN_SOUND;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return state.get(HALF) == DoubleBlockHalf.LOWER ? new SimpleOwnableTileEntity() : new OwnableTileEntityDelegator();
	}
}
