package com.github.elenterius.biomancy.tribute;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ApiStatus.Experimental
public final class Tributes {

	private static final Map<Item, Tribute> TRIBUTES = new HashMap<>();
	private static final List<FuzzyTribute> FUZZY_TRIBUTES = new ArrayList<>();

	public static final Tribute INVALID_ITEM = TributeImpl.builder().successModifier(-99).diseaseModifier(5).hostileModifier(20).create();

	public static final Tribute EXOTIC_MEAL = register(ModItems.CREATOR_MIX.get(), TributeImpl.builder().biomass(20).lifeEnergy(20).successModifier(19).diseaseModifier(6).hostileModifier(-12).create());
	public static final Tribute PRIMORDIAL_CORE = register(ModItems.PRIMORDIAL_CORE.get(), TributeImpl.builder().biomass(80).successModifier(64).anomalyModifier(100).diseaseModifier(50).create());

	public static final Tribute LIVING_FLESH = register(ModItems.LIVING_FLESH.get(), TributeImpl.builder().biomass(10).lifeEnergy(10).successModifier(40).anomalyModifier(55).create());
	public static final Tribute BLOOMBERRY = register(ModItems.BLOOMBERRY.get(), TributeImpl.builder().successModifier(10).lifeEnergy(20).anomalyModifier(20).create());
	public static final Tribute GOLDEN_APPLE = register(Items.GOLDEN_APPLE, TributeImpl.builder().successModifier(10).hostileModifier(-100).create());
	public static final Tribute ENCHANTED_GOLDEN_APPLE = register(Items.ENCHANTED_GOLDEN_APPLE, TributeImpl.builder().lifeEnergy(15).successModifier(40).hostileModifier(-200).create());
	public static final Tribute CAKE = register(Items.CAKE, TributeImpl.builder().hostileModifier(-80).diseaseModifier(10).create());

	public static final Tribute HEALING_ADDITIVE = register(ModItems.HEALING_ADDITIVE.get(), TributeImpl.builder().lifeEnergy(50).successModifier(1).diseaseModifier(-5).hostileModifier(-10).create());
	public static final Tribute REGENERATIVE_FLUID = register(ModItems.REGENERATIVE_FLUID.get(), TributeImpl.builder().lifeEnergy(5).hostileModifier(-1).create());

	public static final Tribute ROTTEN_FLESH = register(Items.ROTTEN_FLESH, TributeImpl.builder().biomass(10).successModifier(-10).diseaseModifier(20).create());
	public static final Tribute MOB_SINEW = register(ModItems.MOB_SINEW.get(), TributeImpl.builder().biomass(5).successModifier(2).hostileModifier(-2).create());
	public static final Tribute FLESH_BITS = register(ModItems.FLESH_BITS.get(), TributeImpl.builder().biomass(5).successModifier(2).hostileModifier(-2).create());

	public static final Tribute RABBIT_FOOT = register(Items.RABBIT_FOOT, TributeImpl.builder().successModifier(1000).hostileModifier(-50).anomalyModifier(50).create());
	public static final Tribute SPIDER_EYE = register(Items.SPIDER_EYE, TributeImpl.builder().successModifier(10).diseaseModifier(10).hostileModifier(-5).create());
	public static final Tribute FERMENTED_SPIDER_EYE = register(Items.FERMENTED_SPIDER_EYE, TributeImpl.builder().successModifier(-10).hostileModifier(-10).create());
	public static final Tribute TOXIN_GLAND = register(ModItems.TOXIN_GLAND.get(), TributeImpl.builder().successModifier(-5).diseaseModifier(50).create());
	public static final Tribute VOLATILE_GLAND = register(ModItems.VOLATILE_GLAND.get(), TributeImpl.builder().successModifier(-5).diseaseModifier(20).create());
	public static final Tribute GENERIC_MOB_GLAND = register(ModItems.GENERIC_MOB_GLAND.get(), TributeImpl.builder().diseaseModifier(-5).hostileModifier(-20).create());
	public static final Tribute BONE = register(Items.BONE, TributeImpl.builder().successModifier(3).diseaseModifier(-10).create());
	public static final Tribute BONE_MEAL = register(Items.BONE_MEAL, TributeImpl.builder().successModifier(1).diseaseModifier(-1).create());
	public static final Tribute MOB_MARROW = register(ModItems.MOB_MARROW.get(), TributeImpl.builder().successModifier(5).diseaseModifier(-20).hostileModifier(-10).create());
	public static final Tribute WITHERED_MOB_MARROW = register(ModItems.WITHERED_MOB_MARROW.get(), TributeImpl.builder().successModifier(-30).diseaseModifier(-40).create());
	public static final Tribute MOB_FANG = register(ModItems.MOB_FANG.get(), TributeImpl.builder().successModifier(5).hostileModifier(5).create());
	public static final Tribute MOB_CLAW = register(ModItems.MOB_CLAW.get(), TributeImpl.builder().successModifier(5).hostileModifier(5).create());
	public static final Tribute ENDER_PEARL = register(Items.ENDER_PEARL, TributeImpl.builder().hostileModifier(50).anomalyModifier(50).create());
	public static final Tribute NETHER_STAR = register(Items.NETHER_STAR, TributeImpl.builder().lifeEnergy(10_000).hostileModifier(100).diseaseModifier(100).create());

	public static final Tribute ELASTIC_FIBERS = register(ModItems.ELASTIC_FIBERS.get(), TributeImpl.builder().diseaseModifier(1).anomalyModifier(1).create());
	public static final Tribute TOUGH_FIBERS = register(ModItems.TOUGH_FIBERS.get(), TributeImpl.builder().diseaseModifier(1).anomalyModifier(1).create());

	// tributes which are matched when no tribute was found beforehand
	public static final Tribute RAW_MEATS = registerFuzzy(stack -> stack.is(ModItemTags.RAW_MEATS), TributeImpl.builder().biomass(20).successModifier(16).diseaseModifier(5).hostileModifier(-5).create());
	public static final Tribute COOKED_MEATS = registerFuzzy(stack -> stack.is(ModItemTags.COOKED_MEATS), TributeImpl.builder().successModifier(-999).create());
	public static final Tribute BONES = registerFuzzy(stack -> stack.is(Tags.Items.BONES), TributeImpl.builder().successModifier(5).diseaseModifier(-5).create());

	private Tributes() {}

	public static Tribute register(Item item, Tribute tribute) {
		TRIBUTES.put(item, tribute);
		return tribute;
	}

	public static Tribute registerFuzzy(Predicate<ItemStack> predicate, Tribute tribute) {
		FUZZY_TRIBUTES.add(new FuzzyTribute(predicate, tribute));
		return tribute;
	}

	public static Tribute from(ItemStack stack) {

		Tribute mobEffectTribute = MobEffectTribute.from(stack);
		Tribute tribute = findExistingTribute(stack);

		if (mobEffectTribute.isEmpty() && tribute.isEmpty()) {
			return INVALID_ITEM;
		}

		return new TributeImpl(tribute, mobEffectTribute);
	}

	private static Tribute findExistingTribute(ItemStack stack) {
		if (stack.isEmpty()) return Tribute.EMPTY;

		Tribute foundTribute = TRIBUTES.get(stack.getItem());
		if (foundTribute != null) return foundTribute;

		for (FuzzyTribute fuzzyTribute : FUZZY_TRIBUTES) {
			if (fuzzyTribute.predicate().test(stack)) return fuzzyTribute.tribute();
		}

		return Tribute.EMPTY;
	}

	record FuzzyTribute(Predicate<ItemStack> predicate, Tribute tribute) {}
}
