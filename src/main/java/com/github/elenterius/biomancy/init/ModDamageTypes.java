package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.LevelReader;

public final class ModDamageTypes {

	public static final ResourceKey<DamageType> TOOTH_PROJECTILE = key("tooth_projectile");
	public static final ResourceKey<DamageType> PRIMORDIAL_SPIKES = key("primordial_spikes");
	public static final ResourceKey<DamageType> CHEST_BITE = key("chest_bite");
	public static final ResourceKey<DamageType> CORROSIVE_ACID = key("corrosive_acid");
	public static final ResourceKey<DamageType> BLEED = key("bleed");
	public static final ResourceKey<DamageType> FALL_ON_SPIKE = key("spike_fall_on");
	public static final ResourceKey<DamageType> IMPALED_BY_SPIKE = key("spike_impale");

	private ModDamageTypes() {}

	private static ResourceKey<DamageType> key(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, BiomancyMod.createRL(name));
	}

	public static Holder.Reference<DamageType> getHolder(ResourceKey<DamageType> key, LevelReader level) {
		return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
	}

	public static void bootstrap(BootstapContext<DamageType> ctx) {
		new DamageTypeBuilder(TOOTH_PROJECTILE).register(ctx);
		new DamageTypeBuilder(PRIMORDIAL_SPIKES).scaling(DamageScaling.ALWAYS).register(ctx);
		new DamageTypeBuilder(CHEST_BITE).scaling(DamageScaling.ALWAYS).exhaustion(0.25f).register(ctx);
		new DamageTypeBuilder(CORROSIVE_ACID).exhaustion(0.1f).register(ctx);
		new DamageTypeBuilder(BLEED).exhaustion(0.25f).register(ctx);
		new DamageTypeBuilder(FALL_ON_SPIKE).register(ctx);
		new DamageTypeBuilder(IMPALED_BY_SPIKE).register(ctx);
	}

}
