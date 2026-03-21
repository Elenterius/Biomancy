package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.sounds.ClientSoundHandler;
import com.github.elenterius.biomancy.util.ClientUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class EntitySoundLayerMessages {

	private EntitySoundLayerMessages() {}

	/// client bound message
	public static class UpdateMessage {

		private final int entityId;
		private final String soundLayerId;
		private final Holder<SoundEvent> sound;
		private final long startTime;
		private final float volume;

		public UpdateMessage(Entity entity, String soundLayerId, SoundEvent soundEvent, long startTime, float volume) {
			this(entity.getId(), soundLayerId, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent), startTime, volume);
		}

		protected UpdateMessage(int entityId, String soundLayerId, Holder<SoundEvent> sound, long startTime, float volume) {
			this.entityId = entityId;
			this.soundLayerId = soundLayerId;
			this.sound = sound;
			this.startTime = startTime;
			this.volume = volume;
		}

		public void encode(final FriendlyByteBuf buffer) {
			buffer.writeVarInt(entityId);
			buffer.writeUtf(soundLayerId);
			buffer.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), sound, (b, s) -> s.writeToNetwork(b));
			buffer.writeVarLong(startTime);
			buffer.writeFloat(volume);
		}

		public static UpdateMessage decode(final FriendlyByteBuf buffer) {
			int entityId = buffer.readVarInt();
			String soundLayer = buffer.readUtf();
			Holder<SoundEvent> sound = buffer.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
			long startTime = buffer.readVarLong();
			float volume = buffer.readFloat();

			return new UpdateMessage(entityId, soundLayer, sound, startTime, volume);
		}

		public static void handle(UpdateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();

			if (context.getDirection().getReceptionSide().isClient()) {
				context.enqueueWork(() -> {
					Entity entity = ClientUtil.getEntity(message.entityId);
					if (entity != null) {
						ClientSoundHandler.updateSoundLayer(entity, message.soundLayerId, message.sound.get(), message.startTime, message.volume);
					}
				});
			}

			context.setPacketHandled(true);
		}

	}

	/// client bound message
	public static class RemovalMessage {

		private final int entityId;
		private final String soundLayerId;
		private final boolean fadeOut;

		public RemovalMessage(Entity entity, String soundLayerId, boolean fadeOut) {
			this(entity.getId(), soundLayerId, fadeOut);
		}

		protected RemovalMessage(int entityId, String soundLayerId, boolean fadeOut) {
			this.entityId = entityId;
			this.soundLayerId = soundLayerId;
			this.fadeOut = fadeOut;
		}

		public void encode(final FriendlyByteBuf buffer) {
			buffer.writeVarInt(entityId);
			buffer.writeUtf(soundLayerId);
			buffer.writeBoolean(fadeOut);
		}

		public static RemovalMessage decode(final FriendlyByteBuf buffer) {
			int entityId = buffer.readVarInt();
			String soundLayer = buffer.readUtf();
			boolean fadeOut = buffer.readBoolean();
			return new RemovalMessage(entityId, soundLayer, fadeOut);
		}

		public static void handle(RemovalMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();

			if (context.getDirection().getReceptionSide().isClient()) {
				context.enqueueWork(() -> {
					Entity entity = ClientUtil.getEntity(message.entityId);
					if (entity != null) {
						ClientSoundHandler.removeSoundLayer(entity, message.soundLayerId, message.fadeOut);
					}
				});
			}

			context.setPacketHandled(true);
		}

	}

}
