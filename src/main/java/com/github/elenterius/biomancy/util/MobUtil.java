package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.mixin.ServerWorldAccessor;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class MobUtil {

	private MobUtil() {}

	public static <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E entityIn, EntityType<T> outcomeType) {
		return convertMobEntityTo(world, entityIn, outcomeType, true);
	}

	public static <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E entityIn, EntityType<T> outcomeType, boolean copyEquipment) {
		return convertMobEntityTo(world, entityIn, outcomeType, copyEquipment, (oldEntity, outcome) -> {});
	}

	public static <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E oldEntity, EntityType<T> outcomeType, boolean copyEquipment, BiConsumer<E, T> onConvert) {
		if (ForgeEventFactory.canLivingConvert(oldEntity, outcomeType, timer -> {})) {
			T newEntity = oldEntity.convertTo(outcomeType, copyEquipment);// create new entity with same settings & equipment and remove old entity
			if (newEntity != null) {
				newEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(oldEntity.blockPosition()), SpawnReason.CONVERSION, null, null);
				newEntity.invulnerableTime = 60;
				onConvert.accept(oldEntity, newEntity);
				ForgeEventFactory.onLivingConvert(oldEntity, newEntity);
				return true;
			}
		}
		return false;
	}

	public static boolean convertLivingEntityTo(ServerWorld world, LivingEntity oldEntity, EntityType<?> outcomeType) {
		if (oldEntity.removed) return false;

		Entity entity = outcomeType.create(world);
		if (entity != null) {
			if (entity instanceof LivingEntity) {
				//noinspection unchecked
				EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>) outcomeType;
				if (ForgeEventFactory.canLivingConvert(oldEntity, entityType, timer -> {})) {
					entity.copyPosition(oldEntity);
					if (world.addFreshEntity(entity)) {
						oldEntity.remove();
						if (entity instanceof MobEntity) {
							((MobEntity) entity).finalizeSpawn(world, world.getCurrentDifficultyAt(oldEntity.blockPosition()), SpawnReason.CONVERSION, null, null);
						}
						entity.invulnerableTime = 60;
						ForgeEventFactory.onLivingConvert(oldEntity, (LivingEntity) entity);
						return true;
					}
				}
			}
			entity.remove();
		}

		return false;
	}

	public static <T extends MobEntity> boolean tryToSpawnEntitySafely(EntityType<T> entityType, ServerWorld world, PlayerEntity player, Vector3d hitVec, BlockPos blockPos, Direction facing, ItemStack stack) {
		if (world.mayInteract(player, blockPos) && player.mayUseItemAt(blockPos, facing, stack)) {
			T entity = entityType.create(world);
			if (entity != null) {
				Vector3d pos = getAdjustedSpawnPositionFor(blockPos, hitVec, facing, entity);
				entity.moveTo(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(world.random.nextFloat() * 360f), 0f);
				entity.yHeadRot = entity.yRot;
				entity.yBodyRot = entity.yRot;
				entity.setDeltaMovement(0, 0, 0);
				entity.fallDistance = 0;

				if (stack.hasCustomHoverName()) {
					entity.setCustomName(stack.getHoverName());
					entity.setCustomNameVisible(true);
				}

				if (world.addFreshEntity(entity)) {
					entity.playAmbientSound();
					return true;
				}
			}
		}
		return false;
	}

	public static <T extends Entity> Vector3d adjustSpawnPositionFor(Vector3d hitVec, Direction facing, T entity) {
		float yPos;
		if (facing.getStepY() < 0) yPos = -entity.getBbHeight();
		else if (facing.getStepY() > 0) yPos = 0f;
		else yPos = -(entity.getBbHeight() * 0.5f); // facing.getStepY() == 0

		float widthFactor = entity.getBbWidth() * 0.5f;
		return hitVec.add(facing.getStepX() * widthFactor, yPos, facing.getStepZ() * widthFactor);
	}

	/**
	 * This should return a position vector that can be used to more safely spawn entities as it should mitigate entities getting stuck in walls.<br>
	 *
	 * @return a position that is offset based on the hit direction and the entity size
	 */
	public static <T extends Entity> Vector3d getAdjustedSpawnPositionFor(BlockPos posVec, Vector3d hitVec, Direction facing, T entity) {

		//check if neighbor blocks are not empty
		BlockPos offsetPos = posVec.offset(facing.getNormal());
		int down = entity.level.getBlockState(offsetPos.below()).isAir() ? 0 : 1;
		int up = entity.level.getBlockState(offsetPos.above()).isAir() ? 0 : 1;
		int north = entity.level.getBlockState(offsetPos.north()).isAir() ? 0 : 1;
		int south = entity.level.getBlockState(offsetPos.south()).isAir() ? 0 : 1;
		int west = entity.level.getBlockState(offsetPos.west()).isAir() ? 0 : 1;
		int east = entity.level.getBlockState(offsetPos.east()).isAir() ? 0 : 1;

		float halfBBWidth = entity.getBbWidth() * 0.5f;
		float halfBBHeight = entity.getBbHeight() * 0.5f;
		double x = hitVec.x;
		double y = hitVec.y - halfBBHeight; //offset to center entity
		double z = hitVec.z;

		if (west + east == 2) x = offsetPos.getX() + 0.5d;
		else if (east == 1) {
			x = offsetPos.getX() + 1f - halfBBWidth;
			if (hitVec.x < x) x = hitVec.x;
		}
		else if (west == 1) {
			x = offsetPos.getX() + halfBBWidth;
			if (hitVec.x > x) x = hitVec.x;
		}

		if (north + south == 2) z = offsetPos.getZ() + 0.5d;
		else if (south == 1) {
			z = offsetPos.getZ() + 1f - halfBBWidth;
			if (hitVec.z < z) z = hitVec.z;
		}
		else if (north == 1) {
			z = offsetPos.getZ() + halfBBWidth;
			if (hitVec.z > z) z = hitVec.z;
		}

		if (down + up == 2) y = offsetPos.getY() + 0.5d - halfBBHeight;
		else if (down == 1) {
			y = offsetPos.getY();
			if (hitVec.y - halfBBHeight > y) y = hitVec.y - halfBBHeight;
		}
		else if (up == 1) {
			y = offsetPos.getY() + 1f - entity.getBbHeight();
			if (hitVec.y - halfBBHeight < y) y = hitVec.y - halfBBHeight;
		}

		return new Vector3d(x, y, z);
	}

	public static boolean hasDuplicateEntity(ServerWorld world, Entity entityIn) {
		return hasDuplicateEntity(world, entityIn.getUUID());
	}

	public static boolean hasDuplicateEntity(ServerWorld world, UUID uuid) {
		return ((ServerWorldAccessor) world).biomancy_getLoadedOrPendingEntity(uuid) != null;
	}

}
