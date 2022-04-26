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

		tag(ModTags.Blocks.FLESHY_FENCES).add(ModBlocks.FLESH_FENCE.get());
		tag(BlockTags.FENCES).addTag(ModTags.Blocks.FLESHY_FENCES);

		tag(BlockTags.CLIMBABLE).add(ModBlocks.FLESH_LADDER.get());
	}

	private void addFleshyBlocksToHoeTag() {
		BlockMaterialPredicate predicate = BlockMaterialPredicate.forMaterial(ModBlocks.FLESH_MATERIAL);
		TagAppender<Block> tag = tag(BlockTags.MINEABLE_WITH_HOE);
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter(block -> predicate.test(block.defaultBlockState())).forEach(tag::add);
	}

}
