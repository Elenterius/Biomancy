package com.github.elenterius.biomancy.entity.mutation;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ChromaSheepEntity extends SheepEntity {

	public ChromaSheepEntity(EntityType<? extends SheepEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 8d).add(Attributes.MOVEMENT_SPEED, 0.23d);
	}

	@Override
	public DyeColor getColor() {
		return DyeColor.byId(random.nextInt(16));
	}

	@Override
	public ChromaSheepEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
		return ModEntityTypes.CHROMA_SHEEP.get().create(world);
	}

}
