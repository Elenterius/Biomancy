package com.github.elenterius.biomancy.entity.golem;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface IOwnableCreature {

	Optional<UUID> getOwnerUUID();

	void setOwnerUUID(@Nullable UUID uuid);

	void setOwner(PlayerEntity entity);

	Optional<PlayerEntity> getOwner();

	default boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
		if (target.getUUID().equals(owner.getUUID())) return false;

		if (target instanceof IOwnableCreature) {
			return !((IOwnableCreature) target).isOwner(owner);
		}
		else if (target instanceof TameableEntity) {
			return !owner.getUUID().equals(((TameableEntity) target).getOwnerUUID());
		}
		else return !(target instanceof PlayerEntity) || !(owner instanceof PlayerEntity) || ((PlayerEntity) owner).canHarmPlayer((PlayerEntity) target);
	}

	default boolean isOwner(LivingEntity entity) {
		return getOwnerUUID().map(uuid -> uuid.equals(entity.getUUID())).orElse(false);
	}

	boolean tryToReturnIntoPlayerInventory();
}
