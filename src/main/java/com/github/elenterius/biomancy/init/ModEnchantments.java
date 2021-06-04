package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.biomancy.enchantment.BulletJumpEnchantment;
import com.github.elenterius.biomancy.enchantment.ClimbingEnchantment;
import com.github.elenterius.biomancy.enchantment.QuickShotEnchantment;
import com.github.elenterius.biomancy.item.weapon.shootable.ProjectileWeaponItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModEnchantments {
	private ModEnchantments() {}

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<ClimbingEnchantment> CLIMBING = ENCHANTMENTS.register("climbing", () -> new ClimbingEnchantment(Enchantment.Rarity.RARE));
	public static final RegistryObject<BulletJumpEnchantment> BULLET_JUMP = ENCHANTMENTS.register("bullet_jump", () -> new BulletJumpEnchantment(Enchantment.Rarity.RARE));
	public static final RegistryObject<AttunedDamageEnchantment> ATTUNED_BANE = ENCHANTMENTS.register("attuned_bane", () -> new AttunedDamageEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
	public static final RegistryObject<QuickShotEnchantment> QUICK_SHOT = ENCHANTMENTS.register("quick_shot", () -> new QuickShotEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));

	public static final EnchantmentType PROJECTILE_WEAPON_TYPE = EnchantmentType.create("biomancy_projectile_weapon", item -> item instanceof ProjectileWeaponItem);

}
