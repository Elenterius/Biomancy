package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.mixin.ZombieVillagerMixinAccessor;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public class CleansingSerum extends Serum {

	public CleansingSerum(int color) {
		super(color);
	}

	@Override
	public boolean affectBlock(CompoundTag tag, @Nullable LivingEntity source, Level level, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		clearPotionEffects(target);
		if (target instanceof FleshBlob fleshBlob) {
			fleshBlob.clearStoredDNA();
		}

		if (!target.level.isClientSide) {
			if (target instanceof ZombieVillager) {
				if (ForgeEventFactory.canLivingConvert(target, EntityType.VILLAGER, timer -> {})) {
					((ZombieVillagerMixinAccessor) target).biomancy_cureZombie((ServerLevel) target.level);
				}
			}
			else if (target instanceof WitherSkeleton skeleton) {
				MobUtil.convertMobTo((ServerLevel) target.level, skeleton, EntityType.SKELETON);
			}
		}

		return true;
	}

	@Override
	public boolean affectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return clearPotionEffects(targetSelf);
	}

	private boolean clearPotionEffects(LivingEntity target) {
		if (!target.level.isClientSide) target.removeAllEffects();
		return true;
	}

}
