package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.shape.Shape;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ticket.ChunkTicketManager;
import net.minecraftforge.common.ticket.SimpleTicket;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShapeManager {
	private static final Map<LevelReader, Map<ChunkPos, ChunkTicketManager<Vec3>>> customTicketHandler = new WeakHashMap<>();

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		if (!event.getLevel().isClientSide()) {
			removeTickets(event.getChunk());
		}
	}

	protected static <T extends SimpleTicket<Vec3>> T addCustomTicket(Level level, T ticket, ChunkPos masterChunk, ChunkPos... additionalChunks) {
		Preconditions.checkArgument(!level.isClientSide, "Water region is only determined server-side");
		Map<ChunkPos, ChunkTicketManager<Vec3>> ticketMap = customTicketHandler.computeIfAbsent(level, id -> new MapMaker().weakValues().makeMap());

		@SuppressWarnings("unchecked")
		ChunkTicketManager<Vec3>[] additionalTickets = new ChunkTicketManager[additionalChunks.length];

		for (int i = 0; i < additionalChunks.length; i++) {
			additionalTickets[i] = ticketMap.computeIfAbsent(additionalChunks[i], ChunkTicketManager::new);
		}
		ticket.setManager(ticketMap.computeIfAbsent(masterChunk, ChunkTicketManager::new), additionalTickets);
		ticket.validate();

		return ticket;
	}

	public static ShapeTicket addShapeTicket(Level level, Shape shape) {
		AABB aabb = shape.getAABB();
		int minX = SectionPos.blockToSectionCoord(aabb.minX);
		int maxX = SectionPos.blockToSectionCoord(aabb.maxX);
		int minZ = SectionPos.blockToSectionCoord(aabb.minZ);
		int maxZ = SectionPos.blockToSectionCoord(aabb.maxZ);

		Set<ChunkPos> chunkPositions = new HashSet<>();
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				chunkPositions.add(new ChunkPos(x, z));
			}
		}

		ChunkPos mainChunkPos = null;
		double minDistance = Double.MAX_VALUE;

		for (ChunkPos chunkPos : chunkPositions) {
			double distance = getDistanceSq(chunkPos, shape.getCenter());
			if (distance < minDistance) {
				mainChunkPos = chunkPos;
				minDistance = distance;
			}
		}
		chunkPositions.remove(mainChunkPos);

		return addCustomTicket(level, new ShapeTicket(shape), mainChunkPos, chunkPositions.toArray(new ChunkPos[0]));
	}

	private static double getDistanceSq(ChunkPos pos, Vec3 vec3d) {
		double dx = pos.getMiddleBlockX() - vec3d.x;
		double dz = pos.getMiddleBlockZ() - vec3d.z;
		return dx * dx + dz * dz;
	}

	@Nullable
	public static Shape getShape(LevelReader level, BlockPos blockPos) {
		ChunkTicketManager<Vec3> ticketManager = getTicketManager(new ChunkPos(blockPos), level);
		if (ticketManager == null) return null;

		Vec3 position = Vec3.atCenterOf(blockPos);
		double minDistSqr = Double.MAX_VALUE;
		Shape closestShape = null;

		for (SimpleTicket<Vec3> ticket : ticketManager.getTickets()) {
			if (ticket.matches(position) && ticket instanceof ShapeTicket shapeTicket) {
				Shape shape = shapeTicket.getShape();
				double distSqr = shape.distanceToSqr(position.x, position.y, position.z);
				if (distSqr < minDistSqr) {
					closestShape = shape;
					minDistSqr = distSqr;
				}
			}
		}

		return closestShape;
	}

	public static Optional<MoundShape> getMoundShape(LevelReader level, BlockPos blockPos) {
		if (getShape(level, blockPos) instanceof MoundShape moundShape) {
			return Optional.of(moundShape);
		}
		return Optional.empty();
	}

	static void removeTickets(ChunkAccess chunk) {
		ChunkTicketManager<Vec3> ticketManager = getTicketManager(chunk.getPos(), chunk.getWorldForge());
		if (ticketManager != null) {
			ticketManager.getTickets().removeIf(next -> next.unload(ticketManager)); //remove if this is the master manager of the ticket
		}
	}

	@Nullable
	private static ChunkTicketManager<Vec3> getTicketManager(ChunkPos pos, LevelReader level) {
		Preconditions.checkArgument(!level.isClientSide(), "Water region is only determined server-side");
		Map<ChunkPos, ChunkTicketManager<Vec3>> ticketMap = customTicketHandler.get(level);
		return ticketMap == null ? null : ticketMap.get(pos);
	}

}
