package com.github.elenterius.biomancy.entity.mutation;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.world.World;

public class ChromaSheepEntity extends SheepEntity {

	public ChromaSheepEntity(EntityType<? extends SheepEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 8d).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23d);
	}

	@Override
	public DyeColor getFleeceColor() {
		return DyeColor.byId(rand.nextInt(16));
	}

}
