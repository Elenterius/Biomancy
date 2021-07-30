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
			T newEntity = oldEntity.func_233656_b_(outcomeType, copyEquipment);// create new entity with same settings & equipment and remove old entity
			if (newEntity != null) {
				newEntity.onInitialSpawn(world, world.getDifficultyForLocation(oldEntity.getPosition()), SpawnReason.CONVERSION, null, null);
				newEntity.hurtResistantTime = 60;
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
					entity.copyLocationAndAnglesFrom(oldEntity);
					if (world.addEntity(entity)) {
						oldEntity.remove();
						if (entity instanceof MobEntity) {
							((MobEntity) entity).onInitialSpawn(world, world.getDifficultyForLocation(oldEntity.getPosition()), SpawnReason.CONVERSION, null, null);
						}
						entity.hurtResistantTime = 60;
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
		if (world.isBlockModifiable(player, blockPos) && player.canPlayerEdit(blockPos, facing, stack)) {
			T entity = entityType.create(world);
			if (entity != null) {
				Vector3d pos = getSimpleOffsetPosition(hitVec, facing, entity);
				entity.setLocationAndAngles(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360f), 0f);
				entity.rotationYawHead = entity.rotationYaw;
				entity.renderYawOffset = entity.rotationYaw;
				entity.setMotion(0, 0, 0);
				entity.fallDistance = 0;

				if (stack.hasDisplayName()) {
					entity.setCustomName(stack.getDisplayName());
					entity.setCustomNameVisible(true);
				}

				if (world.addEntity(entity)) {
					entity.playAmbientSound();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This should return a modified hitVec that can be used to more safely spawn entities as it should prevent entities getting stuck in walls.<br>
	 *
	 * @return a position that is offset based on the hit direction and the entity size
	 */
	public static <T extends Entity> Vector3d getSimpleOffsetPosition(Vector3d hitVec, Direction facing, T entity) {
		float yPos;
		if (facing.getYOffset() < 0f) yPos = -entity.getHeight();
		else if (facing.getYOffset() > 0f) yPos = 0f;
		else yPos = entity.getHeight() * 0.5f;

		float widthFactor = entity.getWidth() * 0.6f; //prevent mobs from suffocating in walls as much as possible

		return hitVec.add(facing.getXOffset() * widthFactor, yPos, facing.getZOffset() * widthFactor);
	}

	public static boolean hasDuplicateEntity(ServerWorld world, Entity entityIn) {
		return hasDuplicateEntity(world, entityIn.getUniqueID());
	}

	public static boolean hasDuplicateEntity(ServerWorld world, UUID uuid) {
		return ((ServerWorldAccessor) world).biomancy_getLoadedOrPendingEntity(uuid) != null;
	}

}
