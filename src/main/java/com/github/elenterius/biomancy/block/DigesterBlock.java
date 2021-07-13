package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class DigesterBlock extends MachineBlock<DigesterTileEntity> {

	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

	public static final VoxelShape SHAPE_UP = createVoxelShape(Direction.UP);
	public static final VoxelShape SHAPE_DOWN = createVoxelShape(Direction.DOWN);
	public static final VoxelShape SHAPE_NORTH = createVoxelShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createVoxelShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createVoxelShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createVoxelShape(Direction.EAST);

	public DigesterBlock(Properties builder) {
		super(builder, true);
		setDefaultState(stateContainer.getBaseState().with(POWERED, false).with(CRAFTING, false).with(FACING, Direction.UP));
	}

	private static VoxelShape createVoxelShape(Direction direction) {
		return Stream.of(
				VoxelShapeUtil.createXZRotatedTowards(direction, 4.5, 14, 4.5, 11.5, 16, 11.5),
				VoxelShapeUtil.createXZRotatedTowards(direction, 3, 4, 3, 13, 14, 13),
				VoxelShapeUtil.createXZRotatedTowards(direction, 4, 0, 4, 12, 4, 12)
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
	public DigesterTileEntity createNewTileEntity(IBlockReader worldIn) {
		return new DigesterTileEntity();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null) {
			boolean hasFuel = nbt.contains(DigesterStateData.NBT_KEY_FUEL);
			boolean hasFluid = nbt.contains(DigesterStateData.NBT_KEY_FLUID_OUT);
			if (hasFuel || hasFluid) {
				tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
				DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

				if (hasFuel) {
					CompoundNBT fuelNbt = nbt.getCompound(DigesterStateData.NBT_KEY_FUEL);
					int fuel = fuelNbt.getInt("Amount");
					String translationKey = "fluid." + fuelNbt.getString("FluidName").replace(":", ".").replace("/", ".");
					tooltip.add(new TranslationTextComponent(translationKey).appendString(String.format(": %s/%s", df.format(fuel), df.format(DigesterTileEntity.MAX_FLUID))));
				}

				if (hasFluid) {
					CompoundNBT fluidNbt = nbt.getCompound(DigesterStateData.NBT_KEY_FLUID_OUT);
					int fluid = fluidNbt.getInt("Amount");
					String translationKey = "fluid." + fluidNbt.getString("FluidName").replace(":", ".").replace("/", ".");
					tooltip.add(new TranslationTextComponent(translationKey).appendString(String.format(": %s/%s", df.format(fluid), df.format(DigesterTileEntity.MAX_FLUID))));
				}
			}
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


	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (worldIn.getGameTime() % 10L == 0 && rand.nextInt(2) == 0) {
			boolean isCrafting = stateIn.get(CRAFTING);
			if (isCrafting) {
				worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.BLOCKS, 0.3f + rand.nextFloat() * 0.2f, 0.75f + rand.nextFloat() * 0.5f, false);
			}
		}
	}

}
