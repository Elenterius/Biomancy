package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.Objects;

public final class BioForgeTab {
	private static final String JSON_KEY = "bio_forge_tab";
	private final int sortPriority;
	private final Item iconItem;

	public BioForgeTab(int sortPriority, Item iconItem) {
		this.sortPriority = sortPriority;
		this.iconItem = iconItem;
	}

	public BioForgeTab(Item itemSupplier) {
		this(0, itemSupplier);
	}

	public static BioForgeTab fromJson(JsonObject json) {
		String categoryId = GsonHelper.getAsString(json, JSON_KEY);
		BioForgeTab category = ModBioForgeTabs.REGISTRY.get().getValue(new ResourceLocation(categoryId));
		if (category == null) {
			throw new JsonSyntaxException("Unknown tab '%s'".formatted(categoryId));
		}
		return category;
	}

	public static BioForgeTab fromNetwork(FriendlyByteBuf buffer) {
		BioForgeTab value = ModBioForgeTabs.REGISTRY.get().getValue(buffer.readResourceLocation());
		return value != null ? value : ModBioForgeTabs.MISC.get();
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(ModBioForgeTabs.REGISTRY.get().getKey(this));
	}

	public void toJson(JsonObject json) {
		json.addProperty(JSON_KEY, ModBioForgeTabs.REGISTRY.get().getKey(this).toString());
	}

	public ItemStack getIcon() {
		return new ItemStack(iconItem);
	}

	public String enumId() {
		return ModBioForgeTabs.REGISTRY.get().getKey(this).toString().replace(":", "_");
	}

	public int sortPriority() {
		return sortPriority;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (BioForgeTab) obj;
		return ModBioForgeTabs.REGISTRY.get().getKey(this).equals(ModBioForgeTabs.REGISTRY.get().getKey(that)) && this.sortPriority == that.sortPriority && Objects.equals(this.iconItem, that.iconItem);
	}

	@Override
	public int hashCode() {
		return ModBioForgeTabs.REGISTRY.get().getKey(this).hashCode();
	}

	@Override
	public String toString() {
		return "BioForgeTab[" + "id=" + ModBioForgeTabs.REGISTRY.get().getKey(this) + ", " + "sortPriority=" + sortPriority + ", " + "iconSupplier=" + iconItem + ']';
	}

}
