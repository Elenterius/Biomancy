package com.github.elenterius.biomancy.block.vialholder;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VialHolderBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final BooleanProperty VIAL_0 = ModBlockProperties.VIAL_0;
	public static final BooleanProperty VIAL_1 = ModBlockProperties.VIAL_1;
	public static final BooleanProperty VIAL_2 = ModBlockProperties.VIAL_2;
	public static final BooleanProperty VIAL_3 = ModBlockProperties.VIAL_3;
	public static final BooleanProperty VIAL_4 = ModBlockProperties.VIAL_4;

	public static final VoxelShape SHAPE_NORTH = createShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createShape(Direction.EAST);

	protected static final BooleanProperty[] VIAL_PROPERTIES = {VIAL_0, VIAL_1, VIAL_2, VIAL_3, VIAL_4};

	public VialHolderBlock(Properties properties) {
		super(properties);

		BlockState defaultState = defaultBlockState();
		for (BooleanProperty vialProperty : VIAL_PROPERTIES) {
			defaultState = defaultState.setValue(vialProperty, false);
		}
		registerDefaultState(defaultState.setValue(FACING, Direction.NORTH));
	}

	public static VoxelShape createShape(Direction direction) {
		return VoxelShapeUtil.createYRotatedTowards(direction, 0, 6, 12, 16, 14, 16);
	}

	public static List<BooleanProperty> getVialProperties() {
		return List.of(VIAL_PROPERTIES);
	}

	public static Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}

	private static boolean hasVial(BlockState state, int index) {
		if (index < 0 || index >= VIAL_PROPERTIES.length) return false;
		return state.getValue(VIAL_PROPERTIES[index]);
	}

	public static int getTintColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
		if (level == null || pos == null || !hasVial(state, tintIndex)) return 0xFFFFFFFF;

		if (level.getBlockEntity(pos) instanceof VialHolderBlockEntity vialHolder) {
			return vialHolder.getVialColor(tintIndex);
		}

		return 0xFFFFFFFF;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING).add(VIAL_PROPERTIES);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction clickedFace = context.getClickedFace();

		if (clickedFace.getAxis().isHorizontal()) {
			return defaultBlockState().setValue(FACING, clickedFace);
		}
		else {
			return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.VIAL_HOLDER.get().create(pos, state);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof VialHolderBlockEntity vialHolder) {
			vialHolder.updateBlockState();
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

		if (level.getBlockEntity(pos) instanceof VialHolderBlockEntity vialHolder) {
			Direction facing = getFacing(state);
			Vec3 hitLocation = hit.getLocation();

			float v = (float) facing.getClockWise().getAxis().choose(hitLocation.x, hitLocation.y, hitLocation.z);
			v = Math.abs(v - Mth.floor(v));

			final int maxIndex = VIAL_PROPERTIES.length - 1;
			int index = 0;

			float min = 0.5f / 16f;
			float max = 15.5f / 16f;
			if (v >= max) {
				index = maxIndex;
			}
			else if (v > min) {
				v = (v - min) / (max - min);
				index = Mth.floor(v * VIAL_PROPERTIES.length);
			}

			if (facing == Direction.NORTH || facing == Direction.EAST) {
				index = maxIndex - index;
			}

			if (!vialHolder.isValidSlotIndex(index)) return InteractionResult.FAIL;

			boolean isVialSlotEmpty = !vialHolder.hasVial(index);
			ItemStack stackInHand = player.getItemInHand(hand);
			boolean isHandEmpty = stackInHand.isEmpty();

			if (isHandEmpty) {
				if (isVialSlotEmpty) return InteractionResult.FAIL;

				if (!level.isClientSide) vialHolder.extractVial(player, index);

			}
			else {
				if (!isVialSlotEmpty) return InteractionResult.FAIL;

				if (!level.isClientSide) {
					ItemStack remainder = vialHolder.insertVial(stackInHand, index);
					player.setItemInHand(hand, remainder);
				}
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof VialHolderBlockEntity vialHolder) {
				vialHolder.dropInventoryContents(level, pos, true);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(getFacing(state)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(getFacing(state)));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (getFacing(state)) {
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case WEST -> SHAPE_WEST;
			case EAST -> SHAPE_EAST;
			default -> Shapes.block();
		};
	}

}
