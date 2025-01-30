package com.github.elenterius.biomancy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterGelBlock extends HalfTransparentBlock {

	protected static final VoxelShape SHAPE = Block.box(1, 1, 1, 15, 15, 15);

	public WaterGelBlock(Properties properties) {
		super(properties);
	}

	public static boolean canEntityWalkOnWaterGel(Entity entity) {
		return entity instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.FEET).canWalkOnPowderedSnow(livingEntity);
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		entity.causeFallDamage(fallDistance, 0f, level.damageSources().fall());
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!(entity instanceof LivingEntity) || entity.getFeetBlockState().is(this)) {
			Vec3 delta = entity.getDeltaMovement();
			entity.setDeltaMovement(delta.multiply(0.8d, delta.y >= 0 ? 0.8f : 1.2f, 0.8d));
		}

		if (level.isClientSide()) return;

		if (entity instanceof LivingEntity livingEntity) {
			if (livingEntity.isSensitiveToWater()) {
				livingEntity.hurt(level.damageSources().indirectMagic(null, null), 1f);
			}

			if (livingEntity.isOnFire() && livingEntity.isAlive()) {
				livingEntity.extinguishFire();
			}

			if (livingEntity instanceof Axolotl axolotl) {
				axolotl.rehydrate();
			}
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext collision) {
			Entity entity = collision.getEntity();
			if (entity != null) {

				if (entity instanceof FallingBlockEntity) {
					return SHAPE;
				}

				if (entity.fallDistance > 0.25f) {
					return SHAPE;
				}

				Vec3 deltaMovement = entity.getDeltaMovement();
				double horizontalLengthSqr = deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z;

				if ((horizontalLengthSqr > 0.0057d || canEntityWalkOnWaterGel(entity)) && collision.isAbove(SHAPE, pos, false) && !collision.isDescending()) {
					return SHAPE;
				}
			}
		}

		return Shapes.empty();
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return Shapes.empty();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return true;
	}

}
