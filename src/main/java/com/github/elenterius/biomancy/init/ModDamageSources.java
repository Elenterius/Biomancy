package com.github.elenterius.biomancy.init;

import net.minecraft.util.DamageSource;

public final class ModDamageSources {
	private ModDamageSources() {}

	public static final DamageSource SYMBIONT_EAT = new DamageSource("symbiont_eat").setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource SYMBIONT_BITE = new DamageSource("symbiont_bite").setDamageBypassesArmor();
	public static final DamageSource SYMBIONT_MAGIC_ATTACK = new DamageSource("symbiont_magic").setDamageBypassesArmor().setMagicDamage();
	public static final DamageSource SYMBIONT_GENERIC_ATTACK = new DamageSource("symbiont_generic");
}
