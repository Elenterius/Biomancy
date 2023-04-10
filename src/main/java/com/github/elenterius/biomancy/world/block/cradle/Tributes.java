package com.github.elenterius.biomancy.world.block.cradle;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

final class Tributes {

	static final ImmutableMap<Item, ImmutableTribute> ITEM_MAP = new ImmutableMap.Builder<Item, ImmutableTribute>()
			.put(ModItems.CREATOR_MIX.get(), new ImmutableTribute(20, 20, 19, 6, 6))

			.put(ModItems.HEALING_ADDITIVE.get(), new ImmutableTribute(0, 50, 1, -5, 0))
			.put(ModItems.REGENERATIVE_FLUID.get(), new ImmutableTribute(0, 5, 0, 0, 0))

			.put(Items.ROTTEN_FLESH, new ImmutableTribute(10, 0, 6, 20, 15))
			.put(ModItems.MOB_SINEW.get(), new ImmutableTribute(5, 0, 2, 0, 0))
			.put(ModItems.FLESH_BITS.get(), new ImmutableTribute(5, 0, 2, 0, 0))

			.put(Items.RABBIT_FOOT, new ImmutableTribute(40, 0, -10))
			.put(Items.SPIDER_EYE, new ImmutableTribute(10, 10, 5))
			.put(Items.FERMENTED_SPIDER_EYE, new ImmutableTribute(-10, 0, 25))
			.put(ModItems.TOXIN_GLAND.get(), new ImmutableTribute(0, 50, 30))
			.put(ModItems.VOLATILE_GLAND.get(), new ImmutableTribute(0, 20, 30))
			.put(ModItems.GENERIC_MOB_GLAND.get(), new ImmutableTribute(0, -5, -5))
			.put(Items.BONE_MEAL, new ImmutableTribute(2, -2, 0))
			.put(ModItems.MOB_MARROW.get(), new ImmutableTribute(5, -20, -5))
			.put(ModItems.WITHERED_MOB_MARROW.get(), new ImmutableTribute(-30, -30, 40))
			.put(ModItems.MOB_FANG.get(), new ImmutableTribute(0, 0, 30))
			.put(ModItems.MOB_CLAW.get(), new ImmutableTribute(0, 0, 20))

			.put(ModItems.LIVING_FLESH.get(), new ImmutableTribute(-999999, 999999, 999999))
			.build();

	static final ImmutableTribute GENERIC_BONES = new ImmutableTribute(5, -5, 0);
	static final ImmutableTribute GENERIC_RAW_MEATS = new ImmutableTribute(20, 0, 16, 5, 0);
	static final ImmutableTribute COOKED_MEATS = new ImmutableTribute(-999, 0, 10);
	static final ImmutableTribute INVALID_ITEM = new ImmutableTribute(-99, 0, 20);

	private Tributes() {}

	static ITribute from(ItemStack stack) {

		MobEffectTribute mobEffectTribute = MobEffectTribute.from(stack);
		ITribute tribute = findExistingTribute(stack);

		if (mobEffectTribute.isEmpty() && tribute.isEmpty()) {
			return INVALID_ITEM;
		}

		return new ImmutableTribute(tribute, mobEffectTribute);
	}

	static ITribute findExistingTribute(ItemStack stack) {
		if (stack.isEmpty()) return ITribute.EMPTY;

		ImmutableTribute foundTribute = ITEM_MAP.get(stack.getItem());
		if (foundTribute != null) return foundTribute;

		if (stack.is(ModTags.Items.RAW_MEATS)) return GENERIC_RAW_MEATS;
		if (stack.is(ModTags.Items.COOKED_MEATS)) return COOKED_MEATS;
		if (stack.is(Tags.Items.BONES)) return GENERIC_BONES;

		return ITribute.EMPTY;
	}

	record ImmutableTribute(int biomass, int lifeEnergy, int successModifier, int diseaseModifier, int hostileModifier) implements ITribute {
		public ImmutableTribute(int successModifier, int diseaseModifier, int hostileModifier) {
			this(0, 0, successModifier, diseaseModifier, hostileModifier);
		}

		public ImmutableTribute(ITribute a, ITribute b) {
			this(
					a.biomass() + b.biomass(),
					a.lifeEnergy() + b.lifeEnergy(),
					a.successModifier() + b.successModifier(),
					a.diseaseModifier() + b.diseaseModifier(),
					a.hostileModifier() + b.hostileModifier()
			);
		}

	}
}
