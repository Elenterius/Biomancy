package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.util.UserAuthorization;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public interface IOwnableTile {

	void setOwner(UUID uuid);

	Optional<UUID> getOwner();

	void removeOwner();

	default boolean hasOwner() {
		return getOwner().isPresent();
	}

	default boolean isOwner(UUID uuid) {
		return getOwner().map(value -> value.equals(uuid)).orElse(false);
	}

	default boolean isLocked() {
		return hasOwner();
	}

	HashMap<UUID, UserAuthorization.AuthorityLevel> getUserAuthorityLevelMap();

	default UserAuthorization.AuthorityLevel getUserAuthorityLevel(UUID userUUID) {
		if (isOwner(userUUID)) return UserAuthorization.AuthorityLevel.OWNER;
		return getUserAuthorityLevelMap().getOrDefault(userUUID, UserAuthorization.AuthorityLevel.NONE);
	}

	default void addUser(UUID userUUID) {
		addUser(userUUID, UserAuthorization.AuthorityLevel.USER);
	}

	default void addUser(UUID userUUID, UserAuthorization.AuthorityLevel authority) {
		if (isOwner(userUUID)) return;
		getUserAuthorityLevelMap().put(userUUID, authority);
	}

	default void removeUser(UUID userUUID) {
		getUserAuthorityLevelMap().remove(userUUID);
	}

	default boolean isUserAuthorized(UUID userUUID) {
		if (isLocked() && !isOwner(userUUID)) {
			UserAuthorization.AuthorityLevel authorityLevel = getUserAuthorityLevelMap().getOrDefault(userUUID, UserAuthorization.AuthorityLevel.NONE);
			return authorityLevel.isUserLevel();
		}
		return true;
	}

	default boolean isUserAuthorized(PlayerEntity player) {
		if (player.isCreative()) return true;
		return isUserAuthorized(player.getUUID());
	}

	default boolean canPlayerUse(PlayerEntity player) {
		if (!player.isSpectator() && !isUserAuthorized(player)) {
			if (!player.level.isClientSide()) {
				player.displayClientMessage(new TranslationTextComponent("container.isLocked", getDefaultName()).withStyle(TextFormatting.RED), true);
				player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 1f, 1f);
			}
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * gets the default name of the tile entity / container
	 */
	ITextComponent getDefaultName();

}