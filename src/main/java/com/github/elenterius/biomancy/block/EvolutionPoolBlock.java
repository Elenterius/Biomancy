package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.tileentity.EvolutionPoolTileEntity;
import com.github.elenterius.biomancy.tileentity.OwnableTileEntityDelegator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class EvolutionPoolBlock extends OwnableContainerBlock {

	public static final int BLOCK_UPDATE_FLAG = Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.UPDATE_NEIGHBORS; // UPDATE_NEIGHBORS flag is named wrongly by forge, should be prefixed with negation

	public static final EnumProperty<MultiBlockPart> MULTI_BLOCK_PART = EnumProperty.create("part", MultiBlockPart.class);
	public static final BooleanProperty CONTROLLER = BooleanProperty.create("controller");
	public static final Direction8[] SORTED_POS_OFFSETS = new Direction8[]{Direction8.NORTH, Direction8.SOUTH, Direction8.WEST, Direction8.EAST, Direction8.NORTH_WEST, Direction8.NORTH_WEST, Direction8.SOUTH_WEST, Direction8.SOUTH_EAST};

	public static final VoxelShape FLOOR_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 3, 16);
	public static final VoxelShape SE_CORNER_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(0, 3, 0, 12, 16, 12), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);
	public static final VoxelShape SW_CORNER_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(4, 3, 0, 16, 16, 12), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);
	public static final VoxelShape NE_CORNER_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(0, 3, 4, 12, 16, 16), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);
	public static final VoxelShape NW_CORNER_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(4, 3, 4, 16, 16, 16), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);

	public static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(0, 3, 0, 12, 16, 16), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);
	public static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(4, 3, 0, 16, 16, 16), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);
	public static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(0, 3, 4, 16, 16, 16), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);
	public static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.combineAndSimplify(
			Block.makeCuboidShape(0, 3, 0, 16, 16, 12), VoxelShapes.fullCube(), IBooleanFunction.ONLY_SECOND);

	public EvolutionPoolBlock(Properties properties) {
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(MULTI_BLOCK_PART, MultiBlockPart.MIDDLE).with(CONTROLLER, false).with(MachineBlock.CRAFTING, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(MULTI_BLOCK_PART, CONTROLLER, MachineBlock.CRAFTING);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return state.get(CONTROLLER) ? new EvolutionPoolTileEntity() : new OwnableTileEntityDelegator();
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new OwnableTileEntityDelegator();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack(ModItems.FLESH_BLOCK_STAIRS.get());
	}

	@Nullable
	@Override
	public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof OwnableTileEntityDelegator) {
			tile = ((OwnableTileEntityDelegator) tile).getDelegate();
		}
		return tile instanceof INamedContainerProvider ? (INamedContainerProvider) tile : null;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote()) return ActionResultType.SUCCESS;

		INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
		if (containerProvider != null && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof OwnableTileEntityDelegator) {
			TileEntity delegate = ((OwnableTileEntityDelegator) tile).getDelegate();
			if (delegate instanceof EvolutionPoolTileEntity) {
				((EvolutionPoolTileEntity) delegate).removeSubTile((OwnableTileEntityDelegator) tile);
			}
		}
		else if (tile instanceof EvolutionPoolTileEntity) {
			((EvolutionPoolTileEntity) tile).scheduleMultiBlockDeconstruction(true);
		}
		worldIn.playEvent(player, 2001, pos, getStateId(state));
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof OwnableTileEntityDelegator) {
				TileEntity delegate = ((OwnableTileEntityDelegator) tile).getDelegate();
				if (delegate instanceof EvolutionPoolTileEntity) {
					((EvolutionPoolTileEntity) delegate).removeSubTile((OwnableTileEntityDelegator) tile);
				}
			}
			else if (tile instanceof EvolutionPoolTileEntity) {
				((EvolutionPoolTileEntity) tile).dropAllInvContents(worldIn, pos);
				((EvolutionPoolTileEntity) tile).scheduleMultiBlockDeconstruction(true);
			}
			if (state.get(MachineBlock.CRAFTING)) {
				worldIn.updateComparatorOutputLevel(pos, this);
			}
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof OwnableTileEntityDelegator) {
			TileEntity delegate = ((OwnableTileEntityDelegator) tile).getDelegate();
			if (delegate == null) {
				revertMultiBlockPart(worldIn, state, pos);
			}
		}
		else if (tile instanceof EvolutionPoolTileEntity) {
			if (!((EvolutionPoolTileEntity) tile).isValidMultiBlock()) {
				revertMultiBlockPart(worldIn, state, pos);
			}
		}
	}

	public static boolean tryToCreate2x2EvolutionPool(World world, BlockState stateIn, BlockPos pos1) {
		if (isValidStairsBlock(stateIn)) {
			final StairsShape stairsShape = stateIn.get(StairsBlock.SHAPE);
			if (stairsShape == StairsShape.INNER_LEFT || stairsShape == StairsShape.INNER_RIGHT) { // 1
				Direction direction1 = stateIn.get(StairsBlock.FACING).getOpposite();
				BlockPos pos2 = pos1.offset(direction1);
				BlockState nextState = world.getBlockState(pos2);
				if (isValidStairsBlock(nextState, stairsShape)) { // 2
					Direction direction2 = nextState.get(StairsBlock.FACING).getOpposite();
					BlockPos pos3 = pos2.offset(direction2);
					nextState = world.getBlockState(pos3);
					if (isValidStairsBlock(nextState, stairsShape)) { // 3
						Direction direction3 = nextState.get(StairsBlock.FACING).getOpposite();
						BlockPos pos4 = pos3.offset(direction3);
						nextState = world.getBlockState(pos4);
						if (isValidStairsBlock(nextState, stairsShape)) { // 4
							Direction direction4 = nextState.get(StairsBlock.FACING).getOpposite();
							if (pos4.offset(direction4).equals(pos1)) {
								world.setBlockState(pos1, getBlockStateFor(stairsShape, direction1).with(CONTROLLER, true), BLOCK_UPDATE_FLAG);
								world.setBlockState(pos2, getBlockStateFor(stairsShape, direction2), BLOCK_UPDATE_FLAG);
								world.setBlockState(pos3, getBlockStateFor(stairsShape, direction3), BLOCK_UPDATE_FLAG);
								world.setBlockState(pos4, getBlockStateFor(stairsShape, direction4), BLOCK_UPDATE_FLAG);
								TileEntity mainTile = world.getTileEntity(pos1);
								TileEntity subTile = world.getTileEntity(pos2);
								if (subTile instanceof OwnableTileEntityDelegator) {
									((OwnableTileEntityDelegator) subTile).setDelegate(mainTile);
									if (mainTile instanceof EvolutionPoolTileEntity) {
										((EvolutionPoolTileEntity) mainTile).addSubTile((OwnableTileEntityDelegator) subTile);
									}
								}
								subTile = world.getTileEntity(pos3);
								if (subTile instanceof OwnableTileEntityDelegator) {
									((OwnableTileEntityDelegator) subTile).setDelegate(mainTile);
									if (mainTile instanceof EvolutionPoolTileEntity) {
										((EvolutionPoolTileEntity) mainTile).addSubTile((OwnableTileEntityDelegator) subTile);
									}
								}
								subTile = world.getTileEntity(pos4);
								if (subTile instanceof OwnableTileEntityDelegator) {
									((OwnableTileEntityDelegator) subTile).setDelegate(mainTile);
									if (mainTile instanceof EvolutionPoolTileEntity) {
										((EvolutionPoolTileEntity) mainTile).addSubTile((OwnableTileEntityDelegator) subTile);
									}
								}

								if (mainTile instanceof EvolutionPoolTileEntity) {
									((EvolutionPoolTileEntity) mainTile).validateMultiBlock();
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static void revertMultiBlockPart(World world, BlockState state, BlockPos pos) {
		if (!state.matchesBlock(ModBlocks.EVOLUTION_POOL.get())) return;

		EvolutionPoolBlock.MultiBlockPart multiBlockPart = state.get(EvolutionPoolBlock.MULTI_BLOCK_PART);
		switch (multiBlockPart) {
			case NORTH_WEST_CORNER:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.SHAPE, StairsShape.INNER_LEFT).with(StairsBlock.FACING, Direction.NORTH), BLOCK_UPDATE_FLAG);
				break;
			case NORTH_WALL:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.FACING, Direction.NORTH), BLOCK_UPDATE_FLAG);
				break;
			case NORTH_EAST_CORNER:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.SHAPE, StairsShape.INNER_LEFT).with(StairsBlock.FACING, Direction.EAST), BLOCK_UPDATE_FLAG);
				break;
			case EAST_WALL:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.FACING, Direction.EAST), BLOCK_UPDATE_FLAG);
				break;
			case SOUTH_EAST_CORNER:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.SHAPE, StairsShape.INNER_LEFT).with(StairsBlock.FACING, Direction.SOUTH), BLOCK_UPDATE_FLAG);
				break;
			case SOUTH_WALL:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.FACING, Direction.SOUTH), BLOCK_UPDATE_FLAG);
				break;
			case SOUTH_WEST_CORNER:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.SHAPE, StairsShape.INNER_LEFT).with(StairsBlock.FACING, Direction.WEST), BLOCK_UPDATE_FLAG);
				break;
			case WEST_WALL:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_STAIRS.get().getDefaultState().with(StairsBlock.FACING, Direction.WEST), BLOCK_UPDATE_FLAG);
				break;
			case MIDDLE:
				world.setBlockState(pos, ModBlocks.FLESH_BLOCK_SLAB.get().getDefaultState(), BLOCK_UPDATE_FLAG);
				break;
		}
	}

	public static BlockState getBlockStateFor(StairsShape stairsShape, Direction direction) {
		BlockState defaultState = ModBlocks.EVOLUTION_POOL.get().getDefaultState();
		EvolutionPoolBlock.MultiBlockPart multiBlockPart = MultiBlockPart.MIDDLE;
		switch (direction) {
			case NORTH:
				multiBlockPart = stairsShape == StairsShape.INNER_LEFT ? MultiBlockPart.SOUTH_EAST_CORNER : MultiBlockPart.SOUTH_WEST_CORNER;
				break;
			case SOUTH:
				multiBlockPart = stairsShape == StairsShape.INNER_LEFT ? MultiBlockPart.NORTH_WEST_CORNER : MultiBlockPart.NORTH_EAST_CORNER;
				break;
			case WEST:
				multiBlockPart = stairsShape == StairsShape.INNER_LEFT ? MultiBlockPart.NORTH_EAST_CORNER : MultiBlockPart.SOUTH_EAST_CORNER;
				break;
			case EAST:
				multiBlockPart = stairsShape == StairsShape.INNER_LEFT ? MultiBlockPart.SOUTH_WEST_CORNER : MultiBlockPart.NORTH_WEST_CORNER;
				break;

			default:
				break;
		}
		return defaultState.with(EvolutionPoolBlock.MULTI_BLOCK_PART, multiBlockPart);
	}

	public static boolean isValidStairsBlock(BlockState stateIn) {
		return stateIn.matchesBlock(ModBlocks.FLESH_BLOCK_STAIRS.get()) && stateIn.get(StairsBlock.HALF) == Half.BOTTOM;
	}

	public static boolean isValidStairsBlock(BlockState stateIn, StairsShape stairsShapeIn) {
		return stateIn.matchesBlock(ModBlocks.FLESH_BLOCK_STAIRS.get()) && stateIn.get(StairsBlock.HALF) == Half.BOTTOM && stateIn.get(StairsBlock.SHAPE) == stairsShapeIn;
	}

	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote && entityIn instanceof ItemEntity && worldIn.getGameTime() % 10 == 0) {
			ItemStack stack = ((ItemEntity) entityIn).getItem();
			if (!stack.isEmpty()) {
				TileEntity tile = worldIn.getTileEntity(pos);
				if (tile instanceof OwnableTileEntityDelegator) {
					tile = ((OwnableTileEntityDelegator) tile).getDelegate();
				}
				if (tile instanceof EvolutionPoolTileEntity) {
					if (EvolutionPoolTileEntity.VALID_FUEL.test(stack)) {
						ItemStack remainder = ((EvolutionPoolTileEntity) tile).addFuel(stack);
						if (remainder.getCount() != stack.getCount()) {
							((ItemEntity) entityIn).setItem(remainder);
						}
					}
					else {
						LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
						capability.ifPresent(itemHandler -> {
							int slots = itemHandler.getSlots();
							ItemStack remainder = stack.copy();
							for (int i = 0; i < slots; i++) {
								if (itemHandler.isItemValid(i, remainder)) {
									remainder = itemHandler.insertItem(i, remainder, false);
								}
							}
							if (remainder.getCount() != stack.getCount()) {
								worldIn.playSound(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1f, false);
								((ItemEntity) entityIn).setItem(remainder);
							}
						});
					}
				}
			}
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return blockState.get(MachineBlock.CRAFTING) ? 15 : 0;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		MultiBlockPart part = state.get(MULTI_BLOCK_PART);
		switch (part) {
			case NORTH_WEST_CORNER:
				return NW_CORNER_SHAPE;
			case NORTH_WALL:
				return NORTH_WALL_SHAPE;
			case NORTH_EAST_CORNER:
				return NE_CORNER_SHAPE;
			case EAST_WALL:
				return EAST_WALL_SHAPE;
			case SOUTH_EAST_CORNER:
				return SE_CORNER_SHAPE;
			case SOUTH_WALL:
				return SOUTH_WALL_SHAPE;
			case SOUTH_WEST_CORNER:
				return SW_CORNER_SHAPE;
			case WEST_WALL:
				return WEST_WALL_SHAPE;
			case MIDDLE:
				return FLOOR_SHAPE;
		}
		return VoxelShapes.fullCube();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(4) == 0) {
			boolean isCrafting = stateIn.get(MachineBlock.CRAFTING);
			if (isCrafting) {
				int n = rand.nextInt(5);
				MultiBlockPart part = stateIn.get(MULTI_BLOCK_PART);
				double zOffset = part == MultiBlockPart.NORTH_EAST_CORNER || part == MultiBlockPart.NORTH_WEST_CORNER ? 1d : 0d;
				double xOffset = part == MultiBlockPart.NORTH_WEST_CORNER || part == MultiBlockPart.SOUTH_WEST_CORNER ? 1d : 0d;

				for (int i = 0; i < n; i++) {
					worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + xOffset + 0.75f * rand.nextFloat() - 0.3525d, pos.getY() + 0.3d, pos.getZ() + zOffset + 0.75f * rand.nextFloat() - 0.3525d, 1.376f, 1.588f, 1.227f);
				}
				if (n > 0 && rand.nextInt(4) == 0) {
					worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_TROPICAL_FISH_FLOP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
				}
			}
		}

		if (rand.nextInt(200) == 0) {
			worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public boolean isTransparent(BlockState state) {
		return true;
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, IBlockReader world, BlockPos pos, Entity collidingEntity) {
		return false;
	}

	public enum MultiBlockPart implements IStringSerializable {
		NORTH_WEST_CORNER("nw_corner"),
		NORTH_WALL("north_wall"),
		NORTH_EAST_CORNER("ne_corner"),
		EAST_WALL("east_wall"),
		SOUTH_EAST_CORNER("se_corner"),
		SOUTH_WALL("south_wall"),
		SOUTH_WEST_CORNER("sw_corner"),
		WEST_WALL("west_wall"),
		MIDDLE("middle");

		private final String name;

		MultiBlockPart(String name) {this.name = name;}

		@Override
		public String getString() {
			return name;
		}
	}
}
