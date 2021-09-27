package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.tileentity.IOwnableTile;
import com.github.elenterius.biomancy.tileentity.SimpleOwnableTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
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
	protected static final VoxelShape BOTTOM_COLLISION_SHAPE = Block.box(0.0D, 0.1D, 0.0D, 16.0D, 3.0D, 16.0D);

	public OwnableTrapDoorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SENSITIVITY, UserSensitivity.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SENSITIVITY);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (!state.getValue(OPEN) && state.getValue(HALF) == Half.BOTTOM) {
			return hasCollision ? BOTTOM_COLLISION_SHAPE : VoxelShapes.empty(); //substitute collision shape to enable entity collision from below
			//Note: onEntityCollision is only called when the entity intersects a "block pos" (collided block positions ~= floor(entityAABB.minPos), ..., floor(entityAABB.maxPos))
		}
		return super.getCollisionShape(state, worldIn, pos, context);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		OwnableBlock.addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			OwnableBlock.attachDataToOwnableTile(worldIn, (SimpleOwnableTileEntity) tileEntity, placer, stack);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) { //only interact with authorized players
				if (player.isShiftKeyDown()) {
					state = state.cycle(SENSITIVITY);
					worldIn.setBlock(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
					if (state.getValue(WATERLOGGED)) {
						worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
					}
					return ActionResultType.sidedSuccess(worldIn.isClientSide);
				}

				state = state.cycle(OPEN);
				worldIn.setBlock(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
				if (state.getValue(WATERLOGGED)) {
					worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
				}
				playSound(player, worldIn, pos, state.getValue(OPEN));
				return ActionResultType.sidedSuccess(worldIn.isClientSide);
			}
		}
		if (state.getValue(WATERLOGGED)) {
			worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		return ActionResultType.PASS;
	}

	@Override
	protected void playSound(@Nullable PlayerEntity player, World worldIn, BlockPos pos, boolean isOpened) {
		worldIn.levelEvent(player, isOpened ? Constants.WorldEvents.WOODEN_TRAPDOOR_OPEN_SOUND : Constants.WorldEvents.WOODEN_TRAPDOOR_CLOSE_SOUND, pos, 0);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
		if (worldIn.isClientSide()) return;

		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			boolean isLocked = ((SimpleOwnableTileEntity) tileEntity).isLocked();

			//when the block is locked, check if th owner of the neighbor is authorized to interact with this block (only works with "direct" neighbors)
			if (isLocked && neighborBlock instanceof IOwnableBlock && worldIn.getBlockState(neighborPos).is(neighborBlock)) { //only allow "direct" neighbors
				TileEntity neighborTile = worldIn.getBlockEntity(neighborPos);
				if (neighborTile instanceof IOwnableTile) {
					Optional<UUID> neighborOwner = ((IOwnableTile) neighborTile).getOwner();
					if (neighborOwner.isPresent()) {
						isLocked = !((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(neighborOwner.get());
					}
				}
			}

			boolean isPowered = worldIn.hasNeighborSignal(pos);

			if (!isLocked) { //normal vanilla behavior
				if (isPowered != state.getValue(POWERED)) {
					if (isPowered != state.getValue(OPEN)) {
						state = state.setValue(OPEN, isPowered);
						playSound(null, worldIn, pos, isPowered);
					}
					worldIn.setBlock(pos, state.setValue(POWERED, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
					if (state.getValue(WATERLOGGED)) {
						worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
					}
				}
			}
			else {
				if (isPowered != state.getValue(POWERED)) {
					if (state.getValue(OPEN)) {
						state = state.setValue(OPEN, false);
						playSound(null, worldIn, pos, false);
					}
					else if (isPowered && !state.getValue(OPEN)) {
						worldIn.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					worldIn.setBlock(pos, state.setValue(POWERED, isPowered), Constants.BlockFlags.BLOCK_UPDATE);
					if (state.getValue(WATERLOGGED)) {
						worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
					}
				}
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (state.getValue(OPEN)) {
			UserSensitivity sensitivity = state.getValue(SENSITIVITY);
			if (!sensitivity.isNone()) {
				TileEntity tileEntity = worldIn.getBlockEntity(pos);
				if (tileEntity instanceof SimpleOwnableTileEntity) {
					AxisAlignedBB aabb = INFLATED_AABB_VOLUME.move(pos);
					List<LivingEntity> list = worldIn.getEntitiesOfClass(LivingEntity.class, aabb);
					if (!list.isEmpty()) {
						for (Entity entity : list) {
							if (!entity.isSteppingCarefully()) {
								if (sensitivity == UserSensitivity.UNAUTHORIZED) {
									if (!((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(entity.getUUID())) {
										// if block is inverted keep door open when not authorized (trap mode)
										worldIn.getBlockTicks().scheduleTick(pos, state.getBlock(), 60); //schedule tick for the next close attempt (~3sec)
										return;
									}
								}
								else {
									if (((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(entity.getUUID())) {
										// when normal only keep door open for authorized users
										worldIn.getBlockTicks().scheduleTick(pos, state.getBlock(), 60); //schedule tick for the next close attempt (~3sec)
										return;
									}
								}
							}
						}
					}
				}
			}

			worldIn.setBlock(pos, state.setValue(OPEN, false), Constants.BlockFlags.BLOCK_UPDATE);
			worldIn.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public void stepOn(World worldIn, BlockPos pos, Entity entityIn) {
		if (worldIn.isClientSide()) return;
		openDoorOnEntityCollision(worldIn.getBlockState(pos), worldIn, pos, entityIn);
	}

	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		openDoorOnEntityCollision(state, worldIn, pos, entityIn);
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity) {
		return false;
	}

	protected void openDoorOnEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isClientSide() && entityIn instanceof LivingEntity && !state.getValue(OPEN)) {
			UserSensitivity sensitivity = state.getValue(SENSITIVITY);
			if (sensitivity.isNone()) return;

			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof SimpleOwnableTileEntity) {
				SimpleOwnableTileEntity ownableTile = (SimpleOwnableTileEntity) tileEntity;

				AxisAlignedBB aabb = state.getValue(HALF) == Half.TOP ? TOP_AABB_VOLUME.move(pos) : BOTTOM_AABB_VOLUME.move(pos);
				List<LivingEntity> list = worldIn.getEntitiesOfClass(LivingEntity.class, aabb);
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!entity.isSteppingCarefully()) {
							if (sensitivity == UserSensitivity.UNAUTHORIZED) {
								if (!ownableTile.isUserAuthorized(entity.getUUID())) {
									openTrapDoor(worldIn, state, pos, true);
									return;
								}
							}
							else if (ownableTile.isUserAuthorized(entity.getUUID())) {
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
		if (!worldIn.isClientSide() && !state.getValue(OPEN)) {
			state = state.setValue(OPEN, true);
			worldIn.setBlock(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
			if (autoClose) worldIn.getBlockTicks().scheduleTick(pos, state.getBlock(), 40); //schedule tick to auto-close door after (~2sec)
			if (state.getValue(WATERLOGGED)) {
				worldIn.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
			}
			worldIn.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		OwnableBlock.dropForCreativePlayer(worldIn, this, pos, player);
		super.playerWillDestroy(worldIn, pos, state, player);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(player)) { //only allow authorized players to mine the block
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SimpleOwnableTileEntity();
	}

}
