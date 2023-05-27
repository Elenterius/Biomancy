package com.github.elenterius.biomancy.block.mawhopper;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MawHopperBlock extends BaseEntityBlock {

	public static final EnumProperty<DirectedConnection> CONNECTION = EnumProperty.create("connection", DirectedConnection.class);
	public static final EnumProperty<VertexType> VERTEX_TYPE = EnumProperty.create("vertex", VertexType.class);

	public MawHopperBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(CONNECTION, DirectedConnection.UP_DOWN).setValue(VERTEX_TYPE, VertexType.SOURCE));
		MawHopperShapes.computePossibleShapes(stateDefinition.getPossibleStates());
	}

	public static DirectedConnection getConnection(BlockState state) {
		return state.getValue(CONNECTION);
	}

	public static VertexType getVertexType(BlockState state) {
		return state.getValue(VERTEX_TYPE);
	}

	private static boolean isOutgoingConnected(LevelAccessor level, BlockPos pos, DirectedConnection connection) {
		BlockState blockStateAtOutgoingPos = level.getBlockState(pos.relative(connection.outgoing));
		return blockStateAtOutgoingPos.getBlock() instanceof MawHopperBlock && getConnection(blockStateAtOutgoingPos).ingoing.getOpposite() == connection.outgoing;
	}

	private static boolean isIngoingConnected(LevelAccessor level, BlockPos pos, DirectedConnection connection) {
		BlockState blockStateAtOutgoingPos = level.getBlockState(pos.relative(connection.ingoing));
		return blockStateAtOutgoingPos.getBlock() instanceof MawHopperBlock && getConnection(blockStateAtOutgoingPos).outgoing.getOpposite() == connection.ingoing;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(CONNECTION, VERTEX_TYPE);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction ingoing;
		Direction outgoing;

		if (context.isSecondaryUseActive()) {
			ingoing = context.getClickedFace();
			outgoing = ingoing.getOpposite();
		}
		else {
			outgoing = context.getClickedFace();
			ingoing = outgoing.getOpposite();
		}

		DirectedConnection connection = DirectedConnection.from(ingoing, outgoing);

		BlockPos sourcePos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
		BlockState sourceState = context.getLevel().getBlockState(sourcePos);

		if (sourceState.getBlock() instanceof MawHopperBlock && getVertexType(sourceState) != VertexType.INNER) {
			DirectedConnection sourceConnection = getConnection(sourceState);
			Direction direction = ingoing.getOpposite();

			VertexType vertexType = VertexType.SOURCE;
			if (direction == sourceConnection.outgoing) {
				vertexType = VertexType.SINK;
			}
			return defaultBlockState().setValue(CONNECTION, connection).setValue(VERTEX_TYPE, vertexType);
		}

		return defaultBlockState().setValue(CONNECTION, connection).setValue(VERTEX_TYPE, VertexType.SOURCE);
	}

	@Override
	public BlockState updateShape(BlockState ourState, Direction updateDirection, BlockState theirState, LevelAccessor level, BlockPos ourPos, BlockPos theirPos) {
		DirectedConnection ourConnection = getConnection(ourState);
		boolean isUpdateFromIngoing = updateDirection == ourConnection.ingoing;
		boolean isUpdateFromOutgoing = updateDirection == ourConnection.outgoing;
		boolean isUpdateFromMaw = theirState.getBlock() instanceof MawHopperBlock;

		if (isUpdateFromMaw) {
			VertexType ourVertexType = getVertexType(ourState);

			if (ourVertexType == VertexType.INNER) return ourState;

			DirectedConnection theirConnection = getConnection(theirState);
			boolean theyPullFromUs = theirPos.relative(theirConnection.ingoing).equals(ourPos);
			boolean theyPushToUs = theirPos.relative(theirConnection.outgoing).equals(ourPos);

			if (theyPushToUs && !isUpdateFromOutgoing) {
				DirectedConnection connection = DirectedConnection.from(theirConnection.outgoing.getOpposite(), ourConnection.outgoing);

				boolean isOutgoingConnected = isOutgoingConnected(level, ourPos, ourConnection);
				VertexType vertexType = isOutgoingConnected ? VertexType.INNER : VertexType.SINK;

				return ourState.setValue(VERTEX_TYPE, vertexType).setValue(CONNECTION, connection);
			}

			if (theyPullFromUs && !isUpdateFromIngoing) {
				DirectedConnection connection = ourVertexType == VertexType.SOURCE ? ourConnection : DirectedConnection.from(ourConnection.ingoing, theirConnection.ingoing.getOpposite());

				boolean isIngoingConnected = isIngoingConnected(level, ourPos, ourConnection);
				VertexType vertexType = isIngoingConnected ? VertexType.INNER : VertexType.SOURCE;

				return ourState.setValue(VERTEX_TYPE, vertexType).setValue(CONNECTION, connection);
			}

			return ourState;
		}
		else {
			if (isUpdateFromIngoing) {
				return getBlockState(ourState, ourConnection, VertexType.SOURCE);
			}

			if (isUpdateFromOutgoing) {
				if (isIngoingConnected(level, ourPos, ourConnection)) {
					return ourState.setValue(VERTEX_TYPE, VertexType.SINK);
				}
				return getBlockState(ourState, ourConnection, VertexType.SOURCE);
			}
		}

		return ourState;
	}

	private BlockState getBlockState(BlockState blockState, DirectedConnection connection, VertexType vertexType) {
		if (vertexType == VertexType.SOURCE && connection.isCorner()) {
			connection = DirectedConnection.from(connection.outgoing.getOpposite(), connection.outgoing);
			blockState = blockState.setValue(CONNECTION, connection);
		}
		return blockState.setValue(VERTEX_TYPE, vertexType);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		//TODO: mark BlockEntity dirty?
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		//Neighbor BlockEntity changes
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (!level.isClientSide && level.getBlockEntity(pos) instanceof MawHopperBlockEntity blockEntity) {
				blockEntity.dropInventoryContents(level, pos);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (player.getItemInHand(hand).isEmpty() && level.getBlockEntity(pos) instanceof MawHopperBlockEntity blockEntity) {
			if (level.isClientSide) return InteractionResult.SUCCESS;
			blockEntity.giveInventoryContentsTo(level, pos, player);
			return InteractionResult.CONSUME;
		}
		return super.use(state, level, pos, player, hand, hit);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.MAW_HOPPER.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.MAW_HOPPER.get(), MawHopperBlockEntity::serverTick);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (getVertexType(state) != VertexType.SOURCE) return;

		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof MawHopperBlockEntity blockEntity) {
			MawHopperBlockEntity.entityInside(level, pos, state, blockEntity, entity);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return MawHopperShapes.getShape(state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(CONNECTION, getConnection(state).rotate(rotation));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(getConnection(state).ingoing));
	}

}
