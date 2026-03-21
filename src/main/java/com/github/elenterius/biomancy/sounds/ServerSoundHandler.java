package com.github.elenterius.biomancy.sounds;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jspecify.annotations.Nullable;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, value = Dist.DEDICATED_SERVER)
public class ServerSoundHandler {

	public static final String SOUND_LAYERS_KEY = "biomancy:sound_layers";

	private static @Nullable SoundEvent parseSoundEvent(String soundId) {
		ResourceLocation key = ResourceLocation.tryParse(soundId);
		if (key == null) return null;
		return ForgeRegistries.SOUND_EVENTS.getValue(key);
	}

	@SubscribeEvent
	public void onStartTrackingEntity(final PlayerEvent.StartTracking event) {
		CompoundTag soundLayers = event.getTarget().getPersistentData().getCompound(SOUND_LAYERS_KEY);

		for (String soundLayerId : soundLayers.getAllKeys()) {
			CompoundTag soundLayer = soundLayers.getCompound(soundLayerId);
			SoundEvent soundEvent = parseSoundEvent(soundLayer.getString("sound_id"));

			if (soundEvent == null) continue;

			float volume = soundLayer.getFloat("volume");
			long startTime = soundLayer.getLong("start_timestamp");
			ModNetworkHandler.sendSoundLayerUpdateToClients(event.getTarget(), soundLayerId, soundEvent, startTime, volume);
		}
	}

	public static void triggerSoundLayerForClients(Entity entity, String soundLayerId, SoundEvent soundEvent, long startTime, float volume) {
		CompoundTag soundLayers = entity.getPersistentData().getCompound(SOUND_LAYERS_KEY);

		CompoundTag soundLayer = new CompoundTag();
		soundLayer.putString("sound_id", soundEvent.getLocation().toString());
		soundLayer.putFloat("volume", volume);
		soundLayer.putLong("start_timestamp", startTime);

		soundLayers.put(soundLayerId, soundLayer);
		entity.getPersistentData().put(SOUND_LAYERS_KEY, soundLayers);

		ModNetworkHandler.sendSoundLayerUpdateToClients(entity, soundLayerId, soundEvent, startTime, volume);
	}

	public static void removeSoundLayerForClients(Entity entity, String soundLayerId) {
		CompoundTag soundLayers = entity.getPersistentData().getCompound(SOUND_LAYERS_KEY);
		soundLayers.remove(soundLayerId);

		ModNetworkHandler.sendSoundLayerRemovalToClients(entity, soundLayerId, true);
	}

	/// triggers sound event on the client side at the eye positon of the player
	public static void triggerSoundForClientPlayer(ServerPlayer player, SoundEvent soundEvent, float volume, float pitch) {
		triggerSoundForClientPlayer(player, soundEvent, player.getSoundSource(), volume, pitch);
	}

	/// triggers sound event on the client side at the eye positon of the player
	public static void triggerSoundForClientPlayer(ServerPlayer player, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
		player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent), source, player.getX(), player.getEyeY(), player.getZ(), volume, pitch, player.getRandom().nextLong()));
	}

	/// triggers sound event on the client side at the specified position
	public static void triggerSoundOnClient(ServerPlayer player, Vec3 soundPosition, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
		triggerSoundOnClient(player, soundPosition.x, soundPosition.y, soundPosition.z, soundEvent, source, volume, pitch);
	}

	/// triggers sound event on the client side at the specified position
	public static void triggerSoundOnClient(ServerPlayer player, double x, double y, double z, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
		player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent), source, x, y, z, volume, pitch, player.getRandom().nextLong()));
	}

}
