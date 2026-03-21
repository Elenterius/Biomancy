package com.github.elenterius.biomancy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class ClientUtil {

	private ClientUtil() {}

	public static @Nullable Level getLevel() {
		return Minecraft.getInstance().level;
	}

	public static @Nullable Player getPlayer() {
		return Minecraft.getInstance().player;
	}

	public static @Nullable Entity getEntity(@Nullable Integer entityId) {
		if (entityId == null) return null;
		return getEntity(entityId.intValue());
	}

	public static @Nullable Entity getEntity(int entityId) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return null;
		return level.getEntity(entityId);
	}

	public static Optional<Entity> getOptionalEntity(int entityId) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return Optional.empty();
		return Optional.ofNullable(level.getEntity(entityId));
	}

}
