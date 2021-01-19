package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.init.ModBlocks;
import com.github.elenterius.blightlings.tileentity.IOwnableTile;
import com.github.elenterius.blightlings.tileentity.SimpleOwnableTileEntity;
import com.github.elenterius.blightlings.util.TooltipUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class OwnableTrapDoorBlock extends TrapDoorBlock implements IOwnableBlock {

	public static final EnumProperty<UserSensitivity> SENSITIVITY = ModBlocks.USER_SENSITIVITY_PROPERTY;
	protected static final AxisAlignedBB TOP_AABB_VOLUME = new AxisAlignedBB(0d, 0.75d, 0d, 1d, 1.25d, 1d);
	protected static final AxisAlignedBB BOTTOM_AABB_VOLUME = new AxisAlignedBB(0d, -0.25d, 0d, 1d, 0.25d, 1d);
	protected static final AxisAlignedBB INFLATED_AABB_VOLUME = new AxisAlignedBB(-0.125d, -0.25d, -0.125d, 1.125d, 1.25d, 1.125d);
	protected static final VoxelShape BOTTOM_COLLISION_SHAPE = Block.makeCuboidShape(0.0D, 0.1D, 0.0D, 16.0D, 3.0D, 16.0D);

	public OwnableTrapDoorBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(SENSITIVITY, UserSensitivity.NONE));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(SENSITIVITY);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (!state.get(OPEN) && state.get(HALF) == Half.BOTTOM) {
			return canCollide ? BOTTOM_COLLISION_SHAPE : VoxelShapes.empty(); //substitute collision shape to enable entity collision from below
			//Note: onEntityCollision is only called when the entity intersects a "block pos" (collided block positions ~= floor(entityAABB.minPos), ..., floor(entityAABB.maxPos))
		}
		return super.getCollisionShape(state, worldIn, pos, context);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		OwnableBlock.addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			OwnableBlock.attachDataToOwnableTile(worldIn, (SimpleOwnableTileEntity) tileEntity, placer, stack);
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) { //only interact with authorized players
				if (player.isSneaking()) {
					state = state.func_235896_a_(SENSITIVITY);
					worldIn.setBlockState(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
					if (state.get(WATERLOGGED)) {
						worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
					}
					return ActionResultType.func_233537_a_(worldIn.isRemote);
				}

				state = state.func_235896_a_(OPEN);
				worldIn.setBlockState(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
				if (state.get(WATERLOGGED)) {
					worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
				}
				playSound(player, worldIn, pos, state.get(OPEN));
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
		}
		if (state.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return ActionResultType.PASS;
	}

	@Override
	protected void playSound(@Nullable PlayerEntity player, World worldIn, BlockPos pos, boolean isOpened) {
		worldIn.playEvent(player, isOpened ? Constants.WorldEvents.WOODEN_TRAPDOOR_OPEN_SOUND : Constants.WorldEvents.WOODEN_TRAPDOOR_CLOSE_SOUND, pos, 0);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
		if (worldIn.isRemote()) return;

		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			boolean isLocked = ((SimpleOwnableTileEntity) tileEntity).isLocked();

			//when the block is locked, check if th owner of the neighbor is authorized to interact with this block (only works with "direct" neighbors)
			if (isLocked && neighborBlock instanceof IOwnableBlock && worldIn.getBlockState(neighborPos).isIn(neighborBlock)) { //only allow "direct" neighbors
				TileEntity neighborTile = worldIn.getTileEntity(neighborPos);
				if (neighborTile instanceof IOwnableTile) {
					Optional<UUID> neighborOwner = ((IOwnableTile) neighborTile).getOwner();
					if (neighborOwner.isPresent()) {
						isLocked = !((SimpleOwnableTileEntity) tileEntity).isAuthorized(neighborOwner.get());
					}
				}
			}

			boolean isPowered = worldIn.isBlockPowered(pos);

			if (!isLocked) { //normal vanilla behavior
				if (isPowered != state.get(POWERED)) {
					if (isPowered != state.get(OPEN)) {
						state = state.with(OPEN, isPowered);
						playSound(null, worldIn, pos, isPowered);
					}
					worldIn.setBlockState(pos, state.with(POWERED, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
					if (state.get(WATERLOGGED)) {
						worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
					}
				}
			}
			else {
				if (isPowered != state.get(POWERED)) {
					if (state.get(OPEN)) {
						state = state.with(OPEN, false);
						playSound(null, worldIn, pos, false);
					}
					else if (isPowered && !state.get(OPEN)) {
						worldIn.playSound(null, pos, SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					worldIn.setBlockState(pos, state.with(POWERED, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
					if (state.get(WATERLOGGED)) {
						worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
					}
				}
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (state.get(OPEN)) {
			UserSensitivity sensitivity = state.get(SENSITIVITY);
			if (!sensitivity.isNone()) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);
				if (tileEntity instanceof SimpleOwnableTileEntity) {
					AxisAlignedBB aabb = INFLATED_AABB_VOLUME.offset(pos);
					List<LivingEntity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class, aabb);
					if (!list.isEmpty()) {
						for (Entity entity : list) {
							if (!entity.isSteppingCarefully()) {
								if (sensitivity == UserSensitivity.UNAUTHORIZED) {
									if (!((SimpleOwnableTileEntity) tileEntity).isAuthorized(entity.getUniqueID())) {
										// if block is inverted keep door open when not authorized (trap mode)
										worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 60); //schedule tick for the next close attempt (~3sec)
										return;
									}
								}
								else {
									if (((SimpleOwnableTileEntity) tileEntity).isAuthorized(entity.getUniqueID())) {
										// when normal only keep door open for authorized users
										worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 60); //schedule tick for the next close attempt (~3sec)
										return;
									}
								}
							}
						}
					}
				}
			}

			worldIn.setBlockState(pos, state.with(OPEN, false), Constants.BlockFlags.BLOCK_UPDATE);
			worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		if (worldIn.isRemote()) return;
		openDoorOnEntityCollision(worldIn.getBlockState(pos), worldIn, pos, entityIn);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		openDoorOnEntityCollision(state, worldIn, pos, entityIn);
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity) {
		return false;
	}

	protected void openDoorOnEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote() && entityIn instanceof LivingEntity && !state.get(OPEN)) {
			UserSensitivity sensitivity = state.get(SENSITIVITY);
			if (sensitivity.isNone()) return;

			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof SimpleOwnableTileEntity) {
				SimpleOwnableTileEntity ownableTile = (SimpleOwnableTileEntity) tileEntity;

				AxisAlignedBB aabb = state.get(HALF) == Half.TOP ? TOP_AABB_VOLUME.offset(pos) : BOTTOM_AABB_VOLUME.offset(pos);
				List<LivingEntity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class, aabb);
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!entity.isSteppingCarefully()) {
							if (sensitivity == UserSensitivity.UNAUTHORIZED) {
								if (!ownableTile.isAuthorized(entity.getUniqueID())) {
									openTrapDoor(worldIn, state, pos, true);
									return;
								}
							}
							else if (ownableTile.isAuthorized(entity.getUniqueID())) {
								openTrapDoor(worldIn, state, pos, true);
								return;
							}
						}
					}
				}
			}
		}
	}

	public void openTrapDoor(World worldIn, BlockState state, BlockPos pos, boolean autoClose) {
		if (!worldIn.isRemote() && !state.get(OPEN)) {
			state = state.with(OPEN, true);
			worldIn.setBlockState(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
			if (autoClose) worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 40); //schedule tick to auto-close door after (~2sec)
			if (state.get(WATERLOGGED)) {
				worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
			}
			worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).isPlayerAuthorized(player)) { //only allow authorized players to mine the block
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SimpleOwnableTileEntity();
	}

}
