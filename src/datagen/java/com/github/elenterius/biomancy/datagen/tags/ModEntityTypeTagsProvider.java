package com.github.elenterius.biomancy.datagen.tags;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.MobUtil;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.core.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

	protected static final EntityType<?>[] TOXIC_MOBS = {
			EntityType.CAVE_SPIDER,
			EntityType.PUFFERFISH,
			EntityType.BEE
	};
	protected static final EntityType<?>[] VOLATILE_MOBS = {
			EntityType.CREEPER,
			EntityType.GHAST, EntityType.BLAZE,
			EntityType.WITHER, EntityType.ENDER_DRAGON
	};
	protected static final EntityType<?>[] SHARP_CLAW_MOBS = {
			EntityType.BAT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.ENDER_DRAGON
	};
	protected static final EntityType<?>[] SHARP_FANG_MOBS = {
			EntityType.BAT,
			EntityType.CAT, EntityType.OCELOT,
			EntityType.WOLF, EntityType.FOX,
			EntityType.POLAR_BEAR, EntityType.PANDA,
			EntityType.HOGLIN, EntityType.ZOGLIN,
			EntityType.ENDER_DRAGON
	};
	protected static final Set<EntityType<?>> INVALID_MOBS_FOR_MEATY_LOOT = Set.of(
			EntityType.SLIME, EntityType.MAGMA_CUBE,
			EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.SHULKER,
			EntityType.VEX, EntityType.GHAST, EntityType.ALLAY,
			EntityType.BLAZE,
			EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER,
			EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.WITHER,

			AMEntityRegistry.SPECTRE.get(), AMEntityRegistry.VOID_WORM.get(), AMEntityRegistry.SKELEWAG.get(), AMEntityRegistry.BONE_SERPENT.get(),
			AMEntityRegistry.MIMICUBE.get(), AMEntityRegistry.FLUTTER.get(), AMEntityRegistry.GUSTER.get()
	);

	public ModEntityTypeTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
		super(pGenerator, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		createTag(ModEntityTags.NOT_CLONEABLE)
				.addTag(ModEntityTags.FORGE_BOSSES)
				.add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM)
				.addOptional("strawgolem:strawgolem", "strawgolem:strawnggolem");

		addSpecialMobLootTags();
	}

	private void addSpecialMobLootTags() {
		createTag(ModEntityTags.SHARP_FANG)
				.add(SHARP_FANG_MOBS)
				.addOptional(
						AMEntityRegistry.GRIZZLY_BEAR, AMEntityRegistry.DROPBEAR, AMEntityRegistry.SEA_BEAR,
						AMEntityRegistry.GORILLA, AMEntityRegistry.GELADA_MONKEY, AMEntityRegistry.CAPUCHIN_MONKEY,
						AMEntityRegistry.RATTLESNAKE, AMEntityRegistry.ANACONDA,
						AMEntityRegistry.TIGER, AMEntityRegistry.MANED_WOLF, AMEntityRegistry.SNOW_LEOPARD,
						AMEntityRegistry.TUSKLIN
				);

		createTag(ModEntityTags.SHARP_CLAW)
				.add(SHARP_CLAW_MOBS)
				.addOptional(
						AMEntityRegistry.GRIZZLY_BEAR, AMEntityRegistry.DROPBEAR, AMEntityRegistry.SEA_BEAR,
						AMEntityRegistry.ROADRUNNER, AMEntityRegistry.SOUL_VULTURE, AMEntityRegistry.BALD_EAGLE, AMEntityRegistry.EMU,
						AMEntityRegistry.PLATYPUS,
						AMEntityRegistry.RACCOON, AMEntityRegistry.TASMANIAN_DEVIL,
						AMEntityRegistry.TIGER, AMEntityRegistry.MANED_WOLF, AMEntityRegistry.SNOW_LEOPARD
				);

		createTag(ModEntityTags.TOXIN_GLAND)
				.add(TOXIC_MOBS)
				.addOptional(
						AMEntityRegistry.KOMODO_DRAGON,
						AMEntityRegistry.PLATYPUS
				);

		createTag(ModEntityTags.VOLATILE_GLAND)
				.add(VOLATILE_MOBS);

		createTag(ModEntityTags.BONE_MARROW)
				.add(EntityType.SKELETON_HORSE, EntityType.WARDEN)
				.addTag(EntityTypeTags.SKELETONS)
				.addOptional(AMEntityRegistry.SKELEWAG, AMEntityRegistry.BONE_SERPENT);

		createTag(ModEntityTags.WITHERED_BONE_MARROW)
				.add(EntityType.WITHER_SKELETON, EntityType.WITHER);

		buildSinewAndBileTag();
	}

	private void buildSinewAndBileTag() {
		Set<String> validNamespaces = Set.of("minecraft", BiomancyMod.MOD_ID, AlexsMobs.MODID);
		Predicate<EntityType<?>> allowedNamespace = entityType -> validNamespaces.contains(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entityType)).getNamespace());

		Set<EntityType<?>> toxicMobs = Set.of(TOXIC_MOBS);
		Set<EntityType<?>> volatileMobs = Set.of(VOLATILE_MOBS);
		Predicate<EntityType<?>> canHaveGland = entityType -> !toxicMobs.contains(entityType) && !volatileMobs.contains(entityType);

		EnhancedTagAppender<EntityType<?>> sinewTag = createTag(ModEntityTags.SINEW);
		EnhancedTagAppender<EntityType<?>> bileGlandTag = createTag(ModEntityTags.BILE_GLAND);

		FakeLevel fakeLevel = new FakeLevel(); //we ignore that this is a AutoClosable object

		for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES) {
			if (!allowedNamespace.test(entityType)) continue;

			Entity entity = entityType.create(fakeLevel);
			if (entity instanceof Mob mob && isValidForMeatyLoot(mob, entityType)) {
				sinewTag.add(entityType);
				if (canHaveGland.test(entityType)) bileGlandTag.add(entityType);
			}
		}
	}

	private boolean isValidForMeatyLoot(Mob mob, EntityType<?> entityType) {
		if (INVALID_MOBS_FOR_MEATY_LOOT.contains(entityType)) return false;
		if (MobUtil.isUndead(mob)) return false;
		if (MobUtil.isSkeleton(mob)) return false;
		if (MobUtil.isWithered(mob)) return false;
		if (mob instanceof AbstractGolem) return false;
		if (mob instanceof Slime) return false;
		if (mob instanceof Warden) return false;
		return true;
	}

	protected EnhancedTagAppender<EntityType<?>> createTag(TagKey<EntityType<?>> tag) {
		return new EnhancedTagAppender<>(tag(tag), ForgeRegistries.ENTITY_TYPES);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	private static final class FakeLevel extends Level {

		private final Scoreboard scoreboard;

		private FakeLevel() {
			super(null, null, RegistryAccess.BUILTIN.get().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD), null, false, false, 0, 1);
			scoreboard = new Scoreboard();
		}

		@Override
		public RegistryAccess registryAccess() {
			return null;
		}

		@Override
		public void close() {
			//do nothing
		}

		@Override
		public long getDayTime() {
			return 0L; //fix for villagers
		}

		@Override
		public long getGameTime() {
			return 0L; //fix for villagers
		}

		@Override
		public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
			//do nothing
		}

		@Override
		public void playSound(@Nullable Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
			//do nothing
		}

		@Override
		public void playSound(@Nullable Player player, Entity entity, SoundEvent event, SoundSource category, float volume, float pitch) {
			//do nothing
		}

		@Override
		public String gatherChunkSourceStats() {
			return null;
		}

		@Nullable
		@Override
		public Entity getEntity(int id) {
			return null;
		}

		@Nullable
		@Override
		public MapItemSavedData getMapData(String mapName) {
			return null;
		}

		@Override
		public void setMapData(String mapId, MapItemSavedData data) {
			//do nothing
		}

		@Override
		public int getFreeMapId() {
			return 0;
		}

		@Override
		public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
			//do nothing
		}

		@Override
		public Scoreboard getScoreboard() {
			return scoreboard; //fix for wither boss
		}

		@Override
		public RecipeManager getRecipeManager() {
			return null;
		}

		@Override
		protected LevelEntityGetter<Entity> getEntities() {
			return null;
		}

		@Override
		public LevelTickAccess<Block> getBlockTicks() {
			return null;
		}

		@Override
		public LevelTickAccess<Fluid> getFluidTicks() {
			return null;
		}

		@Override
		public ChunkSource getChunkSource() {
			return null;
		}

		@Override
		public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
			//do nothing
		}

		@Override
		public void gameEvent(@Nullable Entity entity, GameEvent event, BlockPos pos) {
			//do nothing
		}

		@Override
		public float getShade(Direction direction, boolean shade) {
			return 0;
		}

		@Override
		public List<? extends Player> players() {
			return List.of();
		}

		@Override
		public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
			return null;
		}

		@Override
		public void gameEvent(GameEvent event, Vec3 ppos, Context context) {
			//do nothing
			
		}

		@Override
		public void playSeededSound(Player player, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, long seed) {
			//do nothing
			
		}

		@Override
		public void playSeededSound(Player player, Entity entity, SoundEvent sound, SoundSource source, float volume, float pitch, long seed) {
			//do nothing
		}
	}

}
