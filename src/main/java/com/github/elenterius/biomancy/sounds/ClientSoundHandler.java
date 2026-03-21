package com.github.elenterius.biomancy.sounds;

import com.github.elenterius.biomancy.BiomancyMod;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, value = Dist.CLIENT)
public final class ClientSoundHandler {

	private static final Int2ObjectMap<EntitySoundLayers> ENTITY_SOUND_LAYERS = new Int2ObjectOpenHashMap<>();
	private static final Queue<SoundLayerUpdate> QUEUE = new ArrayDeque<>();

	private ClientSoundHandler() {}

	private static SoundManager getSoundManager() {
		return Minecraft.getInstance().getSoundManager();
	}

	private static void play(SoundInstance sound) {
		getSoundManager().play(sound);
	}

	public static @Nullable WeighedSoundEvents getWeightedSoundEvents(ResourceLocation id) {
		return getSoundManager().getSoundEvent(id);
	}

	public static void updateSoundLayer(Entity entity, String soundLayerId, SoundEvent soundEvent, long startTime, float volume) {
		ENTITY_SOUND_LAYERS.computeIfAbsent(entity.getId(), EntitySoundLayers::new);
		QUEUE.add(new SoundLayerUpdate(entity, soundLayerId, soundEvent, startTime, volume));
	}

	public static void removeSoundLayer(Entity entity, String soundLayerId, boolean fadeOut) {
		if (!ENTITY_SOUND_LAYERS.containsKey(entity.getId())) return;

		LoopingEntitySoundInstance sound = ENTITY_SOUND_LAYERS.get(entity.getId()).remove(soundLayerId);
		if (sound != null) {
			if (fadeOut) sound.fadeOut();
			else sound.stopNow();
		}
	}

	public static void removeSoundLayers(Entity entity, boolean fadeOut) {
		if (!ENTITY_SOUND_LAYERS.containsKey(entity.getId())) return;

		EntitySoundLayers soundLayers = ENTITY_SOUND_LAYERS.remove(entity.getId());
		soundLayers.removeAll(fadeOut);
	}

	public static void removeAllSoundLayers(boolean fadeOut) {
		for (EntitySoundLayers soundLayers : ENTITY_SOUND_LAYERS.values()) {
			soundLayers.removeAll(fadeOut);
		}

		ENTITY_SOUND_LAYERS.clear();
	}

	@SubscribeEvent
	public static void onClientTick(final TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;

		while (!QUEUE.isEmpty()) {
			SoundLayerUpdate update = QUEUE.poll();
			int entityId = update.entity.getId();

			if (ENTITY_SOUND_LAYERS.containsKey(entityId)) {
				EntitySoundLayers soundLayers = ENTITY_SOUND_LAYERS.get(entityId);
				soundLayers.update(update);
			}
		}
	}

	@SubscribeEvent
	public void onEntityLeaveLevel(final EntityLeaveLevelEvent event) {
		if (!event.getLevel().isClientSide) return;
		removeSoundLayers(event.getEntity(), true);
	}

	@SubscribeEvent
	public void onLevelUnload(final LevelEvent.Unload event) {
		if (!event.getLevel().isClientSide()) return;
		removeAllSoundLayers(true);
	}

	public static class EntitySoundLayers {

		public final int entityId;
		private final Map<String, LoopingEntitySoundInstance> layers = new Object2ObjectOpenHashMap<>();

		public EntitySoundLayers(int entityId) {
			this.entityId = entityId;
		}

		public void update(SoundLayerUpdate update) {
			if (update.entity.getId() != entityId) return;

			LoopingEntitySoundInstance currentSound = layers.get(update.soundLayerId);

			long age = update.entity.level().getGameTime() - update.startTime;
			boolean doFadeIn = age < 20;

			if (currentSound == null) {
				LoopingEntitySoundInstance sound = new LoopingEntitySoundInstance(update.entity, update.soundEvent);

				sound.setVolume(update.volume, doFadeIn);
				layers.put(update.soundLayerId, sound);

				play(sound);
				return;
			}

			if (!update.soundEvent.getLocation().equals(currentSound.getLocation())) {
				currentSound.fadeOut();
				layers.remove(update.soundLayerId);

				LoopingEntitySoundInstance sound = new LoopingEntitySoundInstance(update.entity, update.soundEvent);

				sound.setVolume(update.volume, doFadeIn);
				layers.put(update.soundLayerId, sound);

				play(sound);
				return;
			}

			currentSound.setVolume(update.volume, doFadeIn);
		}

		public @Nullable LoopingEntitySoundInstance remove(String soundLayerId) {
			return layers.remove(soundLayerId);
		}

		private void fadeOutAll() {
			for (LoopingEntitySoundInstance sound : layers.values()) {
				sound.fadeOut();
			}
			layers.clear();
		}

		private void stopAll() {
			for (LoopingEntitySoundInstance sound : layers.values()) {
				sound.stopNow();
			}
			layers.clear();
		}

		public void removeAll(boolean fadeOut) {
			if (fadeOut) fadeOutAll();
			else stopAll();
		}

	}

	public record SoundLayerUpdate(Entity entity, String soundLayerId, SoundEvent soundEvent, long startTime, float volume) {}

}
