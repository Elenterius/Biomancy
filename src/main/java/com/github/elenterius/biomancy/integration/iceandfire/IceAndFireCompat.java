package com.github.elenterius.biomancy.integration.iceandfire;

import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

public final class IceAndFireCompat {

	private IceAndFireCompat() {}

	public static void onPostSetup() {
		List<Item> hearts = List.of(getItem("fire_dragon_heart"), getItem("ice_dragon_heart"), getItem("lightning_dragon_heart"), getItem("hydra_heart"));
		Tribute heartTribute = SimpleTribute.builder().biomass(100).lifeEnergy(15_000).successModifier(1000).anomalyModifier(30).build();
		for (Item heart : hearts) {
			Tributes.register(heart, heartTribute);
		}

		List<Item> bloods = List.of(getItem("fire_dragon_blood"), getItem("ice_dragon_blood"), getItem("lightning_dragon_blood"));
		Tribute bloodTribute = SimpleTribute.builder().lifeEnergy(250).successModifier(40).anomalyModifier(5).build();
		for (Item blood : bloods) {
			Tributes.register(blood, bloodTribute);
		}

		Tributes.register(getItem("hydra_fang"), SimpleTribute.builder().successModifier(-5).diseaseModifier(50).hostileModifier(5).build());
		Tributes.register(getItem("myrmex_stinger"), SimpleTribute.builder().successModifier(-5).diseaseModifier(50).hostileModifier(5).build());
		Tributes.register(getItem("sea_serpent_fang"), SimpleTribute.builder().successModifier(8).hostileModifier(5).build());
		Tributes.register(getItem("hippogryph_talon"), SimpleTribute.builder().successModifier(8).hostileModifier(5).build());
	}

	private static Block getBlock(String name) {
		ResourceLocation id = new ResourceLocation("iceandfire", name);
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(id));
	}

	private static Item getItem(String name) {
		ResourceLocation id = new ResourceLocation("iceandfire", name);
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id));
	}

}
