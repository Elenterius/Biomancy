package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.IOwnableTile;
import com.github.elenterius.biomancy.tileentity.OwnableTileEntityDelegator;
import com.github.elenterius.biomancy.tileentity.SimpleOwnableTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
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
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		OwnableBlock.addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		//we assume the pos of this method is always the origin position of the door (lower half)
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			OwnableBlock.attachDataToOwnableTile(worldIn, (SimpleOwnableTileEntity) tileEntity, placer, stack);
		}
		if (tileEntity instanceof OwnableTileEntityDelegator) {
			TileEntity main = worldIn.getBlockEntity(pos.below());
			if (main instanceof SimpleOwnableTileEntity) {
				((OwnableTileEntityDelegator) tileEntity).setDelegate(main);
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		BlockPos tilePos = pos;
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			tilePos = pos.below();
		}

		TileEntity tileEntity = worldIn.getBlockEntity(tilePos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) { //only open/close door for authorized player
				state = state.cycle(OPEN);
				worldIn.setBlock(pos, state, 10);
				worldIn.levelEvent(player, state.getValue(OPEN) ? getOpenSound() : getCloseSound(), pos, 0);

				//handle connected door (open/close double door feature)
				boolean isRightHingeSide = state.getValue(HINGE) == DoorHingeSide.RIGHT;
				Direction direction = state.getValue(FACING);
				BlockPos connectedPos;
				switch (direction) {
					case EAST:
					default:
						connectedPos = pos.relative(isRightHingeSide ? Direction.NORTH : Direction.SOUTH);
						break;
					case SOUTH:
						connectedPos = pos.relative(isRightHingeSide ? Direction.EAST : Direction.WEST);
						break;
					case WEST:
						connectedPos = pos.relative(isRightHingeSide ? Direction.SOUTH : Direction.NORTH);
						break;
					case NORTH:
						connectedPos = pos.relative(isRightHingeSide ? Direction.WEST : Direction.EAST);
						break;
				}
				BlockState connectedState = worldIn.getBlockState(connectedPos);
				if (connectedState.is(this) && connectedState.getValue(FACING) == direction && connectedState.getValue(HINGE) != state.getValue(HINGE)) { //check if it is a door with an opposite hinge
					tilePos = connectedPos;
					if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
						tilePos = connectedPos.below();
					}
					tileEntity = worldIn.getBlockEntity(tilePos);
					if (tileEntity instanceof SimpleOwnableTileEntity) {
						if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) {
							boolean isOpen = state.getValue(OPEN);
							if (connectedState.getValue(OPEN) != isOpen) { //only updated connected door if its open state mismatches the targetState
								connectedState = connectedState.setValue(OPEN, isOpen);

								worldIn.setBlock(connectedPos, connectedState, 10);
								worldIn.levelEvent(player, isOpen ? getOpenSound() : getCloseSound(), connectedPos, 0);
							}
						}
					}
				}

				return ActionResultType.sidedSuccess(worldIn.isClientSide);
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockPos tilePos = pos;
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			tilePos = pos.below();
		}
		OwnableBlock.dropForCreativePlayer(worldIn, this, tilePos, player);

		super.playerWillDestroy(worldIn, pos, state, player);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		BlockPos tilePos = pos;
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			tilePos = pos.below();
		}

		TileEntity tileEntity = worldIn.getBlockEntity(tilePos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(player)) { //only allow authorized players to mine the block
				return super.getDestroyProgress(state, player, worldIn, pos);
			}
		}
		return 0f;
	}

	@Override
	public void setOpen(World worldIn, BlockState state, BlockPos pos, boolean open) {
		if (state.is(this) && state.getValue(OPEN) != open) {
			BlockPos tilePos = pos;
			DoubleBlockHalf half = state.getValue(HALF);
			if (half == DoubleBlockHalf.UPPER) {
				tilePos = pos.below();
			}

			TileEntity tileEntity = worldIn.getBlockEntity(tilePos);
			if (tileEntity instanceof SimpleOwnableTileEntity) {
				if (!((SimpleOwnableTileEntity) tileEntity).isLocked()) {
					worldIn.setBlock(pos, state.setValue(OPEN, open), 10);
					playOpenCloseSound(worldIn, pos, open);
				}
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
		if (worldIn.isClientSide()) return;

		BlockPos tilePos = pos;
		DoubleBlockHalf half = state.getValue(HALF);
		if (half == DoubleBlockHalf.UPPER) {
			tilePos = pos.below();
		}

		TileEntity tileEntity = worldIn.getBlockEntity(tilePos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			boolean isLocked = ((SimpleOwnableTileEntity) tileEntity).isLocked();

			//when the block is locked, check if th owner of the neighbor is authorized to interact with this block
			if (isLocked && neighborBlock instanceof IOwnableBlock) {
				if (worldIn.getBlockState(neighborPos).is(neighborBlock)) { //only allow "direct" neighbors
					TileEntity neighborTile = worldIn.getBlockEntity(neighborPos);
					if (neighborTile instanceof IOwnableTile) {
						Optional<UUID> neighborOwner = ((IOwnableTile) neighborTile).getOwner();
						if (neighborOwner.isPresent()) {
							isLocked = !((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(neighborOwner.get());
						}
					}
				}
			}

			boolean isPowered = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.relative(half == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));

			if (!isLocked) {
				if (neighborBlock != this && isPowered != state.getValue(OPEN)) { //force open the door
					if (isPowered != state.getValue(OPEN)) {
						playOpenCloseSound(worldIn, pos, isPowered);
					}
					worldIn.setBlock(pos, state.setValue(POWERED, isPowered).setValue(OPEN, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
				}
			}
			else {
				if (neighborBlock != this && isPowered != state.getValue(POWERED)) {
					if (state.getValue(OPEN)) { //force close the door if open
						playOpenCloseSound(worldIn, pos, false);
						state = state.setValue(OPEN, false);
					}
					else if (isPowered && !state.getValue(OPEN)) {
						worldIn.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					worldIn.setBlock(pos, state.setValue(POWERED, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
				}
			}
		}
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf half = stateIn.getValue(HALF);
		if ((facing.getAxis() == Direction.Axis.Y) && (half == DoubleBlockHalf.LOWER == (facing == Direction.UP))) { //check if current state is the lower half and facing state is the upper half
			if (facingState.is(this) && facingState.getValue(HALF) != half) { //check if upper half is a different door half
				return stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, facingState.getValue(POWERED));
			}
			return Blocks.AIR.defaultBlockState(); //top door half is missing, set the lower half to air as well
		}
		else {
			//don't check if door is standing on a block, let it float
			return stateIn;
		}
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	public void playOpenCloseSound(World worldIn, BlockPos pos, boolean isOpening) {
		worldIn.levelEvent(null, isOpening ? getOpenSound() : getCloseSound(), pos, 0);
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
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new SimpleOwnableTileEntity() : new OwnableTileEntityDelegator();
	}
}
