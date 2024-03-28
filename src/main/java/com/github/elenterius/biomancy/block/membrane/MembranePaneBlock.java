package com.github.elenterius.biomancy.block.membrane;

import com.github.elenterius.biomancy.block.PaneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MembranePaneBlock extends PaneBlock implements Membrane {

	protected final IgnoreEntityCollisionPredicate ignoreEntityCollisionPredicate;

	public MembranePaneBlock(Properties properties, IgnoreEntityCollisionPredicate predicate) {
		super(properties);
		ignoreEntityCollisionPredicate = predicate;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1f;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		entity.causeFallDamage(fallDistance, 0.2f, level.damageSources().fall());
	}

	@Override
	public boolean shouldIgnoreEntityCollisionAt(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
		return ignoreEntityCollisionPredicate.test(state, level, pos, entity);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext entityContext) {
			Entity entity = entityContext.getEntity();
			if (entity != null && shouldIgnoreEntityCollisionAt(state, level, pos, entity)) {
				return Shapes.empty();
			}
		}
		return state.getShape(level, pos);
	}

	/**
	 * WARNING: broken Forge API
	 * <p>
	 * Gets the path type of this block when an entity is pathfinding. When
	 * {@code null}, uses vanilla behavior.
	 *
	 * @param mob is always null
	 * @see <a href="https://github.com/MinecraftForge/MinecraftForge/issues/9283">Forge Issue 9283</a>
	 */
	@Override
	public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
		//		if (ignoreEntityCollisionPredicate.test(state, level, pos, mob)) return BlockPathTypes.DOOR_OPEN; //doesn't work due to broken Forge API
		return BlockPathTypes.STICKY_HONEY;
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return switch (type) {
			case LAND, AIR -> true;
			case WATER -> level.getFluidState(pos).is(FluidTags.WATER);
		};
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return (adjacentBlockState.is(this) && getOrientation(state) == getOrientation(adjacentBlockState)) || super.skipRendering(state, adjacentBlockState, side);
	}

}
