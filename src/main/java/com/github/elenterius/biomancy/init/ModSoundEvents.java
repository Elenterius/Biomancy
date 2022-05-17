package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSoundEvents {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<SoundEvent> INJECT = registerSoundEvent("inject");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_PLACE = registerSoundEvent("flesh_block.place");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_HIT = registerSoundEvent("flesh_block.hit");
	public static final RegistryObject<SoundEvent> FLESH_BLOCK_STEP = registerSoundEvent("flesh_block.step");

	public static final SoundEvent FAIL = SoundEvents.DISPENSER_FAIL;

	private ModSoundEvents() {}

	private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(BiomancyMod.createRL(name)));
	}

	public static void playItemSFX(Level world, LivingEntity itemHolder, SoundEvent soundEvent) {
		SoundSource soundSource = itemHolder instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		float pitch = 1f / (world.random.nextFloat() * 0.5f + 1f) + 0.2f;
		world.playSound(world.isClientSide && itemHolder instanceof Player player ? player : null, itemHolder.getX(), itemHolder.getY(), itemHolder.getZ(), soundEvent, soundSource, 0.8f, pitch);
	}

}
