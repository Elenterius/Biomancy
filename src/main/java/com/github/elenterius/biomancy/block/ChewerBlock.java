package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.ChewerTileEntity;
import com.github.elenterius.biomancy.tileentity.state.ChewerStateData;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Stream;

public class ChewerBlock extends MachineBlock<ChewerTileEntity> {

	public static final VoxelShape NORTH_SHAPE = createVoxelShape(Direction.NORTH);
	public static final VoxelShape SOUTH_SHAPE = createVoxelShape(Direction.SOUTH);
	public static final VoxelShape EAST_SHAPE = createVoxelShape(Direction.EAST);
	public static final VoxelShape WEST_SHAPE = createVoxelShape(Direction.WEST);

	public ChewerBlock(Properties builder) {
		super(builder);
	}

	private static VoxelShape createVoxelShape(Direction direction) {
		AxisAlignedBB aabb0 = VoxelShapeUtil.createUnitAABB(0, 0, 3, 16, 14, 16);
		AxisAlignedBB aabb1 = VoxelShapeUtil.createUnitAABB(4, 14, 4, 12, 16, 12);
		AxisAlignedBB aabb2 = VoxelShapeUtil.createUnitAABB(3, 1, 0, 13, 10, 3);
		return Stream.of(VoxelShapeUtil.rotateYTo(direction, aabb0), VoxelShapeUtil.rotateYTo(direction, aabb1), VoxelShapeUtil.rotateYTo(direction, aabb2)).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
	}

	@Nullable
	@Override
	public ChewerTileEntity createNewTileEntity(IBlockReader worldIn) {
		return new ChewerTileEntity();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null && nbt.contains(ChewerStateData.NBT_KEY_FUEL)) {
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

			CompoundNBT fuelNbt = nbt.getCompound(ChewerStateData.NBT_KEY_FUEL);
			int fuel = fuelNbt.getInt("Amount");
			String translationKey = "fluid." + fuelNbt.getString("FluidName").replace(":", ".").replace("/", ".");
			tooltip.add(new TranslationTextComponent(translationKey).appendString(String.format(": %s/%s", df.format(fuel), df.format(ChewerTileEntity.MAX_FUEL))));
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction facing = state.get(HORIZONTAL_FACING);
		switch (facing) {
			case NORTH:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case WEST:
				return WEST_SHAPE;
			case EAST:
				return EAST_SHAPE;
		}
		return VoxelShapes.fullCube();
	}
}
