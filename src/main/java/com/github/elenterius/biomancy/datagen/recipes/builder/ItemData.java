package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public final class ItemData {

	private final ResourceLocation registryName;
	private final int count;
	private final @Nullable CompoundTag tag;

	public ItemData(ItemStack stack) {
		this(stack, stack.getCount());
	}

	public ItemData(ItemLike item) {
		this(item, 1);
	}

	public ItemData(ItemStack stack, int count) {
		this(stack.getItem(), stack.getTag(), count);
	}

	public ItemData(ItemLike item, int count) {
		this(item, null, count);
	}

	public ItemData(ItemLike item, @Nullable CompoundTag tag, int count) {
		this.registryName = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.asItem()));
		this.tag = tag;
		this.count = count;
	}

	public static ItemData from(String namespace, String path) {
		return new ItemData(new ResourceLocation(namespace, path));
	}

	public ItemData(ResourceLocation registryName) {
		this(registryName, null, 1);
	}

	public ItemData(ResourceLocation registryName, @Nullable CompoundTag tag, int count) {
		this.registryName = registryName;
		this.tag = tag;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public ResourceLocation getRegistryName() {
		return registryName;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("item", registryName.toString());
		if (count > 1) json.addProperty("count", count);
		if (tag != null && !tag.isEmpty()) json.addProperty("nbt", tag.getAsString());
		return json;
	}

	public String getItemPath() {
		return registryName.getPath();
	}

}
