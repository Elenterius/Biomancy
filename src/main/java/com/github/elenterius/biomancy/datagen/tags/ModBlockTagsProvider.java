package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

import static com.github.elenterius.biomancy.BiomancyMod.MOD_ID;

public class ModBlockTagsProvider extends BlockTagsProvider {

	public ModBlockTagsProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
		super(generatorIn, MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(BlockTags.MINEABLE_WITH_HOE).add(
				ModBlocks.FLESH_BLOCK.get(), ModBlocks.FLESH_BLOCK_SLAB.get(), ModBlocks.FLESH_BLOCK_STAIRS.get(),
				ModBlocks.NECROTIC_FLESH_BLOCK.get(),
				ModBlocks.CREATOR.get()
		);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
