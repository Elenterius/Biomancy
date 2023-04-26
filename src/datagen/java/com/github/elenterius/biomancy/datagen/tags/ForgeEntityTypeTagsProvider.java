package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class ForgeEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public ForgeEntityTypeTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
		super(pGenerator, "forge", existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(ModEntityTags.FORGE_BOSSES).add(EntityType.WITHER, EntityType.ENDER_DRAGON);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

}
