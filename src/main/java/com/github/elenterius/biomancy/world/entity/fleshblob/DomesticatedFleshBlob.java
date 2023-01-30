package com.github.elenterius.biomancy.world.entity.fleshblob;

import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.entity.ai.goal.BurningOrFreezingPanicGoal;
import com.github.elenterius.biomancy.world.entity.ai.goal.EatFoodItemGoal;
import com.github.elenterius.biomancy.world.entity.ai.goal.FindItemGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DomesticatedFleshBlob extends FleshBlob {

	public DomesticatedFleshBlob(EntityType<? extends DomesticatedFleshBlob> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, 0.5f)
				.add(Attributes.ATTACK_DAMAGE, 3);
	}

	@Override
	protected void updateBaseAttributes(byte size) {
		MobUtil.setAttributeBaseValue(this, Attributes.MAX_HEALTH, size * 10f);
		MobUtil.setAttributeBaseValue(this, Attributes.MOVEMENT_SPEED, 0.2f + 0.01f * size);
		MobUtil.setAttributeBaseValue(this, Attributes.ARMOR, size * 0.5f);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new BurningOrFreezingPanicGoal(this, 1.5d));
		goalSelector.addGoal(3, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
		goalSelector.addGoal(3, new EatFoodItemGoal<>(this));
		goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 6f, 0.8f, 1.2f));
		goalSelector.addGoal(4, new AvoidEntityGoal<>(this, AbstractVillager.class, 16f, 0.8f, 1.2f));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1d));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

}
