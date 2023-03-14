package com.github.elenterius.biomancy.world.block.cradle;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.function.Predicate;

public class SacrificeHandler implements INBTSerializable<CompoundTag> {

	private static final int MAX_VALUE = 100;
	private byte biomass;
	private byte lifeEnergy;
	private int successValue;
	private int diseaseValue;
	private int hostileValue;

	public int getSuccessModifier(ItemStack stack) {
		return Tribute.from(stack).successModifier;
	}

	public boolean isFull() {
		return lifeEnergy >= MAX_VALUE && biomass >= MAX_VALUE;
	}

	public void setBiomass(int amount) {
		biomass = (byte) Mth.clamp(amount, 0, MAX_VALUE);
	}

	public void addBiomass(int value) {
		setBiomass(biomass + value);
	}

	public int getBiomassAmount() {
		return biomass;
	}

	public float getBiomassPct() {
		return biomass / (float) MAX_VALUE;
	}

	public void setLifeEnergy(int amount) {
		lifeEnergy = (byte) Mth.clamp(amount, 0, MAX_VALUE);
	}

	public void addLifeEnergy(int value) {
		setBiomass(lifeEnergy + value);
	}

	public int getLifeEnergyAmount() {
		return lifeEnergy;
	}

	public float getLifeEnergyPct() {
		return lifeEnergy / (float) MAX_VALUE;
	}

	public boolean isValidIngredient(ItemStack stack) {
		return Tribute.hasTribute(stack);
	}

	public boolean isLifeEnergySource(ItemStack stack) {
		return Tribute.from(stack).type.hasLifeEnergy();
	}

	public float getSuccessChance() {
		return successValue / 100f;
	}

	public float getHostileChance() {
		return hostileValue / 100f;
	}

	public float getTumorFactor() {
		return diseaseValue / 100f;
	}

	public boolean hasModifiers() {
		return diseaseValue != 0 || hostileValue != 0;
	}

	public boolean addItem(ItemStack stack) {
		if (!stack.isEmpty()) {
			Tribute tribute = Tribute.from(stack);
			boolean isBiomass = tribute.type.isValidBiomass();
			boolean hasLifeEnergy = tribute.type.hasLifeEnergy();

			boolean consumeItem = true;

			if (isBiomass) {
				if (biomass < MAX_VALUE) setBiomass(biomass + tribute.typeValue);
				else consumeItem = false;
			}

			if (hasLifeEnergy) {
				if (lifeEnergy < MAX_VALUE) {
					setLifeEnergy(lifeEnergy + tribute.typeValue);
					consumeItem = true;
				} else consumeItem = false;
			}

			if (consumeItem) {
				diseaseValue += tribute.diseaseModifier;
				hostileValue += tribute.hostileModifier;
				successValue += tribute.successModifier;
				stack.shrink(1);
				return true;
			}
		}

		return false;
	}

	public void reset() {
		biomass = 0;
		lifeEnergy = 0;

		diseaseValue = 0;
		hostileValue = 0;
		successValue = 0;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putByte("Biomass", biomass);
		tag.putByte("LifeEnergy", lifeEnergy);

		tag.putInt("Disease", diseaseValue);
		tag.putInt("Hostile", hostileValue);
		tag.putInt("Success", successValue);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		biomass = tag.getByte("Biomass");
		lifeEnergy = tag.getByte("LifeEnergy");

		diseaseValue = tag.getInt("Disease");
		hostileValue = tag.getInt("Hostile");
		successValue = tag.getInt("Success");
	}

	enum Type {
		INVALID, RAW_MEAT, MODIFIER, CREATOR_MIX, HEAL_POTION;

		public boolean isValidBiomass() {
			return this == RAW_MEAT || this == CREATOR_MIX;
		}

		public boolean hasLifeEnergy() {
			return this == HEAL_POTION || this == CREATOR_MIX;
		}
	}

	record Tribute(Type type, int typeValue, int successModifier, int diseaseModifier, int hostileModifier) {

		static final ImmutableMap<Item, Tribute> ITEM_MAP = new ImmutableMap.Builder<Item, Tribute>()
				.put(ModItems.CREATOR_MIX.get(), new Tribute(Type.CREATOR_MIX, 20, 19, 6, 6))

				.put(ModItems.HEALING_ADDITIVE.get(), new Tribute(Type.HEAL_POTION, 50, 1, -5, 0))
				.put(ModItems.REGENERATIVE_FLUID.get(), new Tribute(Type.HEAL_POTION, 5, 0, 0, 0))

