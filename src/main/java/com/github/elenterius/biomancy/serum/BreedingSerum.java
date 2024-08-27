package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class BreedingSerum extends BasicSerum {

	public BreedingSerum(int colorIn) {
		super(colorIn);
	}

	private static boolean isMatureVillager(LivingEntity target) {
		return target instanceof Villager villager && !villager.isBaby();
	}

	private static boolean isMatureAnimal(LivingEntity target) {
		return target instanceof Animal animal && !animal.isBaby();
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return isMatureAnimal(target) || isMatureVillager(target);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (isMatureAnimal(target) || isMatureVillager(target)) {
			target.addEffect(new MobEffectInstance(ModMobEffects.LIBIDO.get(), 14 * 20, 1, false, true));
		}
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

	@Override
	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {}

}
