package com.github.elenterius.biomancy.world.mound;

import com.github.elenterius.biomancy.world.mound.decorator.ChamberDecorator;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public interface Chamber extends Shape {

	int seed();

	ChamberDecorator getDecorator();

	Vec3 origin();

	//	default boolean containsValidBlock(Level level, BlockPos pos, BlockState blockState) {
	//		if (!contains(pos)) return false;
	//		ChamberDecorator.Result result = getDecorator().isBlockPartOfDecoration(this, level, pos, blockState);
	//		return result == ChamberDecorator.Result.POSITION_IS_VALID_AND_MATERIAL_IS_INVALID;
	//	}

	default boolean contains(BlockPos pos) {
		return contains(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
	}

}
