package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.MobSpawnFilter;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MobSpawnHandler {

	private MobSpawnHandler() {}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		if (event.isCanceled()) return;

		if (event.getLevel() instanceof ServerLevel serverLevel) {
			MobSpawnType spawnReason = event.getSpawnReason();

			//TODO: check unnatural spawns as well??
			if (MobSpawnFilter.isNaturalSpawn(spawnReason)) {
				Mob mob = event.getEntity();
				double x = event.getX();
				double y = event.getY();
				double z = event.getZ();

				Predicate<Shape> denySpawnPredicate = shape -> shape instanceof MobSpawnFilter filter && !filter.isMobAllowedToSpawn(mob, spawnReason, serverLevel, x, y, z);
				boolean denySpawn = SpatialShapeManager.getAnyShape(serverLevel, mob, SpatialShapeManager.QueryStrategy.INTERSECTION, denySpawnPredicate) != null;

				if (denySpawn) {
					//TODO: chamber specific mob spawn filters? --> e.g. chamber only allows creepers spawns
					//MoundChamber chamber = moundShape.getChamberAt(pos.getX(), pos.getY(), pos.getZ());
					event.setResult(Event.Result.DENY);
				}
			}
		}
	}

}
