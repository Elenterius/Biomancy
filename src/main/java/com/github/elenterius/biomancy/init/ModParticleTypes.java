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

	private ModParticleTypes() {}

	private static RegistryObject<SimpleParticleType> register(String name, boolean overrideLimiter) {
		return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(overrideLimiter));
	}

}
