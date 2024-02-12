package com.github.elenterius.biomancy.block.membrane;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MembraneBlock extends HalfTransparentBlock {

	protected final IgnoreEntityCollisionPredicate ignoreEntityCollisionPredicate;

	public MembraneBlock(BlockBehaviour.Properties properties, IgnoreEntityCollisionPredicate predicate) {
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
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext entityContext) {
			Entity entity = entityContext.getEntity();
			if (ignoreEntityCollisionPredicate.test(state, level, pos, entity)) {
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

}
