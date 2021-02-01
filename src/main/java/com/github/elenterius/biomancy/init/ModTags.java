package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public final class ModTags {
	private ModTags() {}

	public static void init() {
		//force initialization of static fields
		Items.forceInit();
		EntityTypes.forceInit();
	}

	public static final class Items {
		public static final Tags.IOptionalNamedTag<Item> RAW_MEATS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "raw_meats"));
		public static final Tags.IOptionalNamedTag<Item> FLESH = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "flesh"));
		public static final Tags.IOptionalNamedTag<Item> COOKED_MEATS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "cooked_meats"));
		public static final Tags.IOptionalNamedTag<Item> SUGARS = ItemTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "sugars"));

		private Items() {}

		private static void forceInit() {}
	}

	public static final class EntityTypes {
		public static final Tags.IOptionalNamedTag<EntityType<?>> INFERNAL = EntityTypeTags.createOptional(new ResourceLocation(BiomancyMod.MOD_ID, "infernal"));

		private EntityTypes() {}

		private static void forceInit() {}
	}
}
