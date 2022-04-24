package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.mixin.LivingEntityAccessor;
import com.github.elenterius.biomancy.mixin.MobEntityAccessor;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class LarynxItem extends Item implements IBiomancyItem {

	public static final String NBT_KEY_ENTITY_NAME = "EntityName";
	public static final String NBT_KEY_ENTITY_ID = "EntityId";

	public LarynxItem(Properties properties) {
		super(properties);
	}

	public static void saveSounds(ItemStack stack, LivingEntity target) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.putString(NBT_KEY_ENTITY_NAME, target.getType().getDescriptionId());
		ResourceLocation registryName = target.getType().getRegistryName();
		if (registryName != null) {
			tag.putString(NBT_KEY_ENTITY_ID, registryName.toString());
		}
		VoiceType.saveAllSounds(target, tag);
	}

	private void playVoice(ItemStack stack, Level level, double x, double y, double z) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!playVoice(stack, level, x, y, z, VoiceType.getVolume(tag), VoiceType.getPitch(tag))) {
			level.playSound(null, x, y, z, SoundEvents.PLAYER_BREATH, SoundSource.RECORDS, 2f, 1f);
		}
	}

	public boolean playVoice(ItemStack stack, Level level, double x, double y, double z, float volume, float pitch) {
		CompoundTag tag = stack.getOrCreateTag();
		VoiceType voice = VoiceType.deserialize(tag);
		SoundEvent soundEvent = voice.getSound(tag);
		if (soundEvent != null) {
			level.playSound(null, x, y, z, soundEvent, SoundSource.RECORDS, volume, pitch);
			return true;
		}
		return false;
	}

	public boolean playVoice(ItemStack stack, Level level, double x, double y, double z, float volume, float pitch, VoiceType voicetype) {
		CompoundTag tag = stack.getOrCreateTag();
		SoundEvent soundEvent = voicetype.getSound(tag);
		if (soundEvent != null) {
			level.playSound(null, x, y, z, soundEvent, SoundSource.RECORDS, volume, pitch);
			return true;
		}
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 16;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	public VoiceType getVoiceType(ItemStack stack) {
		return VoiceType.deserialize(stack.getOrCreateTag());
	}

	public void setVoiceType(ItemStack stack, VoiceType voice) {
		VoiceType.serialize(stack.getOrCreateTag(), voice);
	}

	@Override
	public Component getName(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(NBT_KEY_ENTITY_NAME)) {
			TranslatableComponent mobName = new TranslatableComponent(tag.getString(NBT_KEY_ENTITY_NAME));
			return new TranslatableComponent(getDescriptionId(stack) + ".mob", mobName);
		}
		return new TranslatableComponent(getDescriptionId(stack));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
		CompoundTag tag = stack.getOrCreateTag();
//		tooltip.add(new TextComponent("Pitch: " + VoiceType.getPitch(tag)));
//		tooltip.add(new TextComponent("Volume: " + VoiceType.getVolume(tag)));
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
			VoiceType.DEATH.saveSound(tag, livingEntityAccessor.biomancy_getDeathSound());
			VoiceType.HURT.saveSound(tag, livingEntityAccessor.biomancy_getHurtSound(DamageSource.GENERIC));
			VoiceType.AMBIENT.saveSound(tag, entity instanceof Mob ? ((MobEntityAccessor) entity).biomancy_getAmbientSound() : null);
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
			return "enum.biomancy.voice_type." + name().toLowerCase(Locale.ROOT);
		}

		public VoiceType cycle() {
			return fromId((byte) (id + 1));
		}

		private void saveSound(CompoundTag tag, @Nullable SoundEvent soundEvent) {
			if (soundEvent != null) {
				ResourceLocation registryName = soundEvent.getRegistryName();
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
