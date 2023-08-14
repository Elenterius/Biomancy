package com.github.elenterius.biomancy.block.cradle;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class Tributes {

	static final Map<Item, ITribute> ITEM_MAP = new ImmutableMap.Builder<Item, ITribute>()
			.put(ModItems.CREATOR_MIX.get(), Tribute.builder().biomass(20).lifeEnergy(20).successModifier(19).diseaseModifier(6).hostileModifier(-12).create())
			.put(ModItems.PRIMORDIAL_CORE.get(), Tribute.builder().biomass(80).successModifier(64).anomalyModifier(100).diseaseModifier(50).create())
			.put(ModItems.LIVING_FLESH.get(), Tribute.builder().biomass(10).lifeEnergy(10).successModifier(40).anomalyModifier(55).create())
			.put(Items.GOLDEN_APPLE, Tribute.builder().successModifier(10).hostileModifier(-100).create())
			.put(Items.ENCHANTED_GOLDEN_APPLE, Tribute.builder().lifeEnergy(15).successModifier(40).hostileModifier(-200).create())
			.put(Items.CAKE, Tribute.builder().hostileModifier(-80).diseaseModifier(10).create())

			.put(ModItems.HEALING_ADDITIVE.get(), Tribute.builder().lifeEnergy(50).successModifier(1).diseaseModifier(-5).hostileModifier(-10).create())
			.put(ModItems.REGENERATIVE_FLUID.get(), Tribute.builder().lifeEnergy(5).hostileModifier(-1).create())

			.put(Items.ROTTEN_FLESH, Tribute.builder().biomass(10).successModifier(-10).diseaseModifier(20).create())
			.put(ModItems.MOB_SINEW.get(), Tribute.builder().biomass(5).successModifier(2).hostileModifier(-2).create())
			.put(ModItems.FLESH_BITS.get(), Tribute.builder().biomass(5).successModifier(2).hostileModifier(-2).create())

			.put(Items.RABBIT_FOOT, Tribute.builder().successModifier(1000).hostileModifier(-50).anomalyModifier(50).create())
			.put(Items.SPIDER_EYE, Tribute.builder().successModifier(10).diseaseModifier(10).hostileModifier(-5).create())
			.put(Items.FERMENTED_SPIDER_EYE, Tribute.builder().successModifier(-10).hostileModifier(-10).create())
			.put(ModItems.TOXIN_GLAND.get(), Tribute.builder().successModifier(-5).diseaseModifier(50).create())
			.put(ModItems.VOLATILE_GLAND.get(), Tribute.builder().successModifier(-5).diseaseModifier(20).create())
			.put(ModItems.GENERIC_MOB_GLAND.get(), Tribute.builder().diseaseModifier(-5).hostileModifier(-20).create())
			.put(Items.BONE, Tribute.builder().successModifier(3).diseaseModifier(-10).create())
			.put(Items.BONE_MEAL, Tribute.builder().successModifier(1).diseaseModifier(-1).create())
			.put(ModItems.MOB_MARROW.get(), Tribute.builder().successModifier(5).diseaseModifier(-20).hostileModifier(-10).create())
			.put(ModItems.WITHERED_MOB_MARROW.get(), Tribute.builder().successModifier(-30).diseaseModifier(-40).create())
			.put(ModItems.MOB_FANG.get(), Tribute.builder().successModifier(5).hostileModifier(5).create())
			.put(ModItems.MOB_CLAW.get(), Tribute.builder().successModifier(5).hostileModifier(5).create())
			.put(Items.ENDER_PEARL, Tribute.builder().hostileModifier(50).anomalyModifier(50).create())

			.put(ModItems.ELASTIC_FIBERS.get(), Tribute.builder().diseaseModifier(1).anomalyModifier(1).create())
			.put(ModItems.TOUGH_FIBERS.get(), Tribute.builder().diseaseModifier(1).anomalyModifier(1).create())
			.build();

	static final List<FuzzyTribute> FUZZY_TRIBUTES = List.of(
			new FuzzyTribute(stack -> stack.is(ModItemTags.RAW_MEATS), Tribute.builder().biomass(20).successModifier(16).diseaseModifier(5).hostileModifier(-5).create()),
			new FuzzyTribute(stack -> stack.is(ModItemTags.COOKED_MEATS), Tribute.builder().successModifier(-999).create()),
			new FuzzyTribute(stack -> stack.is(Tags.Items.BONES), Tribute.builder().successModifier(5).diseaseModifier(-5).create())
	);
	static final ITribute INVALID_ITEM = Tribute.builder().successModifier(-99).diseaseModifier(5).hostileModifier(20).create();

	private Tributes() {}

	static ITribute from(ItemStack stack) {

		MobEffectTribute mobEffectTribute = MobEffectTribute.from(stack);
		ITribute tribute = findExistingTribute(stack);

		if (mobEffectTribute.isEmpty() && tribute.isEmpty()) {
			return INVALID_ITEM;
		}

		return new Tribute(tribute, mobEffectTribute);
	}

	static ITribute findExistingTribute(ItemStack stack) {
		if (stack.isEmpty()) return ITribute.EMPTY;

		ITribute foundTribute = ITEM_MAP.get(stack.getItem());
		if (foundTribute != null) return foundTribute;

		for (FuzzyTribute fuzzyTribute : FUZZY_TRIBUTES) {
			if (fuzzyTribute.predicate.test(stack)) return fuzzyTribute.tribute;
		}

		return ITribute.EMPTY;
	}

	record FuzzyTribute(Predicate<ItemStack> predicate, ITribute tribute) {}

}
