package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.block.property.MobSoundType;
import com.github.elenterius.biomancy.mixin.accessor.LivingEntityAccessor;
import com.github.elenterius.biomancy.mixin.accessor.MobEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public final class MobSoundUtil {

	private MobSoundUtil() {}

	public static SoundEvent getSoundFallbackFor(MobSoundType soundType) {
		return switch (soundType) {
			case AMBIENT -> SoundEvents.PLAYER_BREATH;
			case HURT -> SoundEvents.GENERIC_HURT;
			case DEATH -> SoundEvents.GENERIC_DEATH;
		};
	}

	public static CompoundTag saveSounds(LivingEntity livingEntity) {
		CompoundTag tag = new CompoundTag();

		LivingEntityAccessor livingEntityAccessor = (LivingEntityAccessor) livingEntity;
		putSound(tag, MobSoundType.DEATH, livingEntityAccessor.biomancy$getDeathSound());
		putSound(tag, MobSoundType.HURT, livingEntityAccessor.biomancy$getHurtSound(livingEntity.level().damageSources().generic()));
		putSound(tag, MobSoundType.AMBIENT, livingEntity instanceof Mob ? ((MobEntityAccessor) livingEntity).biomancy$getAmbientSound() : null);

		return tag;
	}

	public static void putSound(CompoundTag tag, MobSoundType soundType, @Nullable SoundEvent soundEvent) {
		if (soundEvent != null) {
			tag.putString(soundType.getSerializedName(), soundEvent.getLocation().toString());
		}
		else tag.remove(soundType.getSerializedName());
	}

	@Nullable
	public static SoundEvent getSound(CompoundTag tag, MobSoundType soundType) {
		ResourceLocation soundId = ResourceLocation.tryParse(tag.getString(soundType.getSerializedName()));
		if (soundId != null) {
			return ForgeRegistries.SOUND_EVENTS.getValue(soundId);
		}
		return null;
	}

}
