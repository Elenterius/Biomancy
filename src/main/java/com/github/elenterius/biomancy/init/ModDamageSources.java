package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.damagesource.ModEntityDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;

public final class ModDamageSources {

	public static final DamageSource DISEASE = createDamageSource("disease").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource RAVENOUS_HUNGER = createDamageSource("ravenous_hunger").setDamageBypassesArmor().setDamageIsAbsolute();

	public static final DamageSource SYMBIONT_EAT = createDamageSource("symbiont_eat").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource SYMBIONT_BITE = createDamageSource("symbiont_bite").setDamageBypassesArmor();
	public static final DamageSource SYMBIONT_GENERIC_ATTACK = createDamageSource("symbiont_generic");

	private ModDamageSources() {}

	public static DamageSource createDamageSource(String name) {
		return new DamageSource(BiomancyMod.MOD_ID + "." + name);
	}

	public static ModEntityDamageSource createBlightThornDamage(String damageCause, LivingEntity attacker) {
		ModEntityDamageSource damageSource = new ModEntityDamageSource(damageCause, "blight_thorn", attacker, (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
		damageSource.setIsThornsDamage().setMagicDamage();
		return damageSource;
	}

}
