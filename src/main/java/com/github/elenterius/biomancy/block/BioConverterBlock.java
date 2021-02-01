package com.github.elenterius.biomancy.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BioConverterBlock extends OwnableContainerBlock {

	protected BioConverterBlock(Properties builder) {
		super(builder);
		//TODO: implement
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return null;
	}

}
