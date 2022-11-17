package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModBioForgeCategories;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;
import java.util.function.Supplier;

public final class BioForgeCategory extends ForgeRegistryEntry<BioForgeCategory> {
	private final int sortPriority;
	private final Supplier<? extends Item> iconSupplier;

	public BioForgeCategory(int sortPriority, Supplier<? extends Item> iconSupplier) {
		this.sortPriority = sortPriority;
		this.iconSupplier = iconSupplier;
	}

	public BioForgeCategory(Supplier<? extends Item> itemSupplier) {
		this(0, itemSupplier);
	}

	public static BioForgeCategory fromJson(JsonObject json) {
		String categoryId = GsonHelper.getAsString(json, "category");
		BioForgeCategory category = ModBioForgeCategories.REGISTRY.get().getValue(new ResourceLocation(categoryId));
		if (category == null) {
			throw new JsonSyntaxException("Unknown category '%s'".formatted(categoryId));
		}
		return category;
	}

	public static BioForgeCategory fromNetwork(FriendlyByteBuf buffer) {
		BioForgeCategory value = ModBioForgeCategories.REGISTRY.get().getValue(buffer.readResourceLocation());
		return value != null ? value : ModBioForgeCategories.MISC.get();
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(getRegistryName());
	}

	public void toJson(JsonObject json) {
		json.addProperty("category", getRegistryName().toString());
	}

	public ItemStack getIcon() {
		return new ItemStack(iconSupplier.get());
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
		var that = (BioForgeCategory) obj;
		return getRegistryName().equals(that.getRegistryName()) && this.sortPriority == that.sortPriority && Objects.equals(this.iconSupplier, that.iconSupplier);
	}

	@Override
	public int hashCode() {
		return getRegistryName().hashCode();
	}

	@Override
	public String toString() {
		return "BioForgeCategory[" + "id=" + getRegistryName() + ", " + "sortPriority=" + sortPriority + ", " + "iconSupplier=" + iconSupplier + ']';
	}

}
