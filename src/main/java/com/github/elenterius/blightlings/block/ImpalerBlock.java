package com.github.elenterius.blightlings.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ImpalerBlock extends OwnableBlock {

	public ImpalerBlock(Properties properties) {
		super(properties);
		//TODO: implement
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return null; //TODO: create tile entity
	}

}
