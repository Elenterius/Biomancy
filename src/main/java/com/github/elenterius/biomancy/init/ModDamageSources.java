package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.projectile.ToothProjectile;
import com.github.elenterius.biomancy.world.entity.projectile.WitherProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public final class ModDamageSources {

	public static final DamageSource DISEASE = createGenericDamage("disease").bypassArmor().bypassMagic();
	public static final DamageSource RAVENOUS_HUNGER = createGenericDamage("ravenous_hunger").bypassArmor().bypassMagic();

	public static final DamageSource CREATOR_SPIKES = createGenericDamage("creator_spikes").bypassArmor();
	public static final DamageSource CHEST_BITE = createGenericDamage("chest_bite").bypassArmor();

	public static final DamageSource SYMBIONT_EAT = createGenericDamage("symbiont_eat").bypassArmor().bypassMagic();
	public static final DamageSource SYMBIONT_BITE = createGenericDamage("symbiont_bite").bypassArmor();
	public static final DamageSource SYMBIONT_GENERIC_ATTACK = createGenericDamage("symbiont_generic");

	private ModDamageSources() {}

	public static DamageSource createGenericDamage(String name) {
		return new DamageSource(BiomancyMod.MOD_ID + "." + name);
	}

	public static DamageSource createToothProjectileDamage(ToothProjectile projectile, @Nullable Entity shooter) {
		return new IndirectEntityDamageSource(BiomancyMod.MOD_ID + "." + "tooth_projectile", projectile, shooter).setProjectile();
	}

	public static DamageSource createWitherSkullDamage(WitherProjectile projectile, @Nullable Entity shooter) {
		return new IndirectEntityDamageSource("witherSkull", projectile, shooter).setProjectile();
	}

}
