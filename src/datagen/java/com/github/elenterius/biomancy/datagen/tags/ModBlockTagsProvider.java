package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.block.FleshDoorBlock;
import com.github.elenterius.biomancy.block.FullFleshDoorBlock;
import com.github.elenterius.biomancy.block.membrane.Membrane;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.tags.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static com.github.elenterius.biomancy.BiomancyMod.MOD_ID;

public class ModBlockTagsProvider extends BlockTagsProvider {

	public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, MOD_ID, existingFileHelper);
	}

	private static TagKey<Block> tagKey(String modId, String path) {
		return BlockTags.create(new ResourceLocation(modId, path));
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		addFleshyBlocksToHoeTag();
		addCreateTags();
		addQuarkTags();

		tag(ModBlockTags.FLESH_REPLACEABLE)
				.add(Blocks.CLAY).addTag(BlockTags.SAND).addTag(Tags.Blocks.GRAVEL)
				.add(Blocks.SNOW_BLOCK, Blocks.SNOW)
				.addTag(BlockTags.LEAVES)
				.addTag(BlockTags.OVERWORLD_NATURAL_LOGS)
				.addTag(BlockTags.DIRT)
				.add(Blocks.DIRT_PATH, Blocks.FARMLAND, Blocks.MOSS_BLOCK, Blocks.VINE)
				.add(Blocks.MELON, Blocks.PUMPKIN)
				.addTag(BlockTags.FLOWERS);

		tag(ModBlockTags.ALLOW_VEINS_TO_ATTACH)
				.add(Blocks.DIRT_PATH, Blocks.FARMLAND, Blocks.VINE);

		tag(ModBlockTags.DISALLOW_VEINS_TO_ATTACH).add(
				ModBlocks.PRIMAL_BLOOM.get(),
				ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get()
		);

		tag(ModBlockTags.ACID_DESTRUCTIBLE)
				.addTag(BlockTags.LEAVES)
				.add(Blocks.MOSS_BLOCK, Blocks.VINE)
				.addTag(BlockTags.FLOWERS);

		tag(BlockTags.DOORS).add(ModBlocks.FLESH_DOOR.get()).add(ModBlocks.FULL_FLESH_DOOR.get());
		tag(BlockTags.TRAPDOORS).add(ModBlocks.FLESH_IRIS_DOOR.get());

		tag(Tags.Blocks.CHESTS).add(ModBlocks.FLESHKIN_CHEST.get());

		tag(ModBlockTags.FLESHY_FENCES).add(ModBlocks.FLESH_FENCE.get());
		tag(BlockTags.FENCES).addTag(ModBlockTags.FLESHY_FENCES);
		tag(BlockTags.FENCE_GATES).add(ModBlocks.FLESH_FENCE_GATE.get());

		tag(BlockTags.WALLS).add(
				ModBlocks.FLESH_WALL.get(),
				ModBlocks.PACKED_FLESH_WALL.get(),
				ModBlocks.MALIGNANT_FLESH_WALL.get(),
				ModBlocks.PRIMAL_FLESH_WALL.get(),
				ModBlocks.SMOOTH_PRIMAL_FLESH_WALL.get(),
				ModBlocks.POROUS_PRIMAL_FLESH_WALL.get()
		);

		tag(BlockTags.STAIRS).add(
				ModBlocks.FLESH_STAIRS.get(),
				ModBlocks.PACKED_FLESH_STAIRS.get(),
				ModBlocks.MALIGNANT_FLESH_STAIRS.get(),
				ModBlocks.PRIMAL_FLESH_STAIRS.get(),
				ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS.get(),
				ModBlocks.POROUS_PRIMAL_FLESH_STAIRS.get()
		);

		tag(BlockTags.PRESSURE_PLATES).add(ModBlocks.FLESHKIN_PRESSURE_PLATE.get());

		tag(BlockTags.CLIMBABLE).add(ModBlocks.FLESH_LADDER.get());

		tag(BlockTags.SLABS).add(
				ModBlocks.FLESH_SLAB.get(),
				ModBlocks.PACKED_FLESH_SLAB.get(),
				ModBlocks.ORNATE_FLESH_SLAB.get(),
				ModBlocks.PRIMAL_FLESH_SLAB.get(),
				ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB.get(),
				ModBlocks.POROUS_PRIMAL_FLESH_SLAB.get(),
				ModBlocks.MALIGNANT_FLESH_SLAB.get()
		);

		IntrinsicTagAppender<Block> impermeableTag = tag(BlockTags.IMPERMEABLE);
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(Membrane.class::isInstance).forEach(impermeableTag::add);
	}

	private void addFleshyBlocksToHoeTag() {
		IntrinsicTagAppender<Block> tag = tag(BlockTags.MINEABLE_WITH_HOE);
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(tag::add);
	}

	/**
	 * <a href="https://github.com/Creators-of-Create/Create/wiki/Useful-Tags">Create wiki: Useful Tags</a>
	 */
	private void addCreateTags() {
		String modId = "create";

		//Blocks which should be able to move on contraptions, but would otherwise be ignored due to their empty collision shape
		//Example: Cobweb
		tag(tagKey(modId, "movable_empty_collider")).add(
				ModBlocks.FLESH_DOOR.get(),
				ModBlocks.FLESH_IRIS_DOOR.get()
		);

		//Blocks which can count toward a functional windmill structure
		//Example: Wool
		IntrinsicTagAppender<Block> windmillSailsTag = tag(tagKey(modId, "windmill_sails"));
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(Membrane.class::isInstance).forEach(windmillSailsTag::add);
	}

	/**
	 * <a href="https://github.com/VazkiiMods/Quark/blob/master/src/main/resources/data/quark/tags">Quark Tags</a>
	 */
	private void addQuarkTags() {
		String modId = "quark";

		IntrinsicTagAppender<Block> nonDoubleDoorTag = tag(tagKey(modId, "non_double_door"));
		Predicate<Block> predicate = block -> block instanceof FleshDoorBlock || block instanceof FullFleshDoorBlock;
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(predicate).forEach(nonDoubleDoorTag::add);
	}
}
