package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

public class FleshPlantBlock extends BushBlock {
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	protected static final VoxelShape SHAPE_SMALL = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D);
	private final boolean isSmall;

	public FleshPlantBlock(boolean isSmall, Properties properties) {
		super(properties);
		this.isSmall = isSmall;
	}

	public FleshPlantBlock(Properties properties) {
		super(properties);
		this.isSmall = false;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
		if (state.is(ModBlocks.FLESH_BLOCK.get())) return true;
		if (state.is(ModBlocks.FLESH_BLOCK_SLAB.get())) return state.getValue(SlabBlock.TYPE) != SlabType.BOTTOM;
		return false;
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return ModBlocks.FLESH_PLANT_TYPE;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Vector3d vec = state.getOffset(worldIn, pos);
		return (isSmall ? SHAPE_SMALL : SHAPE).move(vec.x, vec.y, vec.z);
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}
}
