package com.github.elenterius.biomancy.block.membrane;

import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import com.github.elenterius.biomancy.util.MobMaturity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IgnoreEntityCollisionPredicate {
	IgnoreEntityCollisionPredicate IS_BABY_MOB = (state, level, pos, entity) -> MobMaturity.BABY.test(entity);
	IgnoreEntityCollisionPredicate IS_ADULT_MOB = (state, level, pos, entity) -> MobMaturity.ADULT.test(entity);
	IgnoreEntityCollisionPredicate IS_ALIVE_MOB = (state, level, pos, entity) -> entity instanceof LivingEntity livingEntity && !entity.getType().is(ModEntityTags.FORGE_GOLEMS) && livingEntity.getMobType() != MobType.UNDEAD;
	IgnoreEntityCollisionPredicate IS_UNDEAD_MOB = (state, level, pos, entity) -> entity instanceof LivingEntity livingEntity && livingEntity.getMobType() == MobType.UNDEAD;
	IgnoreEntityCollisionPredicate IS_ITEM = (state, level, pos, entity) -> entity instanceof ItemEntity;
	IgnoreEntityCollisionPredicate IS_VALID_FOR_BLOCK_ENTITY_MEMBRANE = (state, level, pos, entity) -> level.getBlockEntity(pos) instanceof Membrane membrane && membrane.shouldIgnoreEntityCollisionAt(state, level, pos, entity);
	IgnoreEntityCollisionPredicate NEVER = (state, level, pos, entity) -> false;
	IgnoreEntityCollisionPredicate ALWAYS = (state, level, pos, entity) -> true;

	boolean test(BlockState state, BlockGetter level, BlockPos pos, Entity entity);
}
