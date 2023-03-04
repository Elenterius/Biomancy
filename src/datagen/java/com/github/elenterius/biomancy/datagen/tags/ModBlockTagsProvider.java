package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

import static com.github.elenterius.biomancy.BiomancyMod.MOD_ID;

public class ModBlockTagsProvider extends BlockTagsProvider {

	public ModBlockTagsProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
		super(generatorIn, MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	@Override
	protected void addTags() {
		addFleshyBlocksToHoeTag();

		tag(BlockTags.DOORS).add(ModBlocks.FLESH_DOOR.get()).add(ModBlocks.FULL_FLESH_DOOR.get());
		tag(BlockTags.TRAPDOORS).add(ModBlocks.FLESH_IRIS_DOOR.get());

		tag(ModTags.Blocks.FLESHY_FENCES).add(ModBlocks.FLESH_FENCE.get());
		tag(BlockTags.FENCES).addTag(ModTags.Blocks.FLESHY_FENCES);
		tag(BlockTags.FENCE_GATES).add(ModBlocks.FLESH_FENCE_GATE.get());

		tag(BlockTags.WALLS).add(ModBlocks.FLESH_WALL.get(), ModBlocks.PACKED_FLESH_WALL.get());

		tag(BlockTags.STAIRS).add(
				ModBlocks.FLESH_STAIRS.get(),
				ModBlocks.PACKED_FLESH_STAIRS.get(),
				ModBlocks.MALIGNANT_FLESH_STAIRS.get(),
				ModBlocks.PRIMAL_FLESH_STAIRS.get()
		);

		tag(BlockTags.PRESSURE_PLATES).add(ModBlocks.FLESHKIN_PRESSURE_PLATE.get());

		tag(BlockTags.CLIMBABLE).add(ModBlocks.FLESH_LADDER.get());

		tag(BlockTags.SLABS).add(
				ModBlocks.FLESH_SLAB.get(),
				ModBlocks.PACKED_FLESH_SLAB.get(),
				ModBlocks.PRIMAL_FLESH_SLAB.get(),
				ModBlocks.MALIGNANT_FLESH_SLAB.get()
		);
	}

	private void addFleshyBlocksToHoeTag() {
		BlockMaterialPredicate predicate = BlockMaterialPredicate.forMaterial(ModBlocks.FLESH_MATERIAL);
		TagAppender<Block> tag = tag(BlockTags.MINEABLE_WITH_HOE);
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(block -> predicate.test(block.defaultBlockState())).forEach(tag::add);
	}

}
