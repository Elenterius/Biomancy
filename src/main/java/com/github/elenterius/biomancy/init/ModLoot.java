package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.loot.DespoilMobLootModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.github.elenterius.biomancy.BiomancyMod.createRL;

public final class ModLoot {

	public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLOBAL_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, BiomancyMod.MOD_ID);
	public static final RegistryObject<GlobalLootModifierSerializer<DespoilMobLootModifier>> DESPOIL_SERIALIZER = GLOBAL_MODIFIERS.register("despoil_mob_loot", DespoilMobLootModifier.Serializer::new);

	private ModLoot() {}

	public static final class Entity {

		public static final ResourceLocation FLESH_BLOB_SIZE_2 = createRL("entities/flesh_blob/size_2");
		public static final ResourceLocation FLESH_BLOB_SIZE_3 = createRL("entities/flesh_blob/size_3");
		public static final ResourceLocation FLESH_BLOB_SIZE_4 = createRL("entities/flesh_blob/size_4");
		public static final ResourceLocation FLESH_BLOB_SIZE_5 = createRL("entities/flesh_blob/size_5");
		public static final ResourceLocation FLESH_BLOB_SIZE_6 = createRL("entities/flesh_blob/size_6");
		public static final ResourceLocation FLESH_BLOB_SIZE_7 = createRL("entities/flesh_blob/size_7");
		public static final ResourceLocation FLESH_BLOB_SIZE_8 = createRL("entities/flesh_blob/size_8");
		public static final ResourceLocation FLESH_BLOB_SIZE_9 = createRL("entities/flesh_blob/size_9");
		public static final ResourceLocation FLESH_BLOB_SIZE_10 = createRL("entities/flesh_blob/size_10");

		private Entity() {}

	}

}
