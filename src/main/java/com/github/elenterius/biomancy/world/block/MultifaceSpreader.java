package com.github.elenterius.biomancy.world.block;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class MultifaceSpreader {
	public static final MultifaceSpreader.SpreadType[] DEFAULT_SPREAD_ORDER = new MultifaceSpreader.SpreadType[]{MultifaceSpreader.SpreadType.SAME_POSITION, MultifaceSpreader.SpreadType.SAME_PLANE, MultifaceSpreader.SpreadType.WRAP_AROUND};
	private static final Direction[] VALUES = Direction.values();
	private final MultifaceSpreader.SpreadConfig config;

	public MultifaceSpreader(FleshVeinsBlock block) {
		this(new MultifaceSpreader.DefaultSpreaderConfig(block));
	}

	public MultifaceSpreader(MultifaceSpreader.SpreadConfig pConfig) {
		this.config = pConfig;
	}

	private static Stream<Direction> stream() {
		return Stream.of(VALUES);
	}

	private static Collection<Direction> shuffledDirections(Random random) {
		ObjectArrayList<Direction> list = new ObjectArrayList<>(VALUES);

		int size = list.size();
		for (int i = size; i > 1; --i) {
			list.set(i - 1, list.set(random.nextInt(i), list.get(i - 1)));
		}

		return list;
	}

	public boolean canSpreadInAnyDirection(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pSpreadDirection) {
		return stream().anyMatch(direction -> getSpreadFromFaceTowardDirection(pState, pLevel, pPos, pSpreadDirection, direction, config::canSpreadInto).isPresent());
	}

	public Optional<MultifaceSpreader.SpreadPos> spreadFromRandomFaceTowardRandomDirection(BlockState state, LevelAccessor level, BlockPos pos, Random random) {
		return shuffledDirections(random).stream()
				.filter(direction -> config.canSpreadFrom(state, direction))
				.map(direction -> spreadFromFaceTowardRandomDirection(state, level, pos, direction, random))
				.filter(Optional::isPresent)
				.findFirst().orElse(Optional.empty());
	}

	public long spreadAll(BlockState state, LevelAccessor level, BlockPos pos) {
		return stream().filter(direction -> config.canSpreadFrom(state, direction))
				.map(direction -> spreadFromFaceTowardAllDirections(state, level, pos, direction))
				.reduce(0L, Long::sum);
	}

	public Optional<MultifaceSpreader.SpreadPos> spreadFromFaceTowardRandomDirection(BlockState state, LevelAccessor level, BlockPos pos, Direction spreadDirection, Random random) {
		return shuffledDirections(random).stream()
				.map(direction -> spreadFromFaceTowardDirection(state, level, pos, spreadDirection, direction))
				.filter(Optional::isPresent)
				.findFirst().orElse(Optional.empty());
	}

	private long spreadFromFaceTowardAllDirections(BlockState state, LevelAccessor level, BlockPos pos, Direction spreadDirection) {
		return stream().map(direction -> spreadFromFaceTowardDirection(state, level, pos, spreadDirection, direction)).filter(Optional::isPresent).count();
	}

	@VisibleForTesting
	public Optional<MultifaceSpreader.SpreadPos> spreadFromFaceTowardDirection(BlockState state, LevelAccessor level, BlockPos pos, Direction spreadDirection, Direction face) {
		return getSpreadFromFaceTowardDirection(state, level, pos, spreadDirection, face, config::canSpreadInto)
				.flatMap(spreadPos -> spreadToFace(level, spreadPos));
	}

	public Optional<MultifaceSpreader.SpreadPos> getSpreadFromFaceTowardDirection(BlockState state, BlockGetter level, BlockPos pos, Direction spreadDirection, Direction face, MultifaceSpreader.SpreadPredicate predicate) {
		if (face.getAxis() == spreadDirection.getAxis()) {
			return Optional.empty();
		}
		else if (config.isOtherBlockValidAsSource(state) || config.hasFace(state, spreadDirection) && !config.hasFace(state, face)) {
			for (MultifaceSpreader.SpreadType spreadType : config.getSpreadTypes()) {
				MultifaceSpreader.SpreadPos spreadPos = spreadType.getSpreadPos(pos, face, spreadDirection);
				if (predicate.test(level, pos, spreadPos)) return Optional.of(spreadPos);
			}
			return Optional.empty();
		}

		return Optional.empty();
	}

	public Optional<MultifaceSpreader.SpreadPos> spreadToFace(LevelAccessor level, MultifaceSpreader.SpreadPos pos) {
		BlockState state = level.getBlockState(pos.pos());
		return config.placeBlock(level, pos, state) ? Optional.of(pos) : Optional.empty();
	}

	public enum SpreadType {
		SAME_POSITION {
			public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
				return new MultifaceSpreader.SpreadPos(pos, face);
			}
		},
		SAME_PLANE {
			public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
				return new MultifaceSpreader.SpreadPos(pos.relative(face), spreadDirection);
			}
		},
		WRAP_AROUND {
			public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
				return new MultifaceSpreader.SpreadPos(pos.relative(face).relative(spreadDirection), face.getOpposite());
			}
		};

		public abstract MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection);
	}

	public interface SpreadConfig {
		@Nullable
		BlockState getStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos, Direction lookingDirection);

		boolean canSpreadInto(BlockGetter level, BlockPos pos, MultifaceSpreader.SpreadPos spreadPos);

		default MultifaceSpreader.SpreadType[] getSpreadTypes() {
			return MultifaceSpreader.DEFAULT_SPREAD_ORDER;
		}

		default boolean hasFace(BlockState state, Direction direction) {
			return FleshVeinsBlock.hasFace(state, direction);
		}

		default boolean isOtherBlockValidAsSource(BlockState otherBlock) {
			return false;
		}

		default boolean canSpreadFrom(BlockState state, Direction direction) {
			return isOtherBlockValidAsSource(state) || hasFace(state, direction);
		}

		default boolean placeBlock(LevelAccessor level, MultifaceSpreader.SpreadPos pos, BlockState state) {
			BlockState blockstate = getStateForPlacement(state, level, pos.pos(), pos.face());
			if (blockstate == null) return false;

			return level.setBlock(pos.pos(), blockstate, 2);
		}
	}

	@FunctionalInterface
	public interface SpreadPredicate {
		boolean test(BlockGetter level, BlockPos pos, MultifaceSpreader.SpreadPos spreadPos);
	}

	public static class DefaultSpreaderConfig implements MultifaceSpreader.SpreadConfig {
		protected FleshVeinsBlock block;

		public DefaultSpreaderConfig(FleshVeinsBlock pBlock) {
			block = pBlock;
		}

		@Nullable
		public BlockState getStateForPlacement(BlockState pCurrentState, BlockGetter pLevel, BlockPos pPos, Direction pLookingDirection) {
			return block.getStateForPlacement(pCurrentState, pLevel, pPos, pLookingDirection);
		}

		protected boolean stateCanBeReplaced(BlockGetter pLevel, BlockPos pos, BlockPos p_221690_, Direction p_221691_, BlockState state) {
			return state.isAir() || state.is(block) || state.is(Blocks.WATER) && state.getFluidState().isSource();
		}

		public boolean canSpreadInto(BlockGetter level, BlockPos pos, MultifaceSpreader.SpreadPos spreadPos) {
			BlockState blockstate = level.getBlockState(spreadPos.pos());
			return this.stateCanBeReplaced(level, pos, spreadPos.pos(), spreadPos.face(), blockstate) && block.isValidStateForPlacement(level, blockstate, spreadPos.pos(), spreadPos.face());
		}
	}

	public record SpreadPos(BlockPos pos, Direction face) {}
}