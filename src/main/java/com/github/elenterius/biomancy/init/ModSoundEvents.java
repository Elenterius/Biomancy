package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<SoundEvent> INJECT = registerSoundEvent("inject");
	public static final RegistryObject<SoundEvent> ACTION_FAIL = registerSoundEvent("action.fail");

	//# Blocks
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_PLACE = registerSoundEvent("flesh_block.place");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_HIT = registerSoundEvent("flesh_block.hit");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_BREAK = registerSoundEvent("flesh_block.break");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_STEP = registerSoundEvent("flesh_block.step");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_FALL = registerSoundEvent("flesh_block.fall");
	public static final RegistryObject<SoundEvent> FLESHY_DOOR_OPEN = registerSoundEvent("fleshy_door.open");
	public static final RegistryObject<SoundEvent> FLESHY_DOOR_CLOSE = registerSoundEvent("fleshy_door.close");
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_OPEN = registerSoundEvent("fleshkin_chest.open");
	public static final RegistryObject<SoundEvent> FLESHKIN_CHEST_CLOSE = registerSoundEvent("fleshkin_chest.close");

	private ModSoundEvents() {}

	private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(BiomancyMod.createRL(name)));
	}

	public static void localItemSFX(ClientLevel level, LivingEntity itemHolder, SoundEvent soundEvent) {
		SoundSource soundSource = itemHolder instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		float pitch = 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f;
		level.playSound(itemHolder instanceof Player player ? player : null, itemHolder.getX(), itemHolder.getY(0.5f), itemHolder.getZ(), soundEvent, soundSource, 0.8f, pitch);
	}

	public static void broadcastItemSFX(ServerLevel level, LivingEntity itemHolder, SoundEvent soundEvent) {
		SoundSource soundSource = itemHolder instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		float pitch = 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f;
		level.playSound(null, itemHolder.getX(), itemHolder.getY(0.5f), itemHolder.getZ(), soundEvent, soundSource, 0.8f, pitch);
	}

}
