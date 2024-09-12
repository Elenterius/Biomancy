package com.github.elenterius.biomancy.datagen.particles;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModParticleSpriteProvider extends ParticleSpriteProvider {

	public ModParticleSpriteProvider(PackOutput packOutput, ExistingFileHelper fileHelper) {
		super(packOutput, BiomancyMod.MOD_ID, fileHelper);
	}

	@Override
	public void registerParticles() {
		addParticle(ModParticleTypes.BLOODY_CLAWS_ATTACK, 8, 1);
		addParticle(ModParticleTypes.FALLING_BLOOD, "minecraft:drip_fall");
		addParticle(ModParticleTypes.LANDING_BLOOD, "minecraft:drip_land");
		addParticle(ModParticleTypes.CORROSIVE_SWIPE_ATTACK, 8, 1);
		addParticle(ModParticleTypes.DRIPPING_ACID, "minecraft:drip_hang");
		addParticle(ModParticleTypes.FALLING_ACID, "minecraft:drip_fall");
		addParticle(ModParticleTypes.LANDING_ACID, "minecraft:drip_land");
		addParticle(ModParticleTypes.PINK_GLOW, "minecraft:glow");
		addParticle(ModParticleTypes.LIGHT_GREEN_GLOW, "minecraft:glow");
		addParticle(ModParticleTypes.HOSTILE, "biomancy:hostile");
		addParticle(ModParticleTypes.BIOHAZARD, "biomancy:biohazard");
	}

}
