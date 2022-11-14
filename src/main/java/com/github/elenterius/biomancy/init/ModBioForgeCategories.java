package com.github.elenterius.biomancy.init;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModBioForgeCategories {
	public static final Map<String, BioForgeCategory> CATEGORIES = new HashMap<>();
	private static final String PREFIX = "biomancy_bio_forge_";
	public static final BioForgeCategory SEARCH = register("search", 1, () -> Items.COMPASS);
	public static final BioForgeCategory MISC = register("misc", -1, ModItems.LIVING_FLESH);
	public static final BioForgeCategory BLOCKS = register("blocks", ModItems.FLESH_BLOCK);
	public static final BioForgeCategory MACHINES = register("machines", ModItems.DECOMPOSER);
	public static final BioForgeCategory WEAPONS = register("weapons", ModItems.LONG_CLAWS);

	private ModBioForgeCategories() {}

	private static BioForgeCategory register(String name, Supplier<? extends Item> itemSupplier) {
		name = PREFIX + name;
		BioForgeCategory category = new BioForgeCategory(name, itemSupplier);
		CATEGORIES.put(name, category);
		return category;
	}

	private static BioForgeCategory register(String name, int sortPriority, Supplier<? extends Item> itemSupplier) {
		name = PREFIX + name;
		BioForgeCategory category = new BioForgeCategory(name, sortPriority, itemSupplier);
		CATEGORIES.put(name, category);
		return category;
	}

	public record BioForgeCategory(String nameId, int sortPriority, Supplier<? extends Item> iconSupplier) {

		public BioForgeCategory(String name, Supplier<? extends Item> itemSupplier) {
			this(name, 0, itemSupplier);
		}

		public static BioForgeCategory byNameId(String id) {
			return CATEGORIES.getOrDefault(id, MISC);
		}

		public static BioForgeCategory fromJson(JsonObject json) {
			String nameId = GsonHelper.getAsString(json, "category", MISC.nameId);
			return byNameId(nameId);
		}

		public static BioForgeCategory fromNetwork(FriendlyByteBuf buffer) {
			return byNameId(buffer.readUtf());
		}

		public void toJson(JsonObject json) {
			json.addProperty("category", nameId);
		}

		public void toNetwork(FriendlyByteBuf buffer) {
			buffer.writeUtf(nameId);
		}

		public ItemStack getIcon() {
			return new ItemStack(iconSupplier.get());
		}

	}

}
