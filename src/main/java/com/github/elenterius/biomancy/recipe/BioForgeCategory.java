package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public record BioForgeCategory(ResourceLocation id, ItemStack icon) {

	public static final Map<ResourceLocation, BioForgeCategory> CATEGORIES = new HashMap<>();

	public static final BioForgeCategory MISC = new BioForgeCategory("misc", ModItems.OCULUS.get());
	public static final BioForgeCategory BLOCKS = new BioForgeCategory("blocks", ModItems.FLESH_BLOCK.get());
	public static final BioForgeCategory MACHINES = new BioForgeCategory("machines", ModItems.DECOMPOSER.get());
	public static final BioForgeCategory WEAPON = new BioForgeCategory("weapon", ModItems.LONG_CLAW.get());

	public BioForgeCategory(ResourceLocation id, ItemStack icon) {
		this.id = id;
		this.icon = icon;
		CATEGORIES.put(id, this);
	}

	public BioForgeCategory(String id, Item icon) {
		this(BiomancyMod.createRL(id), new ItemStack(icon));
	}

	public static Collection<BioForgeCategory> getCategories() {
		return CATEGORIES.values();
	}

	public static BioForgeCategory byId(@Nullable ResourceLocation id) {
		if (id == null) return MISC;
		return CATEGORIES.getOrDefault(id, MISC);
	}

	public static BioForgeCategory fromJson(JsonObject json) {
		String nameId = GsonHelper.getAsString(json, "category", "biomancy:misc");
		return byId(ResourceLocation.tryParse(nameId));
	}

	public void toJson(JsonObject json) {
		json.addProperty("category", id.toString());
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(id);
	}

	public static BioForgeCategory fromNetwork(FriendlyByteBuf buffer) {
		return byId(buffer.readResourceLocation());
	}

}
