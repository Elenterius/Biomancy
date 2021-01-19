package com.github.elenterius.blightlings.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BioDecomposerBlock extends OwnableContainerBlock {

	protected BioDecomposerBlock(Properties builder) {
		super(builder);
		//TODO: implement
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return null;
	}

}
