package com.github.elenterius.biomancy.util.explosion;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class GenericExplosion extends Explosion {

	public GenericExplosion(Level level, @Nullable Entity source, double x, double y, double z, float radius, List<BlockPos> positions) {
		super(level, source, x, y, z, radius, positions);
	}

	public GenericExplosion(Level level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, BlockInteraction blockInteraction, List<BlockPos> positions) {
		super(level, source, x, y, z, radius, fire, blockInteraction, positions);
	}

	public GenericExplosion(Level level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, BlockInteraction blockInteraction) {
		super(level, source, x, y, z, radius, fire, blockInteraction);
	}

	public GenericExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, BlockInteraction blockInteraction) {
		super(level, source, damageSource, damageCalculator, x, y, z, radius, fire, blockInteraction);
	}

	@Override
	public void explode() {
		level.gameEvent(source, GameEvent.EXPLODE, new Vec3(x, y, z));
		gatherBlocksToBlow();
		explodeEntities();
	}

	protected void gatherBlocksToBlow() {
		Set<BlockPos> set = Sets.newHashSet();
		int range = 15;

		for (int j = 0; j <= range; j++) {
			for (int k = 0; k <= range; k++) {
				for (int l = 0; l <= range; l++) {
					if (j == 0 || j == range || k == 0 || k == range || l == 0 || l == range) {
						double d0 = (float) j / 15f * 2f - 1f;
						double d1 = (float) k / 15f * 2f - 1f;
						double d2 = (float) l / 15f * 2f - 1f;
						double distance = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= distance;
						d1 /= distance;
						d2 /= distance;

						float damage = radius * (0.7f + level.random.nextFloat() * 0.6f);
						double posX = x;
						double posY = y;
						double posZ = z;

						for (double stepScale = 0.3d; damage > 0f; damage -= 0.225f) {
							BlockPos blockpos = BlockPos.containing(posX, posY, posZ);
							BlockState blockstate = level.getBlockState(blockpos);
							FluidState fluidstate = level.getFluidState(blockpos);

							if (!level.isInWorldBounds(blockpos)) {
								break;
							}

							Optional<Float> optional = damageCalculator.getBlockExplosionResistance(this, level, blockpos, blockstate, fluidstate);
							if (optional.isPresent()) {
								damage -= (optional.get() + 0.3f) * 0.3f;
							}

							if (damage > 0f && damageCalculator.shouldBlockExplode(this, level, blockpos, blockstate, damage)) {
								set.add(blockpos);
							}

							posX += d0 * stepScale;
							posY += d1 * stepScale;
							posZ += d2 * stepScale;
						}
					}
				}
			}
		}

		toBlow.addAll(set);
	}

	protected void explodeEntities() {
		double diameter = radius * 2f;
		int minX = Mth.floor(x - diameter - 1d);
		int maxX = Mth.floor(x + diameter + 1d);
		int minY = Mth.floor(y - diameter - 1d);
		int maxY = Mth.floor(y + diameter + 1d);
		int minZ = Mth.floor(z - diameter - 1d);
		int maxZ = Mth.floor(z + diameter + 1d);

		List<Entity> entities = level.getEntities(source, new AABB(minX, minY, minZ, maxX, maxY, maxZ));
		ForgeEventFactory.onExplosionDetonate(level, this, entities, diameter);

		Vec3 position = getPosition();

		for (Entity entity : entities) {
			if (entity.ignoreExplosion()) continue;

			double distanceAwayPercent = Math.sqrt(entity.distanceToSqr(position)) / diameter;
			if (distanceAwayPercent > 1d) continue;

			double seenPercent = getSeenPercent(position, entity);
			double nearPercent = (1d - distanceAwayPercent) * seenPercent;

			hurtEntity(entity, Mth.floor((nearPercent * nearPercent + nearPercent) / 2d * 7d * diameter + 1d));

			double deltaX = entity.getX() - x;
			double deltaY = entity.getY(0.5d) - y;
			double deltaZ = entity.getZ() - z;

			Vec3 direction = new Vec3(deltaX, deltaY, deltaZ).normalize();
			if (direction == Vec3.ZERO) {
				direction = new Vec3(level.random.nextGaussian(), level.random.nextGaussian(), level.random.nextGaussian()).normalize();
			}

			Vec3 knockback = direction.scale(getKnockbackStrength(entity, nearPercent));
			entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));

			if (entity instanceof Player player) {
				if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
					hitPlayers.put(player, knockback);
				}
			}
		}
	}

	protected double getKnockbackStrength(Entity entity, double nearExplosionPercent) {
		if (entity instanceof LivingEntity livingentity) {
			return ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, nearExplosionPercent);
		}

		return nearExplosionPercent;
	}

	protected void hurtEntity(Entity entity, float amount) {
		entity.hurt(getDamageSource(), amount);
	}

}
