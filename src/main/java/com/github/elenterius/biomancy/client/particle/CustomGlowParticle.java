package com.github.elenterius.biomancy.client.particle;

import com.github.elenterius.biomancy.util.colors.ColorHarmony;
import com.github.elenterius.biomancy.util.colors.ColorSpace;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomGlowParticle extends TextureSheetParticle {
	static final RandomSource RANDOM = RandomSource.create();
	private final SpriteSet sprites;

	protected CustomGlowParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);

		this.sprites = sprites;
		quadSize *= 0.75F;

		friction = 0.99f;
		hasPhysics = false;
		setSpriteFromAge(sprites);

		xd *= 0.02f;
		yd *= 0.02f;
		zd *= 0.02f;

		lifetime = 20 + level.getRandom().nextInt(81);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float partialTick) {
		int lightColor = super.getLightColor(partialTick);
		int k = lightColor >> 16 & 255;

		float agePct = Mth.clamp((age + partialTick) / (float) lifetime, 0f, 1f);
		int brightness = (lightColor & 255) + (int) (agePct * 15f * 16f);

		return Math.min(brightness, 240) | k << 16;
	}

	@Override
	public void tick() {
		super.tick();
		setSpriteFromAge(sprites);
	}

	public void setColorRGB(int color) {
		rCol = FastColor.ARGB32.red(color) / 255f;
		gCol = FastColor.ARGB32.green(color) / 255f;
		bCol = FastColor.ARGB32.blue(color) / 255f;
	}

	@OnlyIn(Dist.CLIENT)
	public static class AnalogousColorProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;
		private final int color;

		public AnalogousColorProvider(SpriteSet sprites, int rgbColor) {
			sprite = sprites;
			color = rgbColor;
		}

		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CustomGlowParticle particle = new CustomGlowParticle(level, x, y, z, 0.5d - RANDOM.nextDouble(), ySpeed, 0.5d - RANDOM.nextDouble(), sprite);

			if (level.random.nextFloat() < 0.4f) {
				double[][] colors = ColorHarmony.analogousOkLCh(ColorSpace.OkLCh.fromARGB32(color));
				double[] rgb = ColorSpace.OkLCh.toSRGB(colors[1]);
				particle.setColor((float) rgb[0], (float) rgb[1], (float) rgb[2]);
			}
			else {
				particle.setColorRGB(color);
			}

			return particle;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class TwoColorProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;
		private final int primaryColor;
		private final int secondaryColor;

		public TwoColorProvider(SpriteSet sprites, int primaryColor, int secondaryColor) {
			sprite = sprites;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
		}

		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			CustomGlowParticle particle = new CustomGlowParticle(level, x, y, z, 0.5d - RANDOM.nextDouble(), ySpeed, 0.5d - RANDOM.nextDouble(), sprite);
			particle.setColorRGB(level.random.nextFloat() < 0.4f ? secondaryColor : primaryColor);
			return particle;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class GenericProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprite;

		public GenericProvider(SpriteSet sprites) {
			sprite = sprites;
		}

		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new CustomGlowParticle(level, x, y, z, 0.5d - RANDOM.nextDouble(), ySpeed, 0.5d - RANDOM.nextDouble(), sprite);
		}
	}

}
