package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
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
		public static final Tags.IOptionalNamedTag<Item> RAW_MEATS = ItemTags.createOptional(new ResourceLocation(BlightlingsMod.MOD_ID, "raw_meats"));

		private Items() {}

		private static void forceInit() {}
	}

	public static final class EntityTypes {
		public static final Tags.IOptionalNamedTag<EntityType<?>> INFERNAL = EntityTypeTags.createOptional(new ResourceLocation(BlightlingsMod.MOD_ID, "infernal"));

		private EntityTypes() {}

		private static void forceInit() {}
	}
}
