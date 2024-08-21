package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import com.github.elenterius.biomancy.enchantment.*;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.item.weapon.ClawsItem;
import com.github.elenterius.biomancy.item.weapon.gun.Gun;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {

	public static final EnchantmentCategory LIVING_CATEGORY = EnchantmentCategory.create("biomancy_living", item -> item instanceof LivingTool);
	public static final EnchantmentCategory SYRINGE_CATEGORY = EnchantmentCategory.create("biomancy_syringe", item -> item instanceof ExtractorItem || item instanceof InjectorItem);
	public static final EnchantmentCategory SURGERY_CATEGORY = EnchantmentCategory.create("biomancy_surgery", item -> item instanceof ExtractorItem);
	public static final EnchantmentCategory GUN_CATEGORY = EnchantmentCategory.create("biomancy_gun", Gun.class::isInstance);
	public static final EnchantmentCategory WEAPON_CATEGORY = EnchantmentCategory.create("biomancy_weapon", item -> EnchantmentCategory.WEAPON.canEnchant(item) || item instanceof ClawsItem || item instanceof AxeItem || item.builtInRegistryHolder().is(ModItemTags.FORGE_TOOLS_KNIVES));

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<DespoilEnchantment> DESPOIL = ENCHANTMENTS.register("despoil", () -> new DespoilEnchantment(Enchantment.Rarity.RARE, WEAPON_CATEGORY, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
	public static final RegistryObject<AnestheticEnchantment> ANESTHETIC = ENCHANTMENTS.register("anesthetic", () -> new AnestheticEnchantment(Enchantment.Rarity.RARE, SYRINGE_CATEGORY, EquipmentSlot.MAINHAND));
	public static final RegistryObject<SurgicalPrecisionEnchantment> SURGICAL_PRECISION = ENCHANTMENTS.register("surgical_precision", () -> new SurgicalPrecisionEnchantment(Enchantment.Rarity.RARE, SURGERY_CATEGORY, EquipmentSlot.MAINHAND));
	public static final RegistryObject<ParasiticMetabolismEnchantment> PARASITIC_METABOLISM = ENCHANTMENTS.register("parasitic_metabolism", () -> new ParasiticMetabolismEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.values()));
	public static final RegistryObject<SelfFeedingEnchantment> SELF_FEEDING = ENCHANTMENTS.register("self_feeding", () -> new SelfFeedingEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.values()));

	private ModEnchantments() {}

	//	public static final RegistryObject<ClimbingEnchantment> CLIMBING = ENCHANTMENTS.register("climbing", () -> new ClimbingEnchantment(Enchantment.Rarity.RARE));
	//	public static final RegistryObject<BulletJumpEnchantment> BULLET_JUMP = ENCHANTMENTS.register("bullet_jump", () -> new BulletJumpEnchantment(Enchantment.Rarity.VERY_RARE));
	//	public static final RegistryObject<AttunedDamageEnchantment> ATTUNED_BANE = ENCHANTMENTS.register("attuned_bane", () -> new AttunedDamageEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
	//	public static final RegistryObject<QuickShotEnchantment> QUICK_SHOT = ENCHANTMENTS.register("quick_shot", () -> new QuickShotEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
	//	public static final RegistryObject<MaxAmmoEnchantment> MAX_AMMO = ENCHANTMENTS.register("max_ammo", () -> new MaxAmmoEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.MAINHAND));
}
