package com.github.elenterius.biomancy.handler;

import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CustomClientEventHandler {
	private CustomClientEventHandler() {}

	public static void onEntityEvent(Entity entity, int eventId) {
		switch (eventId) {
			case 0:
				addParticlesAroundEntity(entity.level, ParticleTypes.HAPPY_VILLAGER, entity);
				break;
			case 1:
				addParticlesAroundEntity(entity.level, ParticleTypes.ANGRY_VILLAGER, entity);
				break;
			default:
				break;
		}
	}

	public static void onWorldEvent(double x, double y, double z, byte eventId) {}

	public static void addParticlesAroundEntity(World level, IParticleData particle, Entity entity) {
		for (int i = 0; i < 6; i++) {
			double x = level.getRandom().nextGaussian() * 0.02d;
			double y = level.getRandom().nextGaussian() * 0.02d;
			double z = level.getRandom().nextGaussian() * 0.02d;
			level.addParticle(particle, entity.getRandomX(1d), entity.getRandomY(), entity.getRandomZ(1d), x, y, z);
		}
	}

}
