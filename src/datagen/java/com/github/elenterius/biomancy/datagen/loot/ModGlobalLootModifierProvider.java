package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModLoot;
import com.github.elenterius.biomancy.loot.CatMorningGiftLootModifier;
import com.github.elenterius.biomancy.loot.DespoilLootModifier;
import com.mojang.serialization.Codec;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.RegistryObject;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

	public ModGlobalLootModifierProvider(PackOutput packOutput) {
		super(packOutput, BiomancyMod.MOD_ID);
	}

	@Override
	protected void start() {
		addLootModifier(ModLoot.DESPOIL_SERIALIZER, new DespoilLootModifier());
		addLootModifier(ModLoot.CAT_MORNING_GIFT_SERIALIZER, new CatMorningGiftLootModifier());
	}

	protected <T extends IGlobalLootModifier> void addLootModifier(RegistryObject<Codec<? extends T>> codecSupplier, T lootModifier) {
		add(codecSupplier.getId().getPath(), lootModifier);
	}

}
