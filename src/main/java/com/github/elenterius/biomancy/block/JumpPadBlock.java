package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.entity.misc.LivingEntityData;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

import java.util.EnumMap;
import java.util.Map;

public class JumpPadBlock extends MultifaceBlock {

	protected static final Map<Direction, VoxelShape> SHAPE_BY_FACE = Util.make(new EnumMap<>(Direction.class), map -> {
		map.put(Direction.UP, createFaceShape(Direction.UP));
		map.put(Direction.DOWN, createFaceShape(Direction.DOWN));
		map.put(Direction.NORTH, createFaceShape(Direction.NORTH));
		map.put(Direction.SOUTH, createFaceShape(Direction.SOUTH));
		map.put(Direction.EAST, createFaceShape(Direction.EAST));
		map.put(Direction.WEST, createFaceShape(Direction.WEST));
	});

	protected final ImmutableMap<BlockState, VoxelShape> shapesCache;
	private final MultifaceSpreader spreader = new MultifaceSpreader(this);

	public JumpPadBlock(Properties properties) {
		super(properties.noCollission().forceSolidOn());
		shapesCache = getShapeForEachState(JumpPadBlock::calculateMultifaceShape);
	}

	/**
	 * @param face this is not the direction but the face that is located at that position
	 */
	private static VoxelShape createFaceShape(Direction face) {
		return VoxelShapeUtil.createXZRotatedTowards(face, 2, 14, 2, 14, 16, 14);
	}

	private static VoxelShape calculateMultifaceShape(BlockState state) {
		VoxelShape voxelshape = Shapes.empty();

		for (Direction face : DIRECTIONS) {
			if (hasFace(state, face)) {
				voxelshape = Shapes.or(voxelshape, SHAPE_BY_FACE.get(face));
			}
		}

		return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {

		AABB entityAABB = entity.getBoundingBox().inflate(0.0625d);  // instead of inflating the block face AABB in the loop we inflate the entity AABB by 1/16
		boolean isInsideBounds = false;
		Vector3d impulse = new Vector3d();

		for (Direction face : Direction.values()) {
			if (hasFace(state, face)) {

				if (!isInsideBounds && SHAPE_BY_FACE.get(face).bounds().move(pos).intersects(entityAABB)) {
					isInsideBounds = true;
				}

				Vec3i normal = face.getOpposite().getNormal();
				impulse.add(normal.getX() * 2d, normal.getY(), normal.getZ() * 2d); // make horizontal movement stronger
			}
		}

		if (!isInsideBounds) return;

		entity.fallDistance = 0;
		if (entity.isSteppingCarefully() || entity.isSuppressingBounce()) return;

		double multiplier = entity instanceof LivingEntity ? 1d : 0.8d;

		Vec3 movement = entity.getDeltaMovement();
		impulse.normalize(Math.max(movement.length() * 0.9d, 2d) * multiplier);

		if (impulse.length() > 0d) {
			entity.setDeltaMovement(movement.add(impulse.x, impulse.y, impulse.z));
			entity.hasImpulse = true; //force sync movement to client
		}

		// to be able to properly launch entities from the ground we need to disable friction
		// we do this as well for unlaunched entities to allow players to exploit frictionless ground movement for other stuff
		if (entity instanceof LivingEntityData.TransientDataProvider provider) {
			// NOTE: we do not call the native method because we need to temporarily disable friction for the current and next tick,
			//       our LivingEventHandler will re-enable friction after the next tick
			provider.biomancy$getData().setDiscardFriction(true);
		}

		level.playSound(entity, pos, ModSoundEvents.FLESH_BLOB_JUMP.get(), SoundSource.BLOCKS, 0.5f, 1.2f);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return context.getItemInHand().is(asItem()) && super.canBeReplaced(state, context);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		//noinspection DataFlowIssue
		return shapesCache.get(state);
	}

	@Override
	public MultifaceSpreader getSpreader() {
		return spreader;
	}

}
