package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;

class SacrificeHelper {

	private static final ImmutableMap<Item, Modifier> ITEM_MODIFIER_MAP = new ImmutableMap.Builder<Item, Modifier>()
			.put(Items.BONE_MEAL, new Modifier(Type.BONE, 5, 0, 0))
			.put(ModItems.MOB_MARROW.get(), new Modifier(Type.BONE, 5, -20, -5))
			.put(ModItems.MOB_FANG.get(), new Modifier(Type.BONE, 10, 0, 15))
			.put(ModItems.WITHERED_MOB_MARROW.get(), new Modifier(Type.BONE, -5, 50, 5))
			.put(ModItems.FLESH_BITS.get(), new Modifier(Type.RAW_MEAT, 2, 0, 0))
			.put(Items.ROTTEN_FLESH, new Modifier(Type.RAW_MEAT, -5, 35, 15))
			.put(Items.CHICKEN, new Modifier(Type.RAW_MEAT, 25, 25, 0))
			.put(Items.RABBIT_FOOT, new Modifier(Type.RAW_MEAT, 40, 0, -5))
			.put(Items.SPIDER_EYE, new Modifier(Type.RAW_MEAT, -5, 5, 25))
			.put(Items.FERMENTED_SPIDER_EYE, new Modifier(Type.RAW_MEAT, -10, 8, 50))
			.put(ModItems.VENOM_GLAND.get(), new Modifier(Type.RAW_MEAT, -8, 10, 40))
			.put(ModItems.VOLATILE_GLAND.get(), new Modifier(Type.RAW_MEAT, -8, 30, 20))
			.put(ModItems.MOB_GLAND.get(), new Modifier(Type.RAW_MEAT, 0, 10, 0))
			.build();

	private static final Modifier EMPTY_MODIFIER = new Modifier(Type.EMPTY, 0, 0, 0);
	private static final Modifier INVALID_ITEM_MODIFIER = new Modifier(Type.EMPTY, -20, 0, 20);
	private static final Modifier BONES_MODIFIER = new Modifier(Type.BONE, 16, 4, 4);
	private static final Modifier RAW_MEATS_MODIFIER = new Modifier(Type.RAW_MEAT, 24, 4, 4);
	private static final Modifier COOKED_MEATS_MODIFIER = new Modifier(Type.EMPTY, -10, 0, 0);

	private static final Predicate<ItemStack> VALID_INGREDIENTS = stack ->
			ITEM_MODIFIER_MAP.containsKey(stack.getItem())
					|| stack.is(Tags.Items.BONES)
					|| stack.is(ModTags.Items.RAW_MEATS)
					|| stack.is(ModTags.Items.COOKED_MEATS);

	private static final Predicate<MobEffect> VALID_STATUS_EFFECTS = effect -> effect == MobEffects.HEAL || effect == MobEffects.REGENERATION;

	private final Random random;
	private float successChance;
	private float diseaseChance;
	private float hostileChance;

	public SacrificeHelper(NonNullList<ItemStack> items, Random random) {
		this.random = random;
		gatherChances(items);
	}

	public static boolean isValidIngredient(ItemStack stack) {
		return VALID_INGREDIENTS.test(stack);
	}

	public static boolean isValidReactant(ItemStack stack) {
		if (!(stack.getItem() instanceof PotionItem)) return false;

		List<MobEffectInstance> effectInstances = PotionUtils.getMobEffects(stack);
		for (MobEffectInstance instance : effectInstances) {
			if (VALID_STATUS_EFFECTS.test(instance.getEffect())) return true;
		}
		return false;
	}

	public static int getSuccessModifier(ItemStack stack) {
		return getModifier(stack).successModifier;
	}

	public static Modifier getModifier(ItemStack stack) {
		if (stack.isEmpty()) return EMPTY_MODIFIER;

		Modifier optionalModifier = ITEM_MODIFIER_MAP.get(stack.getItem());
		if (optionalModifier != null) return optionalModifier;

		if (stack.is(Tags.Items.BONES)) return BONES_MODIFIER;
		if (stack.is(ModTags.Items.RAW_MEATS)) return RAW_MEATS_MODIFIER;
		if (stack.is(ModTags.Items.COOKED_MEATS)) return COOKED_MEATS_MODIFIER;

		return INVALID_ITEM_MODIFIER;
	}

	public boolean isSacrificeSuccessful() {
		return random.nextFloat() < successChance;
	}

	public boolean isFleshBlobHostile() {
		return random.nextFloat() < hostileChance;
	}

	public float getTumorFactor() {
		return diseaseChance;
	}

	private void gatherChances(NonNullList<ItemStack> items) {
		int[] successValues = new int[Type.values().length];
		int diseaseValue = 0;
		int hostileValue = 0;

		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				Modifier modifier = getModifier(stack);
				diseaseValue += modifier.diseaseModifier;
				hostileValue += modifier.hostileModifier;
				successValues[modifier.type.index] += modifier.successModifier;
			}
		}

		diseaseChance = diseaseValue / 100f;
		hostileChance = hostileValue / 100f;
		int sum = 0;
		for (Type type : Type.values()) {
			sum += type.operator.applyAsInt(successValues[type.index]);
		}
		successChance = sum / 100f;
	}

//	public int countUniqueFleshItems(NonNullList<ItemStack> items) {
//		List<ItemStack> uniqueMeats = new ArrayList<>(items.size());
//		for (ItemStack stack : items) {
//			if (!stack.isEmpty() && stack.is(ModTags.Items.RAW_MEATS)) {
//				boolean skip = false;
//				for (ItemStack uniqueMeat : uniqueMeats) {
//					if (ItemHandlerHelper.canItemStacksStack(stack, uniqueMeat)) {
//						skip = true;
//						break;
//					}
//				}
//				if (skip) continue;
//				uniqueMeats.add(stack);
//			}
//		}
//		return uniqueMeats.size();
//	}

	enum Type {
		EMPTY(0, v -> v),
		BONE(1, v -> v < 20 ? (int) ((v / 20f - 1) * 100) : Math.min(v, 30)),
		RAW_MEAT(2, v -> v < 66 ? (int) ((v / 66f - 1) * 100) : Math.min(v, 90));

		private final int index;
		private final IntUnaryOperator operator;

		Type(int index, IntUnaryOperator operator) {
			this.index = index;
			this.operator = operator;
		}
	}

	record Modifier(Type type, byte successModifier, byte diseaseModifier, byte hostileModifier) {
		public Modifier(Type type, int successModifier, int diseaseModifier, int hostileModifier) {
			this(type, (byte) successModifier, (byte) diseaseModifier, (byte) hostileModifier);
		}
	}

}
