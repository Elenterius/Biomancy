package com.github.elenterius.biomancy.integration.farmersdelight;

import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public final class FarmersDelightCompat {

	private FarmersDelightCompat() {}

	public static void onPostSetup() {
		Tributes.register(getItem("melon_juice"), SimpleTribute.builder().lifeEnergy(10).build());

		Tribute milkBucketTribute = Tributes.getTribute(Items.MILK_BUCKET.getDefaultInstance());
		Tribute milkBottleTribute = SimpleTribute.builder()
				.biomass(milkBucketTribute.biomass() / 4)
				.lifeEnergy(milkBucketTribute.lifeEnergy() / 4)
				.successModifier(milkBucketTribute.successModifier() / 4)
				.diseaseModifier(milkBucketTribute.diseaseModifier() / 4)
				.hostileModifier(milkBucketTribute.hostileModifier() / 4)
				.anomalyModifier(milkBucketTribute.anomalyModifier() / 4)
				.build();
		Tributes.register(getItem("milk_bottle"), milkBottleTribute);

		Tribute hotCocoaTribute = SimpleTribute.builder()
				.biomass(milkBucketTribute.biomass() / 4)
				.lifeEnergy(milkBucketTribute.lifeEnergy() / 4)
				.successModifier(milkBucketTribute.successModifier() / 4)
				.diseaseModifier(milkBucketTribute.diseaseModifier() / 4)
				.hostileModifier(milkBucketTribute.hostileModifier() / 4 - 80)
				.anomalyModifier(milkBucketTribute.anomalyModifier() / 4)
				.build();
		Tributes.register(getItem("hot_cocoa"), hotCocoaTribute);
	}

	private static Block getBlock(String name) {
		ResourceLocation id = new ResourceLocation("farmersdelight", name);
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(id));
	}

	private static Item getItem(String name) {
		ResourceLocation id = new ResourceLocation("farmersdelight", name);
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id));
	}

}
