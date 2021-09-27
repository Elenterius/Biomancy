package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class DecayReagent extends Reagent {

	public DecayReagent(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == ModBlocks.FLESH_BLOCK.get()) {
			if (!world.isClientSide) world.setBlockAndUpdate(pos, ModBlocks.NECROTIC_FLESH_BLOCK.get().defaultBlockState());
			return true;
		}
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (!target.level.isClientSide) {
			Collection<EffectInstance> effects = target.getActiveEffects();
			int amplifier = 0;
			int duration = 0;
			for (EffectInstance effectInstance : effects) {
				if (effectInstance.getEffect() == ModEffects.FLESH_EATING_DISEASE.get()) {
					amplifier = effectInstance.getAmplifier() + 1;
					duration = effectInstance.getDuration();
					break;
				}
			}
			duration += 5 * 120;

			if (!convertLivingEntity((ServerWorld) target.level, target, amplifier)) {
				EffectInstance effectInstance = new EffectInstance(ModEffects.FLESH_EATING_DISEASE.get(), duration, amplifier);
				target.addEffect(effectInstance);
			}
		}
		return true;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		if (!targetSelf.level.isClientSide) {
			Collection<EffectInstance> effects = targetSelf.getActiveEffects();
			int amplifier = 0;
			int duration = 0;
			for (EffectInstance effectInstance : effects) {
				if (effectInstance.getEffect() == ModEffects.FLESH_EATING_DISEASE.get()) {
					amplifier = effectInstance.getAmplifier() + 1;
					duration = effectInstance.getDuration();
					break;
				}
			}
			EffectInstance effectInstance = new EffectInstance(ModEffects.FLESH_EATING_DISEASE.get(), 5 * 120 + duration, amplifier);
			targetSelf.addEffect(effectInstance);
		}
		return true;
	}

	private boolean convertLivingEntity(ServerWorld world, LivingEntity target, int amplifier) {
		if (amplifier < 1) return false;

		if (target instanceof ZombieEntity && ForgeEventFactory.canLivingConvert(target, EntityType.SKELETON, (timer) -> {})) {
			SkeletonEntity skeleton = ((ZombieEntity) target).convertTo(EntityType.SKELETON, true); // create new entity with same settings & equipment and remove old entity
			if (skeleton != null) {
				skeleton.finalizeSpawn(world, world.getCurrentDifficultyAt(target.blockPosition()), SpawnReason.CONVERSION, null, null);
				skeleton.invulnerableTime = 60;
				ForgeEventFactory.onLivingConvert(target, skeleton);
				if (!target.isSilent()) {
					world.levelEvent(null, Constants.WorldEvents.ZOMBIE_INFECT_SOUND, target.blockPosition(), 0);
				}
				return true;
			}
		}
		else if (target instanceof ZombieHorseEntity && ForgeEventFactory.canLivingConvert(target, EntityType.SKELETON_HORSE, (timer) -> {})) {
			SkeletonHorseEntity horse = ((ZombieHorseEntity) target).convertTo(EntityType.SKELETON_HORSE, true); // create new entity with same settings & equipment and remove old entity
			if (horse != null) {
				horse.finalizeSpawn(world, world.getCurrentDifficultyAt(target.blockPosition()), SpawnReason.CONVERSION, null, null);
				horse.invulnerableTime = 60;
				UUID owner = ((ZombieHorseEntity) target).getOwnerUUID();
				if (owner != null) {
					horse.setOwnerUUID(owner);
				}
				horse.setTamed(((ZombieHorseEntity) target).isTamed());
				ForgeEventFactory.onLivingConvert(target, horse);
				if (!target.isSilent()) {
					world.levelEvent(null, Constants.WorldEvents.ZOMBIE_INFECT_SOUND, target.blockPosition(), 0);
				}
				return true;
			}
		}

		return false;
	}

}