				.put(Items.ROTTEN_FLESH, new Tribute(Type.RAW_MEAT, 10, 6, 35, 15))
				.put(Items.CHICKEN, new Tribute(Type.RAW_MEAT, 20, 15, 20, 0))
				.put(ModItems.MOB_SINEW.get(), new Tribute(Type.RAW_MEAT, 5, 2, 0, 0))
				.put(ModItems.FLESH_BITS.get(), new Tribute(Type.RAW_MEAT, 5, 2, 0, 0))

				.put(Items.RABBIT_FOOT, new Tribute(Type.MODIFIER, 40, 0, -10))
				.put(Items.SPIDER_EYE, new Tribute(Type.MODIFIER, 10, 10, 15))
				.put(Items.FERMENTED_SPIDER_EYE, new Tribute(Type.MODIFIER, -10, 0, 25))
				.put(ModItems.TOXIN_GLAND.get(), new Tribute(Type.MODIFIER, 0, 50, 40))
				.put(ModItems.VOLATILE_GLAND.get(), new Tribute(Type.MODIFIER, 0, 20, 40))
				.put(ModItems.GENERIC_MOB_GLAND.get(), new Tribute(Type.MODIFIER, 0, -5, -5))
				.put(Items.BONE_MEAL, new Tribute(Type.MODIFIER, 2, -2, 0))
				.put(ModItems.MOB_MARROW.get(), new Tribute(Type.MODIFIER, 5, -20, -5))
				.put(ModItems.WITHERED_MOB_MARROW.get(), new Tribute(Type.MODIFIER, -30, -30, 50))
				.put(ModItems.MOB_FANG.get(), new Tribute(Type.MODIFIER, 0, 0, 30))
				.put(ModItems.MOB_CLAW.get(), new Tribute(Type.MODIFIER, 0, 0, 20))

				.put(ModItems.LIVING_FLESH.get(), new Tribute(Type.INVALID, -999999, 999999, 999999))
				.build();

		static final Tribute GENERIC_BONES = new Tribute(Type.MODIFIER, 5, -5, 0);
		static final Tribute GENERIC_RAW_MEATS = new Tribute(Type.RAW_MEAT, 20, 16, 5, 0);
		static final Tribute COOKED_MEATS = new Tribute(Type.INVALID, -999, 0, 10);
		static final Tribute INVALID_ITEM = new Tribute(Type.INVALID, -99, 0, 20);
		static final Tribute LV1_HEALING_POTION = createHealingPotionModifier(1);
		static final Tribute LV2_HEALING_POTION = createHealingPotionModifier(2);
		static final Tribute EMPTY = new Tribute(Type.INVALID, 0, 0, 0);
		private static final Predicate<MobEffect> VALID_STATUS_EFFECTS = effect -> effect == MobEffects.HEAL || effect == MobEffects.REGENERATION;

		public Tribute(Type type, int successModifier, int diseaseModifier, int hostileModifier) {
			this(type, 0, successModifier, diseaseModifier, hostileModifier);
		}

		public static Tribute from(ItemStack stack) {
			if (stack.isEmpty()) return EMPTY;

			if (stack.getItem() instanceof PotionItem) {
				int healingLevel = getHealingPotionLevel(stack);
				return getHealPotionModifier(healingLevel);
			}

			Tribute foundTribute = ITEM_MAP.get(stack.getItem());
			if (foundTribute != null) return foundTribute;

			if (stack.is(ModTags.Items.RAW_MEATS)) return GENERIC_RAW_MEATS;
			if (stack.is(ModTags.Items.COOKED_MEATS)) return COOKED_MEATS;
			if (stack.is(Tags.Items.BONES)) return GENERIC_BONES;

			return INVALID_ITEM;
		}

		public static boolean hasTribute(ItemStack stack) {
			return ITEM_MAP.containsKey(stack.getItem())
					|| stack.is(Tags.Items.BONES)
					|| stack.is(ModTags.Items.RAW_MEATS)
					|| stack.is(ModTags.Items.COOKED_MEATS)
					|| stack.getItem() instanceof PotionItem;
		}

		private static Tribute getHealPotionModifier(int healingLevel) {
			return switch (healingLevel) {
				case 0 -> EMPTY;
				case 1 -> LV1_HEALING_POTION;
				case 2 -> LV2_HEALING_POTION;
				default -> createHealingPotionModifier(healingLevel);
			};
		}

		private static Tribute createHealingPotionModifier(int healingLevel) {
			return new Tribute(Type.HEAL_POTION, healingLevel * 50, 0, 0, 0);
		}

		private static int getHealingPotionLevel(ItemStack stack) {
			List<MobEffectInstance> effectInstances = PotionUtils.getMobEffects(stack);
			for (MobEffectInstance instance : effectInstances) {
				if (VALID_STATUS_EFFECTS.test(instance.getEffect())) {
					return instance.getAmplifier() + 1;
				}
			}
			return 0;
		}
	}

}
