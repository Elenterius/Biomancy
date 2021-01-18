package com.github.elenterius.blightlings.block;

import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BioConverterBlock extends ContainerBlock {

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
