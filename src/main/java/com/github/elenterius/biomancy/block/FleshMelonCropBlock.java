package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class FleshMelonCropBlock extends FleshCropBlock {

	public static final VoxelShape SHAPE_0 = Block.makeCuboidShape(0.5D, 0.0D, 0.5D, 15.5D, 2.0D, 15.5D);
	public static final VoxelShape SHAPE_1 = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D);
	public static final VoxelShape SHAPE_2 = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);
	public static final VoxelShape SHAPE_3 = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	public static final VoxelShape[] SHAPES = new VoxelShape[]{SHAPE_0, SHAPE_1, SHAPE_1, SHAPE_1, SHAPE_2, SHAPE_2, SHAPE_2, SHAPE_3};

	public FleshMelonCropBlock(Properties builder) {
		super(builder);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPES[getAge(state)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		int age = getAge(state);
		return age > 0 ? SHAPES[age] : VoxelShapes.empty();
	}

	@Override
	public IItemProvider getSeedsItem() {
		return ModItems.FLESH_MELON_SEEDS.get();
	}

}
