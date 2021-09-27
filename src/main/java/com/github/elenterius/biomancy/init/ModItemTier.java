package com.github.elenterius.biomancy.init;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public enum ModItemTier implements IItemTier {
	BONE(1, 131, 4f, 1f, 5, () -> {
		return Ingredient.of(Items.BONE_MEAL, ModItems.BONE_SCRAPS.get());
	}),
	LESSER_BIOMETAL(3, 1650, 6f, 2f, 14, () -> {
		return Ingredient.of(ModItems.FLESH_BLOCK.get());
	}),
	BIOMETAL(4, 2031, 9f, 4f, 15, () -> {
		return Ingredient.of(ModItems.BIOMETAL.get());
	});

	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final LazyValue<Ingredient> repairMaterial;

	ModItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairMaterial = new LazyValue<>(repairMaterial);
	}

	@Override
	public int getUses() {
		return this.maxUses;
	}

	@Override
	public float getSpeed() {
		return this.efficiency;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.attackDamage;
	}

	@Override
	public int getLevel() {
		return this.harvestLevel;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairMaterial.get();
	}
}
