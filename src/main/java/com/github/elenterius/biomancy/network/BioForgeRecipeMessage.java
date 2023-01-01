package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.recipe.BioForgeRecipe;
import com.github.elenterius.biomancy.world.inventory.menu.BioForgeMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

//server bound message
public class BioForgeRecipeMessage {

	public final ResourceLocation id;
	public final int containerId;

	public BioForgeRecipeMessage(int containerId, ResourceLocation recipeId) {
		this.id = recipeId;
		this.containerId = containerId;
	}

	public static BioForgeRecipeMessage decode(final FriendlyByteBuf buffer) {
		int containerId = buffer.readVarInt();
		ResourceLocation recipeId = buffer.readResourceLocation();

		return new BioForgeRecipeMessage(containerId, recipeId);
	}

	public void encode(final FriendlyByteBuf buffer) {
		buffer.writeVarInt(containerId);
		buffer.writeResourceLocation(id);
	}

	public static void handle(BioForgeRecipeMessage packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender != null && !sender.isSpectator() && sender.containerMenu instanceof BioForgeMenu menu && menu.containerId == packet.containerId) {
				RecipeManager recipeManager = sender.getLevel().getRecipeManager();
				Map<ResourceLocation, BioForgeRecipe> recipes = recipeManager.byType(ModRecipes.BIO_FORGING_RECIPE_TYPE.get());
				BioForgeRecipe recipe = (BioForgeRecipe) recipes.get(packet.id);
				menu.setSelectedRecipe(recipe, sender);
			}
		});
		ctx.get().setPacketHandled(true);
	}

}
