package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.LevelReader;

public final class ModDamageTypes {

	public static final ResourceKey<DamageType> TOOTH_PROJECTILE = key("tooth_projectile");
	public static final ResourceKey<DamageType> PRIMORDIAL_SPIKES = key("primordial_spikes");
	public static final ResourceKey<DamageType> CHEST_BITE = key("chest_bite");
	public static final ResourceKey<DamageType> CORROSIVE_ACID = key("corrosive_acid");
	public static final ResourceKey<DamageType> BLEED = key("bleed");
	public static final ResourceKey<DamageType> SLASH = key("slash");
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
		bootstrap(ctx, TOOTH_PROJECTILE);
		bootstrap(ctx, PRIMORDIAL_SPIKES, DamageScaling.ALWAYS, 0);
		bootstrap(ctx, CHEST_BITE, DamageScaling.ALWAYS, 0.25f);
		bootstrap(ctx, CORROSIVE_ACID, 0.1f);
		bootstrap(ctx, BLEED, 0.25f);
		bootstrap(ctx, SLASH, 0.25f);
		bootstrap(ctx, FALL_ON_SPIKE);
		bootstrap(ctx, IMPALED_BY_SPIKE);
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), 0));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, float exhaustion) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), exhaustion));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, float exhaustion, DamageEffects effects) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), exhaustion, effects));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), scaling, exhaustion));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, DamageScaling scaling, float exhaustion, DamageEffects effects) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), scaling, exhaustion, effects));
	}
}
