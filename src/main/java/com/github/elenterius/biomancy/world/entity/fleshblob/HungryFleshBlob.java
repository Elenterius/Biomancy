package com.github.elenterius.biomancy.world.entity.fleshblob;

import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.entity.ai.goal.BurningOrFreezingPanicGoal;
import com.github.elenterius.biomancy.world.entity.ai.goal.DanceNearJukeboxGoal;
import com.github.elenterius.biomancy.world.entity.ai.goal.EatFoodItemGoal;
import com.github.elenterius.biomancy.world.entity.ai.goal.FindItemGoal;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HungryFleshBlob extends FleshBlob implements Enemy {

	public HungryFleshBlob(EntityType<? extends HungryFleshBlob> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, 1.5f)
				.add(Attributes.ATTACK_DAMAGE, 6);
	}

	@Override
	protected void updateBaseAttributes(byte size) {
		MobUtil.setAttributeBaseValue(this, Attributes.MAX_HEALTH, size * 10f);
		MobUtil.setAttributeBaseValue(this, Attributes.MOVEMENT_SPEED, 0.2f + 0.01f * size);
		MobUtil.setAttributeBaseValue(this, Attributes.ARMOR, size * 1.5f);
		MobUtil.setAttributeBaseValue(this, Attributes.ATTACK_DAMAGE, Math.max(6, size));
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new BurningOrFreezingPanicGoal(this, 1.5f));
		goalSelector.addGoal(3, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
		goalSelector.addGoal(3, new EatFoodItemGoal<>(this, 0.1f));
		goalSelector.addGoal(4, new CustomAttackGoal(this, 1.2f));
		goalSelector.addGoal(5, new AvoidEntityGoal<>(this, AbstractGolem.class, 6f, 1f, 1.2f));
		goalSelector.addGoal(6, new DanceNearJukeboxGoal<>(this));
		goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1f));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MalignantFleshBlob.class, false));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, false));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	static class CustomAttackGoal extends MeleeAttackGoal {

		public CustomAttackGoal(HungryFleshBlob mob, double speed) {
			super(mob, speed, true);
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return 2f + attackTarget.getBbWidth();
		}
	}

}
