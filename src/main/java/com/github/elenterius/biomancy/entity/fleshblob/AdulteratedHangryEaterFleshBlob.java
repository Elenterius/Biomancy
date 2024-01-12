package com.github.elenterius.biomancy.entity.fleshblob;

import com.github.elenterius.biomancy.entity.AdulteratedFleshkin;
import com.github.elenterius.biomancy.entity.MobUtil;
import com.github.elenterius.biomancy.entity.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.ai.goal.BurningOrFreezingPanicGoal;
import com.github.elenterius.biomancy.entity.ai.goal.EatFoodItemGoal;
import com.github.elenterius.biomancy.entity.ai.goal.FindItemGoal;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
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

public class AdulteratedHangryEaterFleshBlob extends EaterFleshBlob implements Enemy, AdulteratedFleshkin {

	public static final float BASE_MAX_HEALTH = 10;
	public static final float BASE_ARMOR = 1.25f;
	public static final float BASE_ATTACK_DAMAGE = 2f;

	public AdulteratedHangryEaterFleshBlob(EntityType<? extends AdulteratedHangryEaterFleshBlob> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, BASE_ARMOR)
				.add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE);
	}

	@Override
	protected void updateBaseAttributes(byte size) {
		MobUtil.setAttributeBaseValue(this, Attributes.MAX_HEALTH, size * BASE_MAX_HEALTH);
		MobUtil.setAttributeBaseValue(this, Attributes.MOVEMENT_SPEED, 0.2f + 0.01f * size);
		MobUtil.setAttributeBaseValue(this, Attributes.ARMOR, size * BASE_ARMOR);
		MobUtil.setAttributeBaseValue(this, Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE * (size * 0.5f));
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new BurningOrFreezingPanicGoal(this, 1.5f));
		goalSelector.addGoal(3, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
		goalSelector.addGoal(3, new EatFoodItemGoal<>(this, 0.1f));
		goalSelector.addGoal(4, new FleshBlobAttackGoal(this, 1.2f));
		goalSelector.addGoal(5, new AvoidEntityGoal<>(this, AbstractGolem.class, 6f, 1f, 1.2f));
		//		goalSelector.addGoal(6, new DanceNearJukeboxGoal<>(this));
		goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1f));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FleshBlob.class, false, PrimordialFleshkin.class::isInstance));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, false));
		targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

}
