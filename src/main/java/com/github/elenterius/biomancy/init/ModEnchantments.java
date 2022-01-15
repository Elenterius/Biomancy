package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.enchantment.DespoilEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BiomancyMod.MOD_ID);

	private ModEnchantments() {}

	public static final RegistryObject<DespoilEnchantment> DESPOIL = ENCHANTMENTS.register("despoil", () -> new DespoilEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));

//	public static final RegistryObject<ClimbingEnchantment> CLIMBING = ENCHANTMENTS.register("climbing", () -> new ClimbingEnchantment(Enchantment.Rarity.RARE));
//	public static final RegistryObject<BulletJumpEnchantment> BULLET_JUMP = ENCHANTMENTS.register("bullet_jump", () -> new BulletJumpEnchantment(Enchantment.Rarity.VERY_RARE));
//	public static final RegistryObject<AttunedDamageEnchantment> ATTUNED_BANE = ENCHANTMENTS.register("attuned_bane", () -> new AttunedDamageEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
//	public static final RegistryObject<QuickShotEnchantment> QUICK_SHOT = ENCHANTMENTS.register("quick_shot", () -> new QuickShotEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
//	public static final RegistryObject<MaxAmmoEnchantment> MAX_AMMO = ENCHANTMENTS.register("max_ammo", () -> new MaxAmmoEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
//
//	public static final EnchantmentType PROJECTILE_WEAPON_TYPE = EnchantmentType.create("biomancy_projectile_weapon", item -> item instanceof ProjectileWeaponItem);

}
