package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.damagesource.ModEntityDamageSource;
import com.github.elenterius.biomancy.entity.projectile.AbstractProjectileEntity;
import com.github.elenterius.biomancy.entity.projectile.ToothProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;

public final class ModDamageSources {

	public static final DamageSource DISEASE = createGenericDamage("disease").bypassArmor().bypassMagic();
	public static final DamageSource RAVENOUS_HUNGER = createGenericDamage("ravenous_hunger").bypassArmor().bypassMagic();

	public static final DamageSource SYMBIONT_EAT = createGenericDamage("symbiont_eat").bypassArmor().bypassMagic();
	public static final DamageSource SYMBIONT_BITE = createGenericDamage("symbiont_bite").bypassArmor();
	public static final DamageSource SYMBIONT_GENERIC_ATTACK = createGenericDamage("symbiont_generic");

	private ModDamageSources() {}

	public static DamageSource createGenericDamage(String name) {
		return new DamageSource(BiomancyMod.MOD_ID + "." + name);
	}

	public static ModEntityDamageSource createBlightThornDamage(String damageCause, LivingEntity attacker) {
		ModEntityDamageSource damageSource = new ModEntityDamageSource(damageCause, "blight_thorn", attacker, (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
		damageSource.setThorns().setMagic();
		return damageSource;
	}

	public static DamageSource createToothProjectileDamage(ToothProjectileEntity projectile, @Nullable Entity shooter) {
		return new IndirectEntityDamageSource(BiomancyMod.MOD_ID + "." + "tooth_projectile", projectile, shooter).setProjectile();
	}

	public static DamageSource createWitherSkullDamage(AbstractProjectileEntity projectile, Entity shooter) {
		return new IndirectEntityDamageSource("witherSkull", projectile, shooter).setProjectile();
	}
}
