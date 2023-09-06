package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.mixin.LivingEntityAccessor;
import com.github.elenterius.biomancy.mixin.MobEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Locale;

public final class MobSoundUtil {

	private MobSoundUtil() {}

	public static void saveMobSounds(ItemStack stack, LivingEntity target) {
		VoiceType.saveAllSounds(target, stack.getOrCreateTag());
	}

	public static VoiceType getVoiceType(ItemStack stack) {
		return VoiceType.deserialize(stack.getOrCreateTag());
	}

	public static void setVoiceType(ItemStack stack, VoiceType voice) {
		VoiceType.serialize(stack.getOrCreateTag(), voice);
	}

	public enum VoiceType {
		NONE(0, "", ""),
		AMBIENT(1, "AmbientSound", ""),
		HURT(2, "HurtSound", "minecraft:entity.generic.hurt"),
		DEATH(3, "DeathSound", "minecraft:entity.generic.death");

		public static final String NBT_KEY_VOLUME = "Volume";
		public static final String NBT_KEY_PITCH = "Pitch";
		public static final String NBT_KEY_VOICE = "VoiceType";

		public final byte id;
		private final String nbtKey;
		private final String genericSoundId;

		VoiceType(int id, String nbtKey, String genericSoundId) {
			this.nbtKey = nbtKey;
			this.id = (byte) id;
			this.genericSoundId = genericSoundId;
		}

		public static VoiceType fromId(byte id) {
			if (id < 0 || id >= values().length) return NONE;
			return switch (id) {
				case 1 -> AMBIENT;
				case 2 -> HURT;
				case 3 -> DEATH;
				default -> NONE;
			};
		}

		public static void serialize(CompoundTag tag, VoiceType soundType) {
			tag.putByte(NBT_KEY_VOICE, soundType.id);
		}

		public static VoiceType deserialize(CompoundTag tag) {
			return fromId(tag.getByte(NBT_KEY_VOICE));
		}

		private static void saveAllSounds(LivingEntity entity, CompoundTag tag) {
			LivingEntityAccessor livingEntityAccessor = (LivingEntityAccessor) entity;
//			tag.putFloat(NBT_KEY_VOLUME, livingEntityAccessor.biomancy_getSoundVolume());
//			tag.putFloat(NBT_KEY_PITCH, livingEntityAccessor.biomancy_getVoicePitch());
			VoiceType.DEATH.saveSound(tag, livingEntityAccessor.biomancy$getDeathSound());
			VoiceType.HURT.saveSound(tag, livingEntityAccessor.biomancy$getHurtSound(DamageSource.GENERIC));
			VoiceType.AMBIENT.saveSound(tag, entity instanceof Mob ? ((MobEntityAccessor) entity).biomancy$getAmbientSound() : null);
			tag.putByte(NBT_KEY_VOICE, DEATH.id);
		}

		private static float getPitch(CompoundTag tag) {
			if (tag.contains(NBT_KEY_PITCH)) {
				return tag.getFloat(NBT_KEY_PITCH);
			}
			return 1f;
		}

		private static float getVolume(CompoundTag tag) {
			if (tag.contains(NBT_KEY_VOLUME)) {
				return tag.getFloat(NBT_KEY_VOLUME);
			}
			return 1f;
		}

		public String getTranslationKey() {
			return "enum.biomancy.voice_type." + name().toLowerCase(Locale.ENGLISH);
		}

		public VoiceType cycle() {
			return fromId((byte) (id + 1));
		}

		private void saveSound(CompoundTag tag, @Nullable SoundEvent soundEvent) {
			if (soundEvent != null) {
				ResourceLocation registryName = soundEvent.getLocation();
				tag.putString(nbtKey, registryName != null ? registryName.toString() : genericSoundId);
			}
			else tag.remove(nbtKey);
		}

		@Nullable
		public SoundEvent getSound(ItemStack stack) {
			return getSound(stack.getOrCreateTag());
		}

		@Nullable
		public SoundEvent getSound(CompoundTag tag) {
			if (this == NONE) return SoundEvents.PLAYER_BREATH;

			ResourceLocation soundId = ResourceLocation.tryParse(tag.getString(nbtKey));
			if (soundId != null) {
				return ForgeRegistries.SOUND_EVENTS.getValue(soundId);
			}
			return null;
		}

	}

}
