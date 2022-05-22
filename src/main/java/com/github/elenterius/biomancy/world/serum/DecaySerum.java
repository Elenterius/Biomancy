package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class DecaySerum extends Serum {

	public DecaySerum(int colorIn) {
		super(colorIn);
	}

	//TODO: use withering ooze for this?
//	@Override
//	public boolean affectBlock(CompoundTag tag, @Nullable LivingEntity source, Level world, BlockPos pos, Direction facing) {
//		BlockState state = world.getBlockState(pos);
//		Block block = state.getBlock();

//		if (block == ModBlocks.FLESH_BLOCK.get()) {
//			if (!world.isClientSide) world.setBlockAndUpdate(pos, ModBlocks.NECROTIC_FLESH_BLOCK.get().defaultBlockState());
//			return true;
//		}
//		else
//		if (block == Blocks.SWEET_BERRY_BUSH || BlockTags.SAPLINGS.contains(block)) {
//			if (!world.isClientSide) world.setBlockAndUpdate(pos, Blocks.DEAD_BUSH.defaultBlockState());
//			return true;
//		}
//		else if (block == Blocks.SPRUCE_LEAVES && world.getBlockState(pos.below()).is(BlockTags.DIRT)) {
//			if (!world.isClientSide) {
//				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
//				world.setBlockAndUpdate(pos.below(), Blocks.PODZOL.defaultBlockState());
//			}
//			return true;
//		}
//
//		return false;
//	}

	@Override
	public void affectEntity(CompoundTag nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (!target.level.isClientSide) {
			Collection<MobEffectInstance> effects = target.getActiveEffects();
			int amplifier = 0;
			int duration = 0;
			for (MobEffectInstance effectInstance : effects) {
				if (effectInstance.getEffect() == ModMobEffects.FLESH_EATING_DISEASE.get()) {
					amplifier = effectInstance.getAmplifier() + 1;
					duration = effectInstance.getDuration();
					break;
				}
			}
			duration += 5 * 120;

			if (!convertLivingEntity((ServerLevel) target.level, target, amplifier)) {
				MobEffectInstance effectInstance = new MobEffectInstance(ModMobEffects.FLESH_EATING_DISEASE.get(), duration, amplifier);
				target.addEffect(effectInstance);
			}
		}
	}

	@Override
	public void affectPlayerSelf(CompoundTag nbt, Player targetSelf) {
		if (!targetSelf.level.isClientSide) {
			Collection<MobEffectInstance> effects = targetSelf.getActiveEffects();
			int amplifier = 0;
			int duration = 0;
			for (MobEffectInstance effectInstance : effects) {
				if (effectInstance.getEffect() == ModMobEffects.FLESH_EATING_DISEASE.get()) {
					amplifier = effectInstance.getAmplifier() + 1;
					duration = effectInstance.getDuration();
					break;
				}
			}
			MobEffectInstance effectInstance = new MobEffectInstance(ModMobEffects.FLESH_EATING_DISEASE.get(), 5 * 120 + duration, amplifier);
			targetSelf.addEffect(effectInstance);
		}
	}

	private boolean convertLivingEntity(ServerLevel level, LivingEntity target, int amplifier) {
		if (amplifier < 1) return false;

		if (target instanceof Zombie zombieTarget && ForgeEventFactory.canLivingConvert(zombieTarget, EntityType.SKELETON, timer -> {})) {
			Skeleton skeleton = zombieTarget.convertTo(EntityType.SKELETON, true); // create new entity with same settings & equipment and remove old entity
			if (skeleton != null) {
				skeleton.finalizeSpawn(level, level.getCurrentDifficultyAt(zombieTarget.blockPosition()), MobSpawnType.CONVERSION, null, null);
				skeleton.invulnerableTime = 60;
				ForgeEventFactory.onLivingConvert(zombieTarget, skeleton);
				if (!zombieTarget.isSilent()) {
					level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, zombieTarget.blockPosition(), 0);
				}
				return true;
			}
		}
		else if (target instanceof ZombieHorse zombieHorseTarget && ForgeEventFactory.canLivingConvert(zombieHorseTarget, EntityType.SKELETON_HORSE, timer -> {})) {
			SkeletonHorse horse = zombieHorseTarget.convertTo(EntityType.SKELETON_HORSE, true); // create new entity with same settings & equipment and remove old entity
			if (horse != null) {
				horse.finalizeSpawn(level, level.getCurrentDifficultyAt(zombieHorseTarget.blockPosition()), MobSpawnType.CONVERSION, null, null);
				horse.invulnerableTime = 60;
				UUID owner = zombieHorseTarget.getOwnerUUID();
				if (owner != null) {
					horse.setOwnerUUID(owner);
				}
				horse.setTamed(zombieHorseTarget.isTamed());
				ForgeEventFactory.onLivingConvert(zombieHorseTarget, horse);
				if (!zombieHorseTarget.isSilent()) {
					level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, zombieHorseTarget.blockPosition(), 0);
				}
				return true;
			}
		}

		return false;
	}

}
