package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticleTypes {

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BiomancyMod.MOD_ID);
	public static final RegistryObject<SimpleParticleType> BLOODY_CLAWS_ATTACK = register("bloody_claws_attack", true);
	public static final RegistryObject<SimpleParticleType> FALLING_BLOOD = register("falling_blood", false);
	public static final RegistryObject<SimpleParticleType> LANDING_BLOOD = register("landing_blood", false);
	public static final RegistryObject<SimpleParticleType> CORROSIVE_SWIPE_ATTACK = register("corrosive_swipe", true);
	public static final RegistryObject<SimpleParticleType> DRIPPING_ACID = register("dripping_acid", false);
	public static final RegistryObject<SimpleParticleType> FALLING_ACID = register("falling_acid", false);
	public static final RegistryObject<SimpleParticleType> LANDING_ACID = register("landing_acid", false);
	public static final RegistryObject<SimpleParticleType> PINK_GLOW = register("pink_glow", false);
	public static final RegistryObject<SimpleParticleType> LIGHT_GREEN_GLOW = register("light_green_glow", false);
	public static final RegistryObject<SimpleParticleType> HOSTILE = register("hostile", false);
	public static final RegistryObject<SimpleParticleType> BIOHAZARD = register("biohazard", false);

	private ModParticleTypes() {}

	private static RegistryObject<SimpleParticleType> register(String name, boolean overrideLimiter) {
		return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(overrideLimiter));
	}

}
