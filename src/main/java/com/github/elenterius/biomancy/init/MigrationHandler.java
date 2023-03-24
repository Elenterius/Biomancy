package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.MissingMappingsEvent.Mapping;

import java.util.List;

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
	public static void onMissingMappings(final MissingMappingsEvent event) {
		handleMissingSerums(event.getMappings(ModSerums.SERUMS.getRegistryKey(), BiomancyMod.MOD_ID));
		handleMissingItems(event.getMappings(ModItems.ITEMS.getRegistryKey(), BiomancyMod.MOD_ID));
		handleMissingBlocks(event.getMappings(ModBlocks.BLOCKS.getRegistryKey(), BiomancyMod.MOD_ID));
		handleMissingBlockEntityTypes(event.getMappings(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), BiomancyMod.MOD_ID));
	}

	private static void handleMissingSerums(List<Mapping<Serum>> mappings) {
		if (mappings.isEmpty()) return;

		for (Mapping<Serum> mapping : mappings) {
			if (mapping.getKey().getPath().equals("growth_serum")) {
				mapping.remap(ModSerums.AGEING_SERUM.get());
			}
			else {
				mapping.ignore();
			}
		}
	}

	public static void handleMissingBlocks(List<Mapping<Block>> mappings) {
		if (mappings.isEmpty()) return;

		BiomancyMod.LOGGER.info("found missing block mappings, attempting to remap...");

		for (Mapping<Block> mapping : mappings) {
			String path = mapping.getKey().getPath();
			switch (path) {
				case "creator" -> mapping.remap(ModBlocks.PRIMORDIAL_CRADLE.get());
				case "flesh_block" -> mapping.remap(ModBlocks.FLESH.get());
				case "flesh_block_slab" -> mapping.remap(ModBlocks.FLESH_SLAB.get());
				case "flesh_block_stairs" -> mapping.remap(ModBlocks.FLESH_STAIRS.get());
				case "flesh_irisdoor" -> mapping.remap(ModBlocks.FLESH_IRIS_DOOR.get());
				case "necrotic_flesh_block" -> mapping.remap(ModBlocks.MALIGNANT_FLESH.get());
				case "flesh_tentacle" -> mapping.remap(ModBlocks.MALIGNANT_FLESH_VEINS.get());
				default -> mapping.ignore();
			}
		}
	}

	public static void handleMissingBlockEntityTypes(List<Mapping<BlockEntityType<?>>> mappings) {
		if (mappings.isEmpty()) return;

		for (Mapping<BlockEntityType<?>> mapping : mappings) {
			if (mapping.getKey().getPath().equals("creator")) {
				mapping.remap(ModBlockEntities.PRIMORDIAL_CRADLE.get());
			}
			else {
				mapping.ignore();
			}
		}
	}

	public static void handleMissingItems(List<Mapping<Item>> mappings) {
		if (mappings.isEmpty()) return;

		BiomancyMod.LOGGER.info("found missing item mappings, attempting to remap...");

		for (Mapping<Item> mapping : mappings) {
			String path = mapping.getKey().getPath();
			switch (path) {
				case "mascot_pattern", "mascot_outline_pattern", "mascot_accent_pattern" -> mapping.remap(ModItems.MASCOT_BANNER_PATTERNS.get());

				case "creator" -> mapping.remap(ModItems.PRIMORDIAL_CRADLE.get());
				case "flesh_block" -> mapping.remap(ModItems.FLESH_BLOCK.get());
				case "flesh_block_slab" -> mapping.remap(ModItems.FLESH_SLAB.get());
				case "flesh_block_stairs" -> mapping.remap(ModItems.FLESH_STAIRS.get());
				case "flesh_irisdoor" -> mapping.remap(ModItems.FLESH_IRIS_DOOR.get());
				case "necrotic_flesh_block" -> mapping.remap(ModItems.MALIGNANT_FLESH_BLOCK.get());
				case "flesh_tentacle" -> mapping.remap(ModItems.MALIGNANT_FLESH_VEINS.get());

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
