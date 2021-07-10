package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import com.github.elenterius.biomancy.tileentity.SolidifierTileEntity;
import com.github.elenterius.biomancy.tileentity.state.DigesterStateData;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
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

public class SolidifierBlock extends MachineBlock<SolidifierTileEntity> {

	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

	public static final VoxelShape SHAPE_UP = createVoxelShape(Direction.UP);
	public static final VoxelShape SHAPE_DOWN = createVoxelShape(Direction.DOWN);
	public static final VoxelShape SHAPE_NORTH = createVoxelShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createVoxelShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createVoxelShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createVoxelShape(Direction.EAST);

	public SolidifierBlock(Properties builder) {
		super(builder, true);
		setDefaultState(stateContainer.getBaseState().with(POWERED, false).with(CRAFTING, false).with(FACING, Direction.UP));
	}

	private static VoxelShape createVoxelShape(Direction direction) {
		return Stream.of(
				VoxelShapeUtil.createXZRotatedTowards(direction, 4, 5, 4, 12, 6, 12),
				VoxelShapeUtil.createXZRotatedTowards(direction, 3, 1, 3, 13, 5, 13),
				VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 5, 11, 1, 11)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POWERED, CRAFTING, FACING); //override with different facing property
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getFace());
	}

	@Nullable
	@Override
	public SolidifierTileEntity createNewTileEntity(IBlockReader worldIn) {
		return new SolidifierTileEntity();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null) {
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

			CompoundNBT fuelNbt = nbt.getCompound(DigesterStateData.NBT_KEY_FUEL);
			int fuel = fuelNbt.getInt("Amount");
			String translationKey = "fluid." + fuelNbt.getString("FluidName").replace(":", ".").replace("/", ".");
			tooltip.add(new TranslationTextComponent(translationKey).appendString(String.format(": %s/%s", df.format(fuel), df.format(DigesterTileEntity.MAX_FUEL))));
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
			case UP:
			default:
				return SHAPE_UP;
			case DOWN:
				return SHAPE_DOWN;
			case NORTH:
				return SHAPE_NORTH;
			case SOUTH:
				return SHAPE_SOUTH;
			case WEST:
				return SHAPE_WEST;
			case EAST:
				return SHAPE_EAST;
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

}
