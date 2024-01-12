package com.github.elenterius.biomancy.block.neural;

import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.MobSpawnFilterShape;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import com.github.elenterius.biomancy.world.spatial.geometry.SphereShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class NeuralInterceptorBlock extends HorizontalDirectionalBlock {

	protected static final VoxelShape SHAPE = createShape();

	private static VoxelShape createShape() {
		VoxelShape a = Block.box(2, 0, 2, 14, 8, 14);
		VoxelShape b = Block.box(3, 8, 3, 13, 17, 13);
		return Shapes.join(a, b, BooleanOp.OR);
	}

	public NeuralInterceptorBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (level instanceof ServerLevel serverLevel) {
			SpatialShapeManager.getOrCreateShape(serverLevel, pos, () -> {
				SphereShape shape = new SphereShape(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 48);
				return new MobSpawnFilterShape(shape);
			});
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level instanceof ServerLevel serverLevel) {
				SpatialShapeManager.remove(serverLevel, pos);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (random.nextInt(4) == 0) {
			int particleAmount = random.nextInt(2, 8);
			int color = 0x9f4576; //magenta haze
			double r = (color >> 16 & 255) / 255d;
			double g = (color >> 8 & 255) / 255d;
			double b = (color & 255) / 255d;
			for (int i = 0; i < particleAmount; i++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + (random.nextFloat() * 0.8f + 0.1f), pos.getY() + 0.75f, pos.getZ() + (random.nextFloat() * 0.8f + 0.1f), r, g, b);
			}

			if (random.nextInt(3) == 0) {
				SoundUtil.clientPlayBlockSound(level, pos, SoundEvents.VILLAGER_AMBIENT, 0.6f, 0.6f);
			}
		}
	}

}
