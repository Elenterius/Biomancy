package com.github.elenterius.biomancy.client.particle;

import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public final class ParticleProviders {

	private ParticleProviders() {}

	@OnlyIn(Dist.CLIENT)
	public static class AcidLandProvider implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet sprite;

		public AcidLandProvider(SpriteSet sprites) {
			sprite = sprites;
		}

		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			VanillaDripParticle particle = new VanillaDripParticle.DripLandParticle(level, x, y, z, ModFluids.ACID.get());
			particle.setColorRGB(ModMobEffects.CORROSIVE.get().getColor());
			particle.pickSprite(sprite);
			return particle;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class AcidHangProvider implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet sprite;

		public AcidHangProvider(SpriteSet sprites) {
			sprite = sprites;
		}

		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			VanillaDripParticle particle = new VanillaDripParticle.DripHangParticle(level, x, y, z, ModFluids.ACID.get(), ModParticleTypes.FALLING_ACID.get());
			particle.setColorRGB(ModMobEffects.CORROSIVE.get().getColor());
			particle.pickSprite(sprite);
			return particle;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class AcidFallProvider implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet sprite;

		public AcidFallProvider(SpriteSet sprites) {
			sprite = sprites;
		}

		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			VanillaDripParticle particle = new VanillaDripParticle.AcidFallAndLandParticle(level, x, y, z, ModFluids.ACID.get(), ModParticleTypes.LANDING_ACID.get());
			particle.setColorRGB(ModMobEffects.CORROSIVE.get().getColor());
			particle.pickSprite(sprite);
			return particle;
		}
	}

}
