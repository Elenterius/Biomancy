package com.github.elenterius.biomancy.client.particle;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BloodDripParticle extends TextureSheetParticle {

	protected BloodDripParticle(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z);
		setSize(0.01f, 0.01f);
		setColorRGB(ModMobEffects.BLEED.get().getColor());
	}

	public void setColorRGB(int color) {
		rCol = FastColor.ARGB32.red(color) / 255f;
		gCol = FastColor.ARGB32.green(color) / 255f;
		bCol = FastColor.ARGB32.blue(color) / 255f;
	}

	public void setColorARGB(int color) {
		rCol = FastColor.ARGB32.red(color) / 255f;
		gCol = FastColor.ARGB32.green(color) / 255f;
		bCol = FastColor.ARGB32.blue(color) / 255f;
		alpha = FastColor.ARGB32.alpha(color) / 255f;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;

		if (lifetime-- <= 0) {
			remove();
			return;
		}
		if (removed) return;

		yd -= gravity;
		move(xd, yd, zd);

		postMoveUpdate();

		if (!removed) {
			xd *= 0.98f;
			yd *= 0.98f;
			zd *= 0.98f;
		}
	}

	protected abstract void postMoveUpdate();

	static class FallingBloodParticle extends BloodDripParticle {
		protected final ParticleOptions landingParticle;

		protected FallingBloodParticle(ClientLevel level, double x, double y, double z, ParticleOptions landingParticle) {
			super(level, x, y, z);
			this.landingParticle = landingParticle;
			gravity = 0.01f;
		}

		@Override
		protected void postMoveUpdate() {
			if (onGround) {
				remove();
				level.addParticle(landingParticle, x, y, z, 0, 0, 0);
				float volume = Mth.randomBetween(random, 0.3f, 1);
				level.playLocalSound(x, y, z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, volume, 1, false);
			}
		}
	}

	static class LandingBloodParticle extends BloodDripParticle {

		protected LandingBloodParticle(ClientLevel level, double x, double y, double z) {
			super(level, x, y, z);
			gravity = 0.06f;
		}

		@Override
		protected void postMoveUpdate() {
			//do nothing
		}
	}

	public static class FallingBloodFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet sprite;

		public FallingBloodFactory(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			FallingBloodParticle particle = new FallingBloodParticle(level, x, y, z, ModParticleTypes.LANDING_BLOOD.get());
			particle.lifetime = (int) (64f / (level.getRandom().nextFloat() * 0.8f + 0.2f));
			particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
			particle.pickSprite(sprite);
			return particle;
		}
	}

	public static class LandingBloodFactory implements ParticleProvider<SimpleParticleType> {
		protected final SpriteSet sprite;

		public LandingBloodFactory(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			LandingBloodParticle particle = new LandingBloodParticle(level, x, y, z);
			particle.lifetime = (int) (128d / (level.getRandom().nextFloat() * 0.8d + 0.2d));
			particle.pickSprite(sprite);
			return particle;
		}
	}

}
