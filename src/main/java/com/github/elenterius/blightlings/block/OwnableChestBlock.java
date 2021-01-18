package com.github.elenterius.blightlings.block;

import net.minecraft.block.ChestBlock;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.function.Supplier;

public class OwnableChestBlock extends ChestBlock {

	public OwnableChestBlock(Properties builder, Supplier<TileEntityType<? extends ChestTileEntity>> tileEntityTypeIn) {
		super(builder, tileEntityTypeIn);
		//TODO: implement
	}
}
