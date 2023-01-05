package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.enchantment.AnestheticEnchantment;
import com.github.elenterius.biomancy.world.enchantment.DespoilEnchantment;
import com.github.elenterius.biomancy.world.enchantment.MaxAmmoEnchantment;
import com.github.elenterius.biomancy.world.enchantment.QuickShotEnchantment;
import com.github.elenterius.biomancy.world.item.BioExtractorItem;
import com.github.elenterius.biomancy.world.item.weapon.ClawsItem;
import com.github.elenterius.biomancy.world.item.weapon.IGun;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {

	public static final EnchantmentCategory SYRINGE_CATEGORY = EnchantmentCategory.create("biomancy_syringe", BioExtractorItem.class::isInstance);
	public static final EnchantmentCategory GUN_CATEGORY = EnchantmentCategory.create("biomancy_gun", IGun.class::isInstance);
	public static final EnchantmentCategory WEAPON_CATEGORY = EnchantmentCategory.create("biomancy_weapon", item -> EnchantmentCategory.WEAPON.canEnchant(item) || item instanceof ClawsItem || item instanceof AxeItem);

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<DespoilEnchantment> DESPOIL = ENCHANTMENTS.register("despoil", () -> new DespoilEnchantment(Enchantment.Rarity.RARE, WEAPON_CATEGORY, EquipmentSlot.MAINHAND));
	public static final RegistryObject<AnestheticEnchantment> ANESTHETIC = ENCHANTMENTS.register("anesthetic", () -> new AnestheticEnchantment(Enchantment.Rarity.RARE, SYRINGE_CATEGORY, EquipmentSlot.MAINHAND));
	public static final RegistryObject<QuickShotEnchantment> QUICK_SHOT = ENCHANTMENTS.register("quick_shot", () -> new QuickShotEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
	public static final RegistryObject<MaxAmmoEnchantment> MAX_AMMO = ENCHANTMENTS.register("max_ammo", () -> new MaxAmmoEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));

	private ModEnchantments() {}

	//	public static final RegistryObject<ClimbingEnchantment> CLIMBING = ENCHANTMENTS.register("climbing", () -> new ClimbingEnchantment(Enchantment.Rarity.RARE));
	//	public static final RegistryObject<BulletJumpEnchantment> BULLET_JUMP = ENCHANTMENTS.register("bullet_jump", () -> new BulletJumpEnchantment(Enchantment.Rarity.VERY_RARE));
	//	public static final RegistryObject<AttunedDamageEnchantment> ATTUNED_BANE = ENCHANTMENTS.register("attuned_bane", () -> new AttunedDamageEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));

}
