package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.menu.BioLabMenu;
import com.github.elenterius.biomancy.util.ItemStackFilter;
import com.github.elenterius.biomancy.util.ItemStackFilterList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//client bound message
public class BioLabFilterMessage {

	public final int containerId;
	private final List<ItemStack> filters;

	public BioLabFilterMessage(int containerId, ItemStackFilterList filters) {
		this.containerId = containerId;
		this.filters = filters.stream().map(ItemStackFilter::getItemStack).collect(Collectors.toCollection(ArrayList::new));
	}

	private BioLabFilterMessage(int containerId, List<ItemStack> filters) {
		this.containerId = containerId;
		this.filters = filters;
	}

	public static BioLabFilterMessage decode(final FriendlyByteBuf buffer) {
		int containerId = buffer.readVarInt();
		List<ItemStack> filterItems = buffer.readCollection(ArrayList::new, BioLabFilterMessage::decodeNullableItemStack);
		return new BioLabFilterMessage(containerId, filterItems);
	}

	private static void encodeNullableItemStack(final FriendlyByteBuf buffer, @Nullable ItemStack stack) {
		buffer.writeBoolean(stack != null);
		if (stack != null) buffer.writeItem(stack);
	}

	private static @Nullable ItemStack decodeNullableItemStack(final FriendlyByteBuf buffer) {
		return !buffer.readBoolean() ? null : buffer.readItem();
	}

	public static void handle(BioLabFilterMessage packet, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();

		if (context.getDirection().getReceptionSide().isClient()) {
			context.enqueueWork(() -> {
				LocalPlayer player = Minecraft.getInstance().player;
				if (player != null && player.containerMenu instanceof BioLabMenu menu && menu.containerId == packet.containerId) {
					menu.setFilters(packet.filters);
				}
			});
		}

		context.setPacketHandled(true);
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeVarInt(containerId);
		buffer.writeCollection(filters, BioLabFilterMessage::encodeNullableItemStack);
	}

}
