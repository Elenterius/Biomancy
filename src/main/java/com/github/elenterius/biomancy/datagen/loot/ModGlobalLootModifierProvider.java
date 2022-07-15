package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModLoot;
import com.github.elenterius.biomancy.loot.DespoilMobLootModifier;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

	public ModGlobalLootModifierProvider(DataGenerator gen) {
		super(gen, BiomancyMod.MOD_ID);
	}

	@Override
	protected void start() {
		add("despoil_mobs", ModLoot.DESPOIL_SERIALIZER.get(), new DespoilMobLootModifier());
	}

}
