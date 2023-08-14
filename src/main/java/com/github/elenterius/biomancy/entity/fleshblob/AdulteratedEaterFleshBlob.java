package com.github.elenterius.biomancy.entity.fleshblob;

import com.github.elenterius.biomancy.entity.AdulteratedFleshkin;
import com.github.elenterius.biomancy.entity.MobUtil;
import com.github.elenterius.biomancy.entity.PrimordialFleshkin;
import com.github.elenterius.biomancy.entity.ai.goal.BurningOrFreezingPanicGoal;
import com.github.elenterius.biomancy.entity.ai.goal.DanceNearJukeboxGoal;
import com.github.elenterius.biomancy.entity.ai.goal.EatFoodItemGoal;
import com.github.elenterius.biomancy.entity.ai.goal.FindItemGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AdulteratedEaterFleshBlob extends EaterFleshBlob implements AdulteratedFleshkin {

	public static final float BASE_MAX_HEALTH = 10;
	public static final float BASE_ARMOR = 0.5f;

	public AdulteratedEaterFleshBlob(EntityType<? extends AdulteratedEaterFleshBlob> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, BASE_ARMOR)
				.add(Attributes.ATTACK_DAMAGE, 1f);
	}

	@Override
	protected void updateBaseAttributes(byte size) {
		MobUtil.setAttributeBaseValue(this, Attributes.MAX_HEALTH, size * BASE_MAX_HEALTH);
		MobUtil.setAttributeBaseValue(this, Attributes.MOVEMENT_SPEED, 0.2f + 0.01f * size);
		MobUtil.setAttributeBaseValue(this, Attributes.ARMOR, size * BASE_ARMOR);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new BurningOrFreezingPanicGoal(this, 1.5d));
		goalSelector.addGoal(3, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
		goalSelector.addGoal(3, new EatFoodItemGoal<>(this, 0.01f));
		goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 6f, 0.8f, 1.2f));
		goalSelector.addGoal(4, new AvoidEntityGoal<>(this, AbstractVillager.class, 16f, 0.8f, 1.2f));
		goalSelector.addGoal(4, new AvoidEntityGoal<>(this, FleshBlob.class, 16f, 0.8f, 1.2f, PrimordialFleshkin.class::isInstance));
		goalSelector.addGoal(5, new DanceNearJukeboxGoal<>(this));
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1d));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}

}
