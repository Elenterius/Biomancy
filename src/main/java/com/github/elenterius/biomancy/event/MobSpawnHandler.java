package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.RegionManager;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MobSpawnHandler {

	private static final Set<MobSpawnType> NATURAL_SPAWN_TYPE = Set.of(
			MobSpawnType.EVENT,
			MobSpawnType.NATURAL,
			MobSpawnType.CHUNK_GENERATION,
			MobSpawnType.PATROL
	);

	private MobSpawnHandler() {}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		if (event.isCanceled()) return;

		boolean isNaturalSpawn = NATURAL_SPAWN_TYPE.contains(event.getSpawnReason());

		if (isNaturalSpawn && event.getLevel() instanceof ServerLevel serverLevel) {
			BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
			//Mob entity = event.getEntity();

			//TODO: implement check with BoundingBox of entity
			if (RegionManager.getClosestShape(serverLevel, pos) instanceof MoundShape) {
				//TODO: chamber level mob spawn prevention? --> e.g. spawning chamber for creepers
				//MoundChamber chamber = moundShape.getChamberAt(pos.getX(), pos.getY(), pos.getZ());

				event.setResult(Event.Result.DENY);
			}
		}
	}

}
