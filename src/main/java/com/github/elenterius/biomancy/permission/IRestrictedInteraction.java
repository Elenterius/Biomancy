package com.github.elenterius.biomancy.permission;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface IRestrictedInteraction {

	default boolean isActionAllowed(UUID userId, IAction action) {
		return action.allowed(getUserType(userId));
	}

	default boolean isActionAllowed(Entity entity, IAction action) {
		if (entity instanceof Player player) return isActionAllowed(player, action);
		return isActionAllowed(entity.getUUID(), action);
	}

	default boolean isActionAllowed(Player player, IAction action) {
		if (player.isSpectator()) return false;
		if (player.isCreative()) return true;
		return action.allowed(getUserType(player.getUUID()));
	}

	UserType getUserType(UUID userId);

	boolean setUserType(UUID userId, UserType userType);

	default void addUser(UUID userId) {
		addUser(userId, UserType.DEFAULT);
	}

	default void addUser(UUID userId, UserType userType) {
		setUserType(userId, userType);
	}

	void removeUser(UUID userId);

}
