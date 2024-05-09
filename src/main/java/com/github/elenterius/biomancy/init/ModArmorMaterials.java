package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.common.base.Suppliers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
	ACOLYTE(BiomancyMod.createRLString("acolyte"),
			1,
			new int[]{2, 6, 5, 2},
			0.25f,
			0.1f,
			0,
			() -> Ingredient.EMPTY,
			() -> SoundEvents.ARMOR_EQUIP_TURTLE
	),
	OVERSEER(BiomancyMod.createRLString("overseer"),
			1,
			new int[]{2, 6, 5, 2},
			0.25f,
			0,
			0,
			() -> Ingredient.EMPTY,
			() -> SoundEvents.ARMOR_EQUIP_TURTLE
	);

	private static final int[] BASE_DURABILITY = new int[]{11, 16, 15, 13};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] defense;
	private final float toughness;
	private final float knockbackResistance;
	private final int enchantability;
	private final Supplier<Ingredient> repairMaterial;
	private final Supplier<SoundEvent> equipSound;

	ModArmorMaterials(String name, int durabilityMultiplier, int[] defense, float toughness, float knockbackResistance, int enchantability, Supplier<Ingredient> repairMaterial, Supplier<SoundEvent> equipSound) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.defense = defense;
		this.enchantability = enchantability;
		this.equipSound = equipSound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairMaterial = Suppliers.memoize(repairMaterial::get);
	}

	@Override
	public int getEnchantmentValue() {
		return enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return equipSound.get();
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repairMaterial.get();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getToughness() {
		return toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type type) {
		return BASE_DURABILITY[type.ordinal()] * durabilityMultiplier;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return defense[type.ordinal()];
	}

}
