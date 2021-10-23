package com.github.elenterius.biomancy.entity.ai.goal;

import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.statuseffect.StatusEffect;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class CompelledWalkGoal extends Goal {

	protected final CreatureEntity creature;
	private final double speedModifier;
	private double x;
	private double y;
	private double z;
	private CompulsionType compulsionType;

	protected enum CompulsionType {
		NONE(null), ATTRACTION(ModEffects.ATTRACTED.get()), REPULSION(ModEffects.REPULSED.get());

		private final StatusEffect effect;

		CompulsionType(StatusEffect effect) {this.effect = effect;}

		public boolean isMobEffectActive(CreatureEntity creatureIn) {
			if (this == NONE) return false;
			return creatureIn.hasEffect(effect);
		}

		public void removeEffect(CreatureEntity creatureIn) {
			if (this != NONE && creatureIn.hasEffect(effect))
				creatureIn.removeEffect(effect);
		}
	}

	public CompelledWalkGoal(CreatureEntity creature, double speedModifier) {
		this.creature = creature;
		this.speedModifier = speedModifier;
		compulsionType = CompulsionType.NONE;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return CompulsionType.ATTRACTION.isMobEffectActive(creature) || CompulsionType.REPULSION.isMobEffectActive(creature);
	}

	@Override
	public boolean canContinueToUse() {
		if (!canUse()) return false;
		double distSqr = creature.distanceToSqr(x, y, z);
		return distSqr >= 9d && distSqr < 4096d; // 64*64
	}

	@Override
	public void start() {
		boolean isAttracted = CompulsionType.ATTRACTION.isMobEffectActive(creature);
		boolean isRepulsed = CompulsionType.REPULSION.isMobEffectActive(creature);

		BlockPos pos = creature.getNavigation().getTargetPos();
		if (pos != null) {
			if (compulsionType == CompulsionType.NONE) {
				x = pos.getX() + 0.5d;
				y = pos.getY() + 0.5d;
				z = pos.getZ() + 0.5d;
				determineCompulsionType(isAttracted, isRepulsed);
			}
		}
		else {
			CompulsionType.ATTRACTION.removeEffect(creature);
			CompulsionType.REPULSION.removeEffect(creature);
		}
	}

	private void determineCompulsionType(boolean isAttracted, boolean isRepulsed) {
		if (isAttracted && isRepulsed && creature.getRandom().nextFloat() > 0.5f) {
			isAttracted = false;
		}

		if (isAttracted) {
			compulsionType = CompulsionType.ATTRACTION;
		}
		else {
			compulsionType = CompulsionType.REPULSION;
			Vector3d pos = getAvoidPosition(new Vector3d(x, y, z));
			x = pos.x;
			y = pos.y;
			z = pos.z;
		}
	}

	@Override
	public void stop() {
		compulsionType.removeEffect(creature);
		compulsionType = CompulsionType.NONE;
		creature.getNavigation().stop();
	}

	@Override
	public void tick() {
		if (creature.distanceToSqr(x, y, z) < 9d) {
			creature.getNavigation().stop();
		}
		else {
			creature.getNavigation().moveTo(x, y, z, speedModifier);
		}
	}

	private Vector3d getAvoidPosition(Vector3d avoidPos) {
		EffectInstance effect = creature.getEffect(ModEffects.REPULSED.get());
		int xzRange = Math.max(effect != null ? effect.getAmplifier() : 8, 8);
		Vector3d dist = creature.position().subtract(avoidPos);
		return avoidPos.add(dist.normalize().multiply(xzRange - dist.x, 0, xzRange - dist.z));
	}

}
