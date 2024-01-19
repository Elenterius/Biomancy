package com.github.elenterius.biomancy.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Almost exact copy of vanilla code because DripParticle class can't be extended
 */
@OnlyIn(Dist.CLIENT)
public class VanillaDripParticle extends TextureSheetParticle {

	protected final Fluid type;
	private boolean isGlowing;

	protected VanillaDripParticle(ClientLevel level, double x, double y, double z, Fluid fluid) {
		super(level, x, y, z);
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.06F;
		this.type = fluid;
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	protected void setGlowing(boolean glowing) {
		isGlowing = glowing;
	}

	@Override
	public int getLightColor(float partialTick) {
		return isGlowing ? 0xf0 : super.getLightColor(partialTick);
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;

		preMoveUpdate();

		if (!removed) {
			yd -= gravity;
			move(xd, yd, zd);

			postMoveUpdate();

			if (!removed) {
				xd *= 0.9800000190734863;
				yd *= 0.9800000190734863;
				zd *= 0.9800000190734863;
				BlockPos blockPos = BlockPos.containing(x, y, z);
				FluidState fluidState = level.getFluidState(blockPos);
				if (fluidState.getType() == type && y < (blockPos.getY() + fluidState.getHeight(level, blockPos))) {
					remove();
				}
			}
		}
	}

	protected void preMoveUpdate() {
		if (lifetime-- <= 0) {
			remove();
		}
	}

	protected void postMoveUpdate() { /* placeholder */ }

	protected void setColorRGB(int color) {
		rCol = FastColor.ARGB32.red(color) / 255f;
		gCol = FastColor.ARGB32.green(color) / 255f;
		bCol = FastColor.ARGB32.blue(color) / 255f;
	}

	protected void setColorARGB(int color) {
		rCol = FastColor.ARGB32.red(color) / 255f;
		gCol = FastColor.ARGB32.green(color) / 255f;
		bCol = FastColor.ARGB32.blue(color) / 255f;
		alpha = FastColor.ARGB32.alpha(color) / 255f;
	}

	@OnlyIn(Dist.CLIENT)
	protected static class DripLandParticle extends VanillaDripParticle {
		protected DripLandParticle(ClientLevel level, double x, double y, double z, Fluid fluid) {
			super(level, x, y, z, fluid);
			this.lifetime = (int) (16.0f / (Math.random() * 0.8f + 0.2f));
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected static class DripHangParticle extends VanillaDripParticle {
		protected final ParticleOptions fallingParticle;

		protected DripHangParticle(ClientLevel level, double x, double y, double z, Fluid fluid, ParticleOptions fallingParticle) {
			super(level, x, y, z, fluid);
			this.fallingParticle = fallingParticle;
			this.gravity *= 0.02F;
			this.lifetime = 40;
		}

		@Override
		protected void preMoveUpdate() {
			if (lifetime-- <= 0) {
				remove();
				level.addParticle(fallingParticle, x, y, z, xd, yd, zd);
			}
		}

		@Override
		protected void postMoveUpdate() {
			xd *= 0.02;
			yd *= 0.02;
			zd *= 0.02;
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected static class FallingParticle extends VanillaDripParticle {
		protected FallingParticle(ClientLevel level, double x, double y, double z, Fluid fluid) {
			this(level, x, y, z, fluid, (int) (64.0f / (Math.random() * 0.8f + 0.2f)));
		}

		protected FallingParticle(ClientLevel level, double x, double y, double z, Fluid fluid, int lifetime) {
			super(level, x, y, z, fluid);
			this.lifetime = lifetime;
		}

		@Override
		protected void postMoveUpdate() {
			if (onGround) {
				remove();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected static class FallAndLandParticle extends FallingParticle {
		protected final ParticleOptions landParticle;

		protected FallAndLandParticle(ClientLevel level, double x, double y, double z, Fluid fluid, ParticleOptions landParticle) {
			super(level, x, y, z, fluid);
			this.landParticle = landParticle;
		}

		@Override
		protected void postMoveUpdate() {
			if (onGround) {
				remove();
				level.addParticle(landParticle, x, y, z, 0, 0, 0);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected static class AcidFallAndLandParticle extends FallAndLandParticle {
		protected AcidFallAndLandParticle(ClientLevel level, double x, double y, double z, Fluid fluid, ParticleOptions landParticle) {
			super(level, x, y, z, fluid, landParticle);
		}

		@Override
		protected void postMoveUpdate() {
			if (onGround) {
				remove();
				level.addParticle(landParticle, x, y, z, 0, 0, 0);

				if (random.nextInt(4) == 0) {
					float volume = Mth.randomBetween(random, 0.4f, 0.6f);
					float pitch = Mth.randomBetween(random, 1.8f, 3.4f);
					level.playLocalSound(x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, volume, pitch, false);
					for (int i = 0; i < 4; i++) {
						level.addParticle(ParticleTypes.LARGE_SMOKE, x + random.nextDouble(), y + 0.2f, z + random.nextDouble(), 0, 0, 0);
					}
				}
			}
		}
	}

}
