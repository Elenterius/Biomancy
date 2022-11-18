package com.github.elenterius.biomancy.world.inventory.menu;

import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;

public final class BioForgeTab extends ForgeRegistryEntry<BioForgeTab> {
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
		buffer.writeResourceLocation(getRegistryName());
	}

	public void toJson(JsonObject json) {
		json.addProperty(JSON_KEY, getRegistryName().toString());
	}

	public ItemStack getIcon() {
		return new ItemStack(iconItem);
	}

	public String enumId() {
		return getRegistryName().toString().replace(":", "_");
	}

	public int sortPriority() {
		return sortPriority;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (BioForgeTab) obj;
		return getRegistryName().equals(that.getRegistryName()) && this.sortPriority == that.sortPriority && Objects.equals(this.iconItem, that.iconItem);
	}

	@Override
	public int hashCode() {
		return getRegistryName().hashCode();
	}

	@Override
	public String toString() {
		return "BioForgeTab[" + "id=" + getRegistryName() + ", " + "sortPriority=" + sortPriority + ", " + "iconSupplier=" + iconItem + ']';
	}

}
