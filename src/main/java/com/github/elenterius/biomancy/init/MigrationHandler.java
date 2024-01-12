package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Currently MissingMappings Event fires on WRONG Bus (FORGE BUS) even though it implements IModBusEvent
 * <a href="https://github.com/MinecraftForge/MinecraftForge/issues/8513">view GitHub Issue</a>
 * <br>
 * SHOULD BE FIXED IN Forge 1.19
 * <a href="https://github.com/MinecraftForge/MinecraftForge/pull/8538">viw Pull Request Draft</a>
 */
@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MigrationHandler {

	private MigrationHandler() {}

	@SubscribeEvent
	public static void onMissingSerumMappings(final RegistryEvent.MissingMappings<Serum> event) {
		ImmutableList<RegistryEvent.MissingMappings.Mapping<Serum>> mappings = event.getMappings(BiomancyMod.MOD_ID);
		if (mappings.isEmpty()) return;

		for (RegistryEvent.MissingMappings.Mapping<Serum> mapping : mappings) {
			if (mapping.key.getPath().equals("growth_serum")) {
				mapping.remap(ModSerums.AGEING_SERUM.get());
			}
		}
	}

	@SubscribeEvent
	public static void onMissingBlockMappings(final RegistryEvent.MissingMappings<Block> event) {
		ImmutableList<RegistryEvent.MissingMappings.Mapping<Block>> mappings = event.getMappings(BiomancyMod.MOD_ID);
		if (mappings.isEmpty()) return;

		BiomancyMod.LOGGER.info("found missing block mappings, attempting to remap...");

		for (RegistryEvent.MissingMappings.Mapping<Block> mapping : mappings) {
			String path = mapping.key.getPath();
			switch (path) {
				case "bio_lantern" -> mapping.remap(ModBlocks.YELLOW_BIO_LANTERN.get());
				case "bone_spike" -> mapping.remap(ModBlocks.FLESH_SPIKE.get());
				case "creator" -> mapping.remap(ModBlocks.PRIMORDIAL_CRADLE.get());
				case "flesh_block" -> mapping.remap(ModBlocks.FLESH.get());
				case "flesh_block_slab" -> mapping.remap(ModBlocks.FLESH_SLAB.get());
				case "flesh_block_stairs" -> mapping.remap(ModBlocks.FLESH_STAIRS.get());
				case "flesh_irisdoor" -> mapping.remap(ModBlocks.FLESH_IRIS_DOOR.get());
				case "necrotic_flesh_block" -> mapping.remap(ModBlocks.MALIGNANT_FLESH.get());
				case "flesh_tentacle" -> mapping.remap(ModBlocks.MALIGNANT_FLESH_VEINS.get());
				case "corrupted_primal_flesh" -> mapping.remap(ModBlocks.PRIMAL_FLESH.get());

				default -> mapping.ignore();
			}
		}
	}

	@SubscribeEvent
	public static void onMissingEntityTypes(final RegistryEvent.MissingMappings<EntityType<?>> event) {
		ImmutableList<RegistryEvent.MissingMappings.Mapping<EntityType<?>>> mappings = event.getMappings(BiomancyMod.MOD_ID);
		if (mappings.isEmpty()) return;

		BiomancyMod.LOGGER.info("found missing entity mappings, attempting to remap...");

		for (RegistryEvent.MissingMappings.Mapping<EntityType<?>> mapping : mappings) {
			String path = mapping.key.getPath();
			if (path.equals("malignant_flesh_blob")) {
				mapping.remap(ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get());
			}
			else {
				mapping.ignore();
			}
		}
	}

	@SubscribeEvent
	public static void onMissingBlockEntityMappings(final RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
		ImmutableList<RegistryEvent.MissingMappings.Mapping<BlockEntityType<?>>> mappings = event.getMappings(BiomancyMod.MOD_ID);
		if (mappings.isEmpty()) return;

		for (RegistryEvent.MissingMappings.Mapping<BlockEntityType<?>> mapping : mappings) {
			String path = mapping.key.getPath();
			if (path.equals("creator")) {
				mapping.remap(ModBlockEntities.PRIMORDIAL_CRADLE.get());
			}
			else {
				mapping.ignore();
			}
		}
	}

	@SubscribeEvent
	public static void onMissingItemMappings(final RegistryEvent.MissingMappings<Item> event) {
		ImmutableList<RegistryEvent.MissingMappings.Mapping<Item>> mappings = event.getMappings(BiomancyMod.MOD_ID);
		if (mappings.isEmpty()) return;

		BiomancyMod.LOGGER.info("found missing item mappings, attempting to remap...");

		for (RegistryEvent.MissingMappings.Mapping<Item> mapping : mappings) {
			String path = mapping.key.getPath();
			switch (path) {
				case "long_claws" -> mapping.remap(ModItems.RAVENOUS_CLAWS.get());

				case "bio_lantern" -> mapping.remap(ModItems.YELLOW_BIO_LANTERN.get());
				case "glass_vial" -> mapping.remap(ModItems.VIAL.get());

				case "creator" -> mapping.remap(ModItems.PRIMORDIAL_CRADLE.get());
				case "flesh_block" -> mapping.remap(ModItems.FLESH_BLOCK.get());
				case "flesh_block_slab" -> mapping.remap(ModItems.FLESH_SLAB.get());
				case "flesh_block_stairs" -> mapping.remap(ModItems.FLESH_STAIRS.get());
				case "flesh_irisdoor" -> mapping.remap(ModItems.FLESH_IRIS_DOOR.get());
				case "necrotic_flesh_block" -> mapping.remap(ModItems.MALIGNANT_FLESH_BLOCK.get());
				case "flesh_tentacle" -> mapping.remap(ModItems.MALIGNANT_FLESH_VEINS.get());
				case "corrupted_primal_flesh" -> mapping.remap(ModItems.PRIMAL_FLESH_BLOCK.get());

				case "biometal" -> mapping.remap(ModItems.LIVING_FLESH.get());
				case "bone_gear" -> mapping.remap(Items.BONE);
				case "lens" -> mapping.remap(ModItems.GEM_FRAGMENTS.get());
				case "skin_chunk", "flesh_lump", "mended_skin" -> mapping.remap(ModItems.FLESH_BITS.get());
				case "stomach", "artificial_stomach" -> mapping.remap(ModItems.GENERIC_MOB_GLAND.get());
				case "bolus" -> mapping.remap(ModItems.NUTRIENTS.get());
				case "keratin_filaments" -> mapping.remap(ModItems.TOUGH_FIBERS.get());
				case "digestate" -> mapping.remap(ModItems.ORGANIC_MATTER.get());
				case "oxide_powder", "silicate_paste", "bio_minerals" -> mapping.remap(ModItems.MINERAL_FRAGMENT.get());
				case "hormone_bile" -> mapping.remap(ModItems.HORMONE_SECRETION.get());
				default -> mapping.ignore();
			}
		}
	}

}
