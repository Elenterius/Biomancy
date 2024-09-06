package com.github.elenterius.biomancy.integration.alexscaves;

import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public final class AlexsCavesCompat {

	private AlexsCavesCompat() {}

	public static void onPostSetup() {
		Item dinosaurChop = getBlock("dinosaur_chop").asItem();
		Tributes.register(dinosaurChop, SimpleTribute.builder().biomass(100).successModifier(65).hostileModifier(20).anomalyModifier(30).build());
		Nutrients.registerFuel(dinosaurChop, Nutrients.RAW_MEAT_NUTRITION_MODIFIER.applyAsInt(3) * 12);
		Nutrients.registerRepairMaterial(dinosaurChop, Nutrients.RAW_MEAT_NUTRITION_MODIFIER.applyAsInt(3) * 2 * 12);

		Item greenSoylent = getItem("green_soylent");
		Tributes.register(greenSoylent, SimpleTribute.builder().biomass(15).successModifier(15).build());
		Nutrients.registerFuel(greenSoylent, Nutrients.RAW_MEAT_NUTRITION_MODIFIER.applyAsInt(3));
		Nutrients.registerRepairMaterial(greenSoylent, Nutrients.RAW_MEAT_NUTRITION_MODIFIER.applyAsInt(3) * 2);
	}

	private static Block getBlock(String name) {
		ResourceLocation id = new ResourceLocation("alexscaves", name);
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(id));
	}

	private static Item getItem(String name) {
		ResourceLocation id = new ResourceLocation("alexscaves", name);
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id));
	}

}
