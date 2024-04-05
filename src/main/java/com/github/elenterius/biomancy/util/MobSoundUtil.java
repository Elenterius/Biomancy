package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.block.property.MobSoundType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Locale;

public final class MobSoundUtil {

	private MobSoundUtil() {}

	public static @Nullable SoundEvent findSoundFor(EntityType<?> entityType, MobSoundType soundType) {
		ResourceLocation key = EntityType.getKey(entityType);

		String soundTypeSuffix = soundType.name().toLowerCase(Locale.ENGLISH);
		ResourceLocation soundKey = ResourceLocation.tryBuild(key.getNamespace(), "entity." + key.getPath() + "." + soundTypeSuffix);

		return soundKey != null ? ForgeRegistries.SOUND_EVENTS.getValue(soundKey) : null;
	}

	public static SoundEvent getAmbientSoundFallback() {
		return getSoundFallbackFor(MobSoundType.AMBIENT);
	}

	public static SoundEvent getSoundFallbackFor(MobSoundType soundType) {
		return switch (soundType) {
			case AMBIENT -> SoundEvents.PLAYER_BREATH;
			case HURT -> SoundEvents.GENERIC_HURT;
			case DEATH -> SoundEvents.GENERIC_DEATH;
		};
	}

}
