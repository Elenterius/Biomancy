package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModLoot;
import com.github.elenterius.biomancy.loot.CatMorningGiftLootModifier;
import com.github.elenterius.biomancy.loot.SpecialMobLootModifier;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.RegistryObject;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

	public ModGlobalLootModifierProvider(DataGenerator gen) {
		super(gen, BiomancyMod.MOD_ID);
	}

	@Override
	protected void start() {
		addLootModifier(ModLoot.SPECIAL_MOB_LOOT_SERIALIZER, new SpecialMobLootModifier());
		addLootModifier(ModLoot.CAT_MORNING_GIFT_SERIALIZER, new CatMorningGiftLootModifier());
	}

	protected <T extends IGlobalLootModifier> void addLootModifier(RegistryObject<GlobalLootModifierSerializer<T>> serializerHolder, T lootModifier) {
		add(serializerHolder.getId().getPath(), serializerHolder.get(), lootModifier);
	}

}
