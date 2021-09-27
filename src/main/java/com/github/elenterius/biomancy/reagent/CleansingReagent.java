package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.mixin.ZombieVillagerEntityMixinAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public class CleansingReagent extends Reagent {

	public CleansingReagent(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		clearPotionEffects(target);
		if (target instanceof FleshBlobEntity) {
			((FleshBlobEntity) target).clearForeignEntityDNA();
		}
		else if (target instanceof ZombieVillagerEntity) {
			if (!target.level.isClientSide && ForgeEventFactory.canLivingConvert(target, EntityType.VILLAGER, (timer) -> {})) {
				((ZombieVillagerEntityMixinAccessor) target).biomancy_cureZombie((ServerWorld) target.level);
			}
		}
		return true;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		return clearPotionEffects(targetSelf);
	}

	private boolean clearPotionEffects(LivingEntity target) {
		if (!target.level.isClientSide) target.removeAllEffects();
		return true;
	}

}
