package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.world.MobSpawnFilter;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MobSpawnHandler {

	private MobSpawnHandler() {}

	//TODO: re-enable this
	public static void onCheckSpawn(final MobSpawnEvent.PositionCheck event) {
		if (event.isCanceled()) return;

		if (event.getLevel() instanceof ServerLevel serverLevel) {
			MobSpawnType spawnReason = event.getSpawnType();

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

	@SubscribeEvent
	public static void onBabySpawn(final BabyEntitySpawnEvent event) {
		Mob parentA = event.getParentA();
		Mob parentB = event.getParentB();
		AgeableMob child = event.getChild();

		if (child instanceof Pig) {
			float p = (parentA.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f) + (parentB.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f);
			if (p > 0f && parentA.getRandom().nextFloat() < p) {
				event.setChild(ModEntityTypes.FLESH_PIG.get().create(parentA.level()));
			}
		}
		else if (child instanceof Cow) {
			float p = (parentA.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f) + (parentB.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f);
			if (p > 0f && parentA.getRandom().nextFloat() < p) {
				event.setChild(ModEntityTypes.FLESH_COW.get().create(parentA.level()));
			}
		}
		else if (child instanceof Chicken) {
			float p = (parentA.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f) + (parentB.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f);
			if (p > 0f && parentA.getRandom().nextFloat() < p) {
				event.setChild(ModEntityTypes.FLESH_CHICKEN.get().create(parentA.level()));
			}
		}
		else if (child instanceof Sheep) {
			float p = (parentA.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f) + (parentB.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f);
			if (p > 0f) {
				if (parentA.getRandom().nextFloat() < p) {
					event.setChild(ModEntityTypes.FLESH_SHEEP.get().create(parentA.level()));
				}
				else if (parentA.getRandom().nextFloat() < p) {
					event.setChild(ModEntityTypes.THICK_FUR_SHEEP.get().create(parentA.level()));
				}
				else if (parentA.getRandom().nextFloat() < p * 0.5f) {
					event.setChild(ModEntityTypes.CHROMA_SHEEP.get().create(parentA.level()));
				}
			}
		}
	}

}
