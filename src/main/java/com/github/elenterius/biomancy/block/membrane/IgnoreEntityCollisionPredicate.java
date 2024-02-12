package com.github.elenterius.biomancy.block.membrane;

import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IgnoreEntityCollisionPredicate {
	IgnoreEntityCollisionPredicate IS_BABY_MOB = (state, level, pos, entity) -> entity instanceof LivingEntity livingEntity && livingEntity.isBaby();
	IgnoreEntityCollisionPredicate IS_ADULT_MOB = (state, level, pos, entity) -> entity instanceof LivingEntity livingEntity && !livingEntity.isBaby();
	IgnoreEntityCollisionPredicate IS_ALIVE_MOB = (state, level, pos, entity) -> entity instanceof LivingEntity livingEntity && !entity.getType().is(ModEntityTags.FORGE_GOLEMS) && livingEntity.getMobType() != MobType.UNDEAD;
	IgnoreEntityCollisionPredicate IS_UNDEAD_MOB = (state, level, pos, entity) -> entity instanceof LivingEntity livingEntity && livingEntity.getMobType() == MobType.UNDEAD;
	IgnoreEntityCollisionPredicate IS_ITEM = (state, level, pos, entity) -> entity instanceof ItemEntity;
	IgnoreEntityCollisionPredicate NEVER = (state, level, pos, entity) -> false;

	boolean test(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity entity);
}
