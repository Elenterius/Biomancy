package com.github.elenterius.biomancy.block.chrysalis;

import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface Chrysalis {
	String ENTITY_KEY = "entity_info";
	String ENTITY_NAME_KEY = "name";
	String ENTITY_DATA_KEY = "data";
	String ENTITY_VOLUME_KEY = "volume";

	static boolean isValidEntity(Entity entity) {
		if (entity instanceof Player) return false;
		if (!(entity instanceof LivingEntity living)) return false;

		boolean valid = entity.isAlive() && entity.canChangeDimensions() && entity.getType().canSummon() && entity.getType().canSerialize();

		return valid && (living instanceof FleshBlob || living.isBaby());
	}

	static boolean storeEntity(CompoundTag tag, Entity entity, boolean removeEntity) {

		if (removeEntity && entity.isPassenger()) {
			entity.removeVehicle();
			if (entity.isPassenger()) return false;
		}

		List<Entity> cachedPassengers = null;
		if (entity.isVehicle()) {
			cachedPassengers = entity.getPassengers();
			entity.ejectPassengers();
		}

		if (saveEntity(tag, entity)) {
			if (removeEntity) {
				entity.remove(Entity.RemovalReason.DISCARDED);
			}
			else if (cachedPassengers != null) { //if we don't remove the entity from the world we restore the passengers
				cachedPassengers.forEach(passenger -> passenger.startRiding(entity));
			}
			return true;
		}

		return false;
	}

	static boolean saveEntity(CompoundTag compoundTag, Entity entity) {
		CompoundTag entityData = new CompoundTag();

		if (entity.saveAsPassenger(entityData)) {
			CompoundTag tag = new CompoundTag();
			tag.put(ENTITY_DATA_KEY, entityData);
			tag.putString(ENTITY_NAME_KEY, entity.getType().getDescriptionId());

			EntityDimensions dimensions = entity.getDimensions(entity.getPose());
			float volume = dimensions.width * dimensions.height * dimensions.width;
			tag.putFloat(ENTITY_VOLUME_KEY, volume);

			compoundTag.put(ENTITY_KEY, tag);
			return true;
		}

		return false;
	}

	static boolean spawnEntity(ServerLevel level, Vec3 pos, CompoundTag entityTag) {
		Entity entityToSpawn = EntityType.loadEntityRecursive(entityTag.getCompound(ENTITY_DATA_KEY), level, entity -> {
			entity.moveTo(pos.x, pos.y, pos.z, Mth.wrapDegrees(level.random.nextFloat() * 360), 0);

			if (entity instanceof LivingEntity living) {
				living.yHeadRot = living.getYRot();
				living.yBodyRot = living.getYRot();
			}

			entity.setDeltaMovement(0, 0, 0);
			entity.fallDistance = 0;
			return entity;
		});

		if (entityToSpawn != null) {
			if (!MobUtil.isEntityIdUnique(level, entityToSpawn)) {
				//reset UUID to prevent "Trying to add entity with duplicated UUID" issue
				//this only happens if the item stack was copied (e.g. in creative mode) or if the original mob wasn't removed from the world
				MobUtil.randomizeUUID(entityToSpawn);
				//TODO: trigger secret achievement: Paradox! - There can't be two identical entities in the same world.
			}
			return level.addFreshEntity(entityToSpawn);
		}

		return false;
	}
}
