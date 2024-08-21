package com.github.elenterius.biomancy.api.tribute;

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

	public static final Tribute WRONG_ITEM = SimpleTribute.builder().successModifier(-99).diseaseModifier(5).hostileModifier(20).build();

	// tributes which are matched when no direct item match was found
	public static final Tribute RAW_MEAT_TAG = registerFuzzy(stack -> stack.is(ModItemTags.RAW_MEATS), SimpleTribute.builder().biomass(20).successModifier(16).diseaseModifier(5).hostileModifier(-5).build());
	public static final Tribute COOKED_MEAT_TAG = registerFuzzy(stack -> stack.is(ModItemTags.COOKED_MEATS), SimpleTribute.builder().successModifier(-999).hostileModifier(1).build());
	public static final Tribute BONE_TAG = registerFuzzy(stack -> stack.is(Tags.Items.BONES), SimpleTribute.builder().successModifier(3).diseaseModifier(-5).build());
	public static final Tribute FANG_TAG = registerFuzzy(stack -> stack.is(ModItemTags.FANGS), SimpleTribute.builder().successModifier(8).hostileModifier(5).build());
	public static final Tribute CLAW_TAG = registerFuzzy(stack -> stack.is(ModItemTags.CLAWS), SimpleTribute.builder().successModifier(8).hostileModifier(5).build());

	// normal tributes
	static {
		register(ModItems.CREATOR_MIX.get(), SimpleTribute.builder().biomass(20).lifeEnergy(20).successModifier(19).diseaseModifier(6).hostileModifier(-12).build());
		register(ModItems.PRIMORDIAL_CORE.get(), SimpleTribute.builder().biomass(80).successModifier(64).anomalyModifier(100).diseaseModifier(50).build());

		register(ModItems.LIVING_FLESH.get(), SimpleTribute.builder().biomass(10).lifeEnergy(10).successModifier(40).anomalyModifier(55).build());
		register(ModItems.BLOOMBERRY.get(), SimpleTribute.builder().successModifier(10).lifeEnergy(20).anomalyModifier(20).build());
		register(Items.GOLDEN_APPLE, SimpleTribute.builder().successModifier(10).hostileModifier(-100).build());
		register(Items.ENCHANTED_GOLDEN_APPLE, SimpleTribute.builder().lifeEnergy(15).successModifier(40).hostileModifier(-200).build());
		register(Items.CAKE, SimpleTribute.builder().hostileModifier(-80).diseaseModifier(10).build());

		register(ModItems.HEALING_ADDITIVE.get(), SimpleTribute.builder().lifeEnergy(55).successModifier(1).diseaseModifier(-5).hostileModifier(-10).build());
		register(ModItems.REGENERATIVE_FLUID.get(), SimpleTribute.builder().lifeEnergy(5).hostileModifier(-1).build());
		register(ModItems.WITHERING_OOZE.get(), SimpleTribute.builder().biomass(-5).successModifier(-1).diseaseModifier(1).build());
		register(ModItems.CORROSIVE_ADDITIVE.get(), SimpleTribute.builder().biomass(-20).successModifier(-5).diseaseModifier(5).build());
		register(ModItems.ORGANIC_COMPOUND.get(), SimpleTribute.builder().successModifier(10).build());
		register(ModItems.EXOTIC_COMPOUND.get(), SimpleTribute.builder().successModifier(5).anomalyModifier(20).build());
		register(ModItems.GENETIC_COMPOUND.get(), SimpleTribute.builder().successModifier(1).diseaseModifier(-10).build());
		register(ModItems.FRENZY_SERUM.get(), SimpleTribute.builder().hostileModifier(1000).anomalyModifier(10).build());
		register(ModItems.ABSORPTION_BOOST.get(), SimpleTribute.builder().lifeEnergy(60).build());
		register(ModItems.REJUVENATION_SERUM.get(), SimpleTribute.builder().lifeEnergy(60).build());
		register(ModItems.CLEANSING_SERUM.get(), SimpleTribute.builder().lifeEnergy(-20).diseaseModifier(-120).build());

		register(Items.ROTTEN_FLESH, SimpleTribute.builder().biomass(10).successModifier(-10).diseaseModifier(20).build());
		register(ModItems.MOB_SINEW.get(), SimpleTribute.builder().biomass(5).successModifier(2).hostileModifier(-2).build());
		register(ModItems.FLESH_BITS.get(), SimpleTribute.builder().biomass(5).successModifier(2).hostileModifier(-2).build());

		register(Items.RABBIT_FOOT, SimpleTribute.builder().successModifier(1000).hostileModifier(-50).anomalyModifier(50).build());
		register(Items.SPIDER_EYE, SimpleTribute.builder().successModifier(10).diseaseModifier(10).hostileModifier(-5).build());
		register(Items.FERMENTED_SPIDER_EYE, SimpleTribute.builder().successModifier(-10).hostileModifier(-10).build());
		register(ModItems.TOXIN_GLAND.get(), SimpleTribute.builder().successModifier(-5).diseaseModifier(50).build());
		register(ModItems.VOLATILE_GLAND.get(), SimpleTribute.builder().successModifier(-5).diseaseModifier(20).build());
		register(ModItems.GENERIC_MOB_GLAND.get(), SimpleTribute.builder().diseaseModifier(-5).hostileModifier(-20).build());

		register(Items.BONE, SimpleTribute.builder().successModifier(3).diseaseModifier(-10).build());
		register(Items.BONE_MEAL, SimpleTribute.builder().successModifier(1).diseaseModifier(-1).build());
		register(ModItems.MOB_MARROW.get(), SimpleTribute.builder().successModifier(5).diseaseModifier(-20).hostileModifier(-10).build());
		register(ModItems.WITHERED_MOB_MARROW.get(), SimpleTribute.builder().successModifier(-30).diseaseModifier(-40).build());
		register(ModItems.MOB_FANG.get(), SimpleTribute.builder().successModifier(8).hostileModifier(5).build());
		register(ModItems.MOB_CLAW.get(), SimpleTribute.builder().successModifier(8).hostileModifier(5).build());

		register(Items.ENDER_PEARL, SimpleTribute.builder().hostileModifier(50).anomalyModifier(50).build());

		register(Items.NETHER_STAR, SimpleTribute.builder().lifeEnergy(15_000).hostileModifier(100).diseaseModifier(100).build());
		register(Items.TOTEM_OF_UNDYING, SimpleTribute.builder().lifeEnergy(2_500).successModifier(100).hostileModifier(-500).build());

		register(ModItems.ELASTIC_FIBERS.get(), SimpleTribute.builder().diseaseModifier(1).anomalyModifier(1).build());
		register(ModItems.TOUGH_FIBERS.get(), SimpleTribute.builder().diseaseModifier(1).anomalyModifier(1).build());
	}

	private Tributes() {}

	public static Tribute register(Item item, Tribute tribute) {
		TRIBUTES.put(item, tribute);
		return tribute;
	}

	public static Tribute registerFuzzy(Predicate<ItemStack> predicate, Tribute tribute) {
		FUZZY_TRIBUTES.add(new FuzzyTribute(predicate, tribute));
		return tribute;
	}

	public static Tribute getTribute(ItemStack stack) {

		Tribute mobEffectTribute = MobEffectTribute.from(stack);
		Tribute tribute = findExistingTribute(stack);

		if (mobEffectTribute.isEmpty() && tribute.isEmpty()) {
			return WRONG_ITEM;
		}

		return combineTributes(tribute, mobEffectTribute);
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

	private static Tribute combineTributes(Tribute a, Tribute b) {
		return new SimpleTribute(
				a.biomass() + b.biomass(),
				a.lifeEnergy() + b.lifeEnergy(),
				a.successModifier() + b.successModifier(),
				a.diseaseModifier() + b.diseaseModifier(),
				a.hostileModifier() + b.hostileModifier(),
				a.anomalyModifier() + b.anomalyModifier()
		);
	}

	record FuzzyTribute(Predicate<ItemStack> predicate, Tribute tribute) {}

}
